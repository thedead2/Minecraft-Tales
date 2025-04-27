package de.thedead2.minecraft_tales.event.types;

import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.nio.file.Path;


public abstract class PlayerSaveEvent extends PlayerEvent {

    private final File playerFile;

    public PlayerSaveEvent(Player player, File playerFile) {
        super(player);
        this.playerFile = playerFile;
    }


    public File getPlayerFile() {
        return playerFile;
    }

    public Path getPlayerSavePath() {
        Path path = playerFile.toPath();

        return path.subpath(0, path.getNameCount() - 1);

    }
}
