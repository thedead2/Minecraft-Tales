package de.thedead2.minecraft_tales.data.story.nodes.conditions;

import de.thedead2.minecraft_tales.data.story.variables.SavableVariable;
import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;

import java.util.List;
import java.util.UUID;


public class CheckVariableNode<T> extends ConditionNode {

    private final SavableVariable<T> variable;


    public CheckVariableNode(UUID nodeId, String simpleName, String shortDescription, List<UUID> children, SavableVariable<T> variable) {
        super(nodeId, simpleName, shortDescription, children);
        this.variable = variable;
    }


    public SavableVariable<T> getVariable() {
        return variable;
    }

    @Override
    public CheckVariableExecutor<T> executor(StoryProgressHandler progressHandler) {
        return new CheckVariableExecutor<>(this);
    }


    public static class CheckVariableExecutor<T> extends ConditionNodeExecutor<CheckVariableNode<T>> {

        private SavableVariable<T> variableToCheck;


        public CheckVariableExecutor(CheckVariableNode<T> node) {
            super(node);
        }


        @Override
        public boolean isConditionMet() {
            return this.node.getVariable().valueEqual(this.variableToCheck);
        }


        @Override
        public void execute(StoryProgressHandler progressHandler) {
            this.update(progressHandler);
        }


        @Override
        public void finish(StoryProgressHandler progressHandler) {
        }


        @Override
        @SuppressWarnings("unchecked")
        public void update(StoryProgressHandler progressHandler) {
            this.variableToCheck = (SavableVariable<T>) progressHandler.getVariable(this.node.getVariable().uuid());
        }


        @Override
        public void cancel(StoryProgressHandler progressHandler) {

        }
    }
}
