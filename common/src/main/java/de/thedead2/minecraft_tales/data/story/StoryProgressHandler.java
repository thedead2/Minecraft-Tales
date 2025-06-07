package de.thedead2.minecraft_tales.data.story;

import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.MTMainInitiator;
import de.thedead2.minecraft_tales.data.story.nodes.conditions.ConditionNode;
import de.thedead2.minecraft_tales.data.story.nodes.conditions.EventListenerNode;
import de.thedead2.minecraft_tales.data.story.nodes.StoryNode;
import de.thedead2.minecraft_tales.data.story.timer.TickTimer;
import de.thedead2.minecraft_tales.data.story.timer.TimeKeeper;
import de.thedead2.minecraft_tales.data.story.variables.SavableVariable;
import de.thedead2.minecraft_tales.event.MTEventBus;
import de.thedead2.minecraft_tales.event.MTEventListener;
import de.thedead2.minecraft_tales.event.types.MTEvent;
import de.thedead2.minecraft_tales.network.packets.ClientUpdateQuestOverlayPacket;
import de.thedead2.minecraft_tales.player.MTPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;


public class StoryProgressHandler {

    //TODO: Maybe add NodeState (ACTIVE; DONE; etc.)

    private final MTPlayer player;
    private final MTEventBus eventBus;
    private final TimeKeeper timeKeeper;
    private final StoryHolder storyHolder;

    private final Queue<StoryNode> scheduledNodes;
    private final Queue<StoryNode.NodeExecutor<?>> activeNodes;

    //TODO: Move to StoryProgress:
    private final Set<UUID> completedNodes;
    private final Map<UUID, SavableVariable<?>> variables;

    public StoryProgressHandler(UUID playerID, StoryHolder storyHolder) {
        this.player = MTMainInitiator.getModInstance().getPlayerData(playerID);
        this.timeKeeper = new TimeKeeper(playerID);
        this.storyHolder = storyHolder;
        this.eventBus = MTMainInitiator.getModInstance().getEventBus();
        this.scheduledNodes = new PriorityBlockingQueue<>();
        this.activeNodes = new PriorityBlockingQueue<>();
        this.variables = new HashMap<>();
        this.completedNodes = new HashSet<>();
    }

    public void scheduleNode(StoryNode currentNode) {
        if(this.completedNodes.contains(currentNode.getNodeId())) {
            throw new IllegalStateException("Can't schedule Node " + currentNode.getNodeId() + " as it has been already completed!");
        }
        this.scheduledNodes.add(currentNode);
    }


    public <T extends MTEvent> MTEventListener.RegisteredListener<T> registerListener(EventListenerNode.ListenerNodeExecutor<T> listener) {
        return this.eventBus.registerListener(listener.getNode().getEventType(), event -> {
            listener.invoke(event);

            this.checkCondition(listener);
        });
    }

    public <T extends MTEvent> boolean unregisterListener(EventListenerNode.ListenerNodeExecutor<T> listener) {
        return this.eventBus.unregisterListener(listener.getNode().getEventType(), listener.getRegListener());
    }

    public void checkCondition(ConditionNode.ConditionNodeExecutor<? extends ConditionNode> node) {
        if (node.isConditionMet()) {
            this.finishNode(node);
            this.activeNodes.remove(node);

            this.run();
        }
    }

    public void start(Collection<StoryNode> nodes) {
        this.scheduleNodes(nodes);
        this.run();
    }

    public void run() {
        boolean repeat;

        do {
            repeat = false;

            while (!this.scheduledNodes.isEmpty()) {
                StoryNode currentNode = this.scheduledNodes.poll();
                StoryNode.NodeExecutor<?> nodeExecutor = currentNode.executor(this);

                nodeExecutor.execute(this);

                this.activeNodes.add(nodeExecutor);
            }

            var it = this.activeNodes.iterator();

            while (it.hasNext()) { //TODO: Replace with thread save alternative
                StoryNode.NodeExecutor<?> currentNode = it.next();

                currentNode.update(this);

                if(currentNode.shouldProceed()) {
                    this.finishNode(currentNode);
                    repeat = true;

                    it.remove();
                }
            }
        }
        while (repeat);
    }

    private void finishNode(StoryNode.NodeExecutor<?> currentNode) {
        currentNode.finish(this);
        StoryNode node = currentNode.getNode();

        MTGlobalConstants.LOGGER.debug("Node {} has been completed! Scheduling children: {}", node.getNodeId(), node.getChildren());

        this.scheduleNodes(this.storyHolder.getChildrenForNode(node));
        this.completedNodes.add(node.getNodeId());
    }

    public StoryProgress save() {
        return null;
    }

    public void load(StoryProgress progress) {
        //this.start();
    }


    public void scheduleNodes(Collection<StoryNode> children) {
        for (StoryNode child : children) {
            this.scheduleNode(child);
        }
    }


    public SavableVariable<?> getVariable(UUID uuid) {
        return this.variables.get(uuid);
    }

    public void setVariable(SavableVariable<?> variable) {
        this.variables.put(variable.uuid(), variable);
    }


    public MTPlayer getPlayer() {
        return this.player;
    }


    public StoryHolder getStoryHolder() {
        return storyHolder;
    }


    public void stop() {
        this.save();
        this.timeKeeper.stopGracefully();
    }


    public void registerTimer(ResourceLocation objectiveId, long duration, Runnable onFinish) {
        Consumer<TickTimer> updateListener = tickTimer -> {
            MTGlobalConstants.PLATFORM.getNetworkHandler().sendToClient(new ClientUpdateQuestOverlayPacket(objectiveId, tickTimer.formatTimeLeft()), (ServerPlayer) this.player.getPlayer());
        };

        this.timeKeeper.startListening(
                objectiveId,
                new TickTimer(20, 0, duration, false).addUpdateListener(updateListener),
                onFinish
        );
    }
}
