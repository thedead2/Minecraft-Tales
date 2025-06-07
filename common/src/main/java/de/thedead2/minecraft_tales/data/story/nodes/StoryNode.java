package de.thedead2.minecraft_tales.data.story.nodes;


import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;

import java.util.List;
import java.util.UUID;


public abstract class StoryNode {
    private final UUID nodeId;
    private final String simpleName;
    private final String shortDescription;
    private final List<UUID> children;


    public StoryNode(UUID nodeId, String simpleName, String shortDescription, List<UUID> children) {
        this.nodeId = nodeId;
        this.simpleName = simpleName;
        this.shortDescription = shortDescription;
        this.children = List.copyOf(children);
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public List<UUID> getChildren() {
        return this.children;
    }


    public String getSimpleName() {
        return simpleName;
    }


    public String getShortDescription() {
        return shortDescription;
    }

    public abstract NodeExecutor<? extends StoryNode> executor(StoryProgressHandler progressHandler);


    public static abstract class NodeExecutor<T extends StoryNode> {

        protected final T node;


        public NodeExecutor(T node) {
            this.node = node;
        }


        public final T getNode() {
            return this.node;
        }

        public abstract void execute(StoryProgressHandler progressHandler);

        public abstract void finish(StoryProgressHandler progressHandler);

        public abstract void update(StoryProgressHandler progressHandler);

        public abstract void cancel(StoryProgressHandler progressHandler);

        public abstract boolean shouldProceed();
    }
}
