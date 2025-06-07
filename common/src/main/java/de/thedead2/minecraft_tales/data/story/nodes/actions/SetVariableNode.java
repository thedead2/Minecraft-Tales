package de.thedead2.minecraft_tales.data.story.nodes.actions;

import de.thedead2.minecraft_tales.data.story.variables.SavableVariable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class SetVariableNode<T> extends ActionNode<Void> {

    private final SavableVariable<T> variable;

    public SetVariableNode(UUID nodeId, String simpleName, String shortDescription, List<UUID> children, SavableVariable<T> variable) {
        super(nodeId, simpleName, shortDescription, children);

        this.variable = variable;
    }


    public SavableVariable<T> getVariable() {
        return variable;
    }


    @Override
    public ActionFactory<Void> getActionFactory() {
        return progressHandler -> {
            progressHandler.setVariable(this.variable);
            return CompletableFuture.completedFuture(null);
        };
    }
}
