package de.thedead2.minecraft_tales.data.story.nodes.conditions;

import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;
import de.thedead2.minecraft_tales.data.story.nodes.StoryNode;

import java.util.List;
import java.util.UUID;


public abstract class ConditionNode extends StoryNode {

    public ConditionNode(UUID nodeId, String simpleName, String shortDescription, List<UUID> children) {
        super(nodeId, simpleName, shortDescription, children);
    }


    @Override
    public abstract ConditionNodeExecutor<? extends StoryNode> executor(StoryProgressHandler progressHandler);


    public static abstract class ConditionNodeExecutor<T extends ConditionNode> extends NodeExecutor<T> {

        public ConditionNodeExecutor(T node) {
            super(node);
        }


        public abstract boolean isConditionMet();

        @Override
        public boolean shouldProceed() {
            return isConditionMet();
        }
    }
}
