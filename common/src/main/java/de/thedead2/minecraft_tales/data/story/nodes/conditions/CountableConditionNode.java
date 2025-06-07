package de.thedead2.minecraft_tales.data.story.nodes.conditions;

import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;
import de.thedead2.minecraft_tales.data.story.nodes.StoryNode;

import java.util.List;
import java.util.UUID;


public final class CountableConditionNode extends ConditionNode {

    private final UUID conditionId;
    private final int amount;

    public CountableConditionNode(UUID nodeId, String simpleName, String shortDescription, List<UUID> children, UUID conditionId, int amount) {
        super(nodeId, simpleName, shortDescription, children);
        this.conditionId = conditionId;
        this.amount = amount;
    }

    public UUID getConditionId() {
        return conditionId;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public CountableConditionExecutor executor(StoryProgressHandler handler) {
        StoryNode node = handler.getStoryHolder().getNodeForId(this.conditionId);

        if (!(node instanceof ConditionNode conditionNode)) {
            throw new IllegalArgumentException("Invalid condition reference: " + this.conditionId);
        }

        return new CountableConditionExecutor(this, conditionNode.executor(handler));
    }


    public static class CountableConditionExecutor extends ConditionNode.ConditionNodeExecutor<CountableConditionNode> {

        private final ConditionNode.ConditionNodeExecutor<? extends ConditionNode> conditionExecutor;
        private int count = 0;

        public CountableConditionExecutor(CountableConditionNode node, ConditionNode.ConditionNodeExecutor<? extends ConditionNode> conditionExecutor) {
            super(node);
            this.conditionExecutor = conditionExecutor;
        }

        @Override
        public void execute(StoryProgressHandler handler) {
            this.conditionExecutor.execute(handler);
        }

        @Override
        public void finish(StoryProgressHandler handler) {
            this.conditionExecutor.finish(handler);
        }

        @Override
        public void update(StoryProgressHandler handler) {
            this.conditionExecutor.update(handler);

            if (this.conditionExecutor.isConditionMet()) {
                this.count++;
            }
        }

        @Override
        public void cancel(StoryProgressHandler handler) {
            this.conditionExecutor.cancel(handler);
        }

        @Override
        public boolean isConditionMet() {
            return this.count >= this.node.getAmount();
        }
    }
}
