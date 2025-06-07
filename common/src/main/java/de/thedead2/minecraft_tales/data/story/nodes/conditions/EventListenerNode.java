package de.thedead2.minecraft_tales.data.story.nodes.conditions;

import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;
import de.thedead2.minecraft_tales.data.trigger.ConditionTrigger;
import de.thedead2.minecraft_tales.event.MTEventListener;
import de.thedead2.minecraft_tales.event.types.MTEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;


public class EventListenerNode<T extends MTEvent> extends ConditionNode {

    private final Class<T> eventType;
    private final ConditionTrigger<T, ?, ?> trigger;

    public EventListenerNode(UUID nodeId, String simpleName, String shortDescription, List<UUID> children, Class<T> eventType, ConditionTrigger<T, ?, ?> trigger) {
        super(nodeId, simpleName, shortDescription, children);
        this.eventType = eventType;
        this.trigger = trigger;
    }

    public Class<T> getEventType() {
        return eventType;
    }


    public ConditionTrigger<T, ?, ?> getTrigger() {
        return trigger;
    }


    @Override
    public ListenerNodeExecutor<T> executor(StoryProgressHandler progressHandler) {
        return new ListenerNodeExecutor<>(this);
    }


    public static class ListenerNodeExecutor<T extends MTEvent> extends ConditionNodeExecutor<EventListenerNode<T>> implements MTEventListener<T> {

        @Nullable
        private MTEventListener.RegisteredListener<T> registeredListener;

        private boolean complete;


        public ListenerNodeExecutor(EventListenerNode<T> node) {
            super(node);
        }


        @Override
        public boolean isConditionMet() {
            return complete;
        }


        @Override
        public void execute(StoryProgressHandler progressHandler) {
            this.registeredListener = progressHandler.registerListener(this);
        }


        @Override
        public void finish(StoryProgressHandler progressHandler) {
            progressHandler.unregisterListener(this);
        }


        @Override
        public void update(StoryProgressHandler progressHandler) {

        }


        @Override
        public void cancel(StoryProgressHandler progressHandler) {
            this.finish(progressHandler);
        }


        @Override
        public void invoke(T event) {
            this.complete = this.node.getTrigger().onEvent(event);
        }


        public MTEventListener.RegisteredListener<T> getRegListener() {
            return this.registeredListener;
        }
    }
}
