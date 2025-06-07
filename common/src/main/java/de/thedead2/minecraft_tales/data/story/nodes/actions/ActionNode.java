package de.thedead2.minecraft_tales.data.story.nodes.actions;

import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;
import de.thedead2.minecraft_tales.data.story.nodes.StoryNode;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public abstract class ActionNode<T> extends StoryNode {

    public ActionNode(UUID nodeId, String simpleName, String shortDescription, List<UUID> children) {
        super(nodeId, simpleName, shortDescription, children);
    }


    public abstract ActionFactory<T> getActionFactory();


    @Override
    public ActionNodeExecutor<T, ? extends ActionNode<T>> executor(StoryProgressHandler progressHandler) {
        return new ActionNodeExecutor<>(this);
    }


    @FunctionalInterface
    public interface ActionFactory<T> {

        CompletableFuture<T> create(StoryProgressHandler progressHandler);

    }

    public static class ActionNodeExecutor<V, T extends ActionNode<V>> extends NodeExecutor<T> {

        private boolean started;
        private CompletableFuture<V> actionFuture;

        public ActionNodeExecutor(T node) {
            super(node);
        }


        @Override
        public void execute(StoryProgressHandler progressHandler) {
            if (!this.started) {
                try {
                    this.actionFuture = this.node.getActionFactory().create(progressHandler);
                }
                catch (Exception e) {
                    MTGlobalConstants.LOGGER.error("Error while creating action for node {}", this.node.getNodeId(), e);
                    this.actionFuture = CompletableFuture.failedFuture(e);
                }

                this.started = true;
            }
        }

        @Override
        public void update(StoryProgressHandler progressHandler) {}

        @Override
        public void finish(StoryProgressHandler progressHandler) {}

        @Override
        public void cancel(StoryProgressHandler progressHandler) {
            if (this.actionFuture != null && !this.actionFuture.isDone()) {
                this.actionFuture.cancel(true);
            }
        }

        @Override
        public boolean shouldProceed() {
            return this.actionFuture == null || this.actionFuture.isDone();
        }
    }
}
