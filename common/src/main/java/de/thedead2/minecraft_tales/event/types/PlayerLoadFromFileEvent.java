package de.thedead2.minecraft_tales.event.types;

import net.minecraft.world.entity.player.Player;

import java.io.File;


public class PlayerLoadFromFileEvent extends PlayerSaveEvent {

    public PlayerLoadFromFileEvent(Player player, File playerFile) {
        super(player, playerFile);
    }
}
