package de.thedead2.minecraft_tales.data.story;

import de.thedead2.minecraft_tales.data.story.nodes.StoryNode;

import java.util.*;


public class StoryHolder {
    private final Map<UUID, StoryNode> storyNodes;
    private final UUID startNodeId;
    private final UUID endNodeId;


    public StoryHolder(Map<UUID, StoryNode> storyNodes, UUID startNodeId, UUID endNodeId) {
        this.storyNodes = storyNodes;
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
    }

    public StoryNode getNodeForId(UUID id) {
        return storyNodes.get(id);
    }

    public Collection<StoryNode> getChildrenForNode(StoryNode node) {
        return getNodesForIds(node.getChildren());
    }

    public StoryNode getStartNode() {
        return this.storyNodes.get(this.startNodeId);
    }

    public StoryNode getEndNode() {
        return this.storyNodes.get(this.endNodeId);
    }


    public List<StoryNode> getNodesForIds(List<UUID> nodeIds) {
        List<StoryNode> children = new ArrayList<>();

        for (UUID id : nodeIds) {
            children.add(this.storyNodes.get(id));
        }

        return children;
    }
}
