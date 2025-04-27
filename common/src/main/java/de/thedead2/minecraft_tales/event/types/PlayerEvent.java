package de.thedead2.minecraft_tales.event.types;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;


public abstract class PlayerEvent extends MTEvent {

    private final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return player;
    }
}
