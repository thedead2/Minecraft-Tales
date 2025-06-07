package de.thedead2.minecraft_tales.data.story.nodes.conditions;

import de.thedead2.minecraft_tales.data.story.StoryHolder;
import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;
import de.thedead2.minecraft_tales.data.story.nodes.StoryNode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public final class LogicNode extends ConditionNode {
    private final Type logicType;
    private final List<UUID> conditions;


    public LogicNode(UUID nodeId, String simpleName, String shortDescription, Type logicType, List<UUID> children, List<UUID> conditions) {
        super(nodeId, simpleName, shortDescription, children);
        this.logicType = logicType;
        this.conditions = List.copyOf(conditions);
    }


    @Override
    public LogicNodeExecutor executor(StoryProgressHandler progressHandler) {
        List<ConditionNodeExecutor<?>> conditionNodes = new ArrayList<>();
        StoryHolder storyHolder = progressHandler.getStoryHolder();

        for (UUID condition : this.conditions) {
            StoryNode node = storyHolder.getNodeForId(condition);

            if(node instanceof ConditionNode conditionNode) {
                conditionNodes.add(conditionNode.executor(progressHandler));
            }
            else throw new IllegalArgumentException("Invalid condition reference: " + condition);
        }

        return new LogicNodeExecutor(this, conditionNodes);
    }


    public Type getLogicType() {
        return logicType;
    }


    public List<UUID> getConditions() {
        return conditions;
    }


    public enum Type {
        AND, OR, XOR, NOT
    }


    public static class LogicNodeExecutor extends ConditionNodeExecutor<LogicNode> {

        private final List<ConditionNodeExecutor<? extends ConditionNode>> executors;


        public LogicNodeExecutor(LogicNode node, List<ConditionNodeExecutor<? extends ConditionNode>> conditions) {
            super(node);
            this.executors = List.copyOf(conditions);
        }


        @Override
        public void execute(StoryProgressHandler progressHandler) {
            for (ConditionNodeExecutor<? extends ConditionNode> executor : executors) {
                executor.execute(progressHandler);
            }
        }

        @Override
        public void finish(StoryProgressHandler progressHandler) {
            for (ConditionNodeExecutor<? extends ConditionNode> executor : executors) {
                executor.finish(progressHandler);
            }
        }

        @Override
        public void update(StoryProgressHandler progressHandler) {
            for (ConditionNodeExecutor<? extends ConditionNode> executor : executors) {
                executor.update(progressHandler);
            }
        }

        @Override
        public void cancel(StoryProgressHandler progressHandler) {
            for (ConditionNodeExecutor<? extends ConditionNode> executor : executors) {
                executor.cancel(progressHandler);
            }
        }

        @Override
        public boolean isConditionMet() {
            long met = executors.stream()
                                .filter(ConditionNodeExecutor::isConditionMet)
                                .count();

            return switch (node.getLogicType()) {
                case AND -> met == executors.size();
                case OR  -> met >= 1;
                case XOR -> met == 1;
                case NOT -> met == 0;
            };
        }


    }
}
