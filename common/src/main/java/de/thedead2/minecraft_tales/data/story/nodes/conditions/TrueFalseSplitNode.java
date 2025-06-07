package de.thedead2.minecraft_tales.data.story.nodes.conditions;

import de.thedead2.minecraft_tales.data.story.StoryHolder;
import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;
import de.thedead2.minecraft_tales.data.story.nodes.StoryNode;

import java.util.List;
import java.util.UUID;


public class TrueFalseSplitNode extends ConditionNode {

    private final UUID conditionId;
    private final List<UUID> onTrue;
    private final List<UUID> onFalse;

    public TrueFalseSplitNode(UUID nodeId, String simpleName, String shortDescription, UUID condition, List<UUID> onTrue, List<UUID> onFalse) {
        super(nodeId, simpleName, shortDescription, List.of());
        this.conditionId = condition;
        this.onTrue = List.copyOf(onTrue);
        this.onFalse = List.copyOf(onFalse);
    }


    @Override
    public TrueFalseExecutor executor(StoryProgressHandler progressHandler) {
        StoryNode node = progressHandler.getStoryHolder().getNodeForId(this.conditionId);

        if (!(node instanceof ConditionNode conditionNode)) {
            throw new IllegalArgumentException("Invalid condition reference: " + this.conditionId);
        }

        return new TrueFalseExecutor(this, conditionNode.executor(progressHandler));
    }


    public static class TrueFalseExecutor extends ConditionNodeExecutor<TrueFalseSplitNode> {

        private final ConditionNodeExecutor<?> conditionExecutor;


        public TrueFalseExecutor(TrueFalseSplitNode node, ConditionNodeExecutor<?> conditionExecutor) {
            super(node);
            this.conditionExecutor = conditionExecutor;
        }


        @Override
        public boolean isConditionMet() {
            return true;
        }


        @Override
        public void execute(StoryProgressHandler progressHandler) {
            this.conditionExecutor.execute(progressHandler);
        }


        @Override
        public void finish(StoryProgressHandler progressHandler) {
            this.conditionExecutor.finish(progressHandler);

            StoryHolder storyHolder = progressHandler.getStoryHolder();

            if (this.conditionExecutor.isConditionMet()) {
                progressHandler.scheduleNodes(storyHolder.getNodesForIds(this.node.onTrue));
            }
            else {
                progressHandler.scheduleNodes(storyHolder.getNodesForIds(this.node.onFalse));
            }
        }


        @Override
        public void update(StoryProgressHandler progressHandler) {
            this.conditionExecutor.update(progressHandler);
        }


        @Override
        public void cancel(StoryProgressHandler progressHandler) {
            this.conditionExecutor.cancel(progressHandler);
        }
    }
}
