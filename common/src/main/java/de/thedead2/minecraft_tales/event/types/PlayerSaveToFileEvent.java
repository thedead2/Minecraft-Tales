package de.thedead2.minecraft_tales.event.types;

import net.minecraft.world.entity.player.Player;

import java.io.File;


public class PlayerSaveToFileEvent extends PlayerSaveEvent {

    private final boolean playerLoggedOut;

    public PlayerSaveToFileEvent(Player player, File playerFile, boolean playerLoggedOut) {
        super(player, playerFile);
        this.playerLoggedOut = playerLoggedOut;
    }

    public boolean isPlayerLoggedOut() {
        return playerLoggedOut;
    }
}
