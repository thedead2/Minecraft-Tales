package de.thedead2.minecraft_tales.platform;

import de.thedead2.minecraft_tales.api.services.MTInstance;
import de.thedead2.minecraft_tales.event.MTEventBus;
import de.thedead2.minecraft_tales.player.PlayerDataManager;
import de.thedead2.minecraft_tales.player.MTPlayer;
import de.thedead2.minecraft_tales.player.PlayerTeam;
import de.thedead2.minecraft_tales.util.helper.IOHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

import static de.thedead2.minecraft_tales.MTGlobalConstants.*;


public class MTServerInstance implements MTInstance {

    private final MinecraftServer server;
    private final PlayerDataManager playerDataManager;
    private final MTEventBus eventBus;

    public MTServerInstance(MinecraftServer server) {
        this.server = server;
        this.playerDataManager = new PlayerDataManager();
        this.eventBus = new MTEventBus();
    }

    @Override
    public void init() {
        initDirectories();
    }


    @Override
    public @Nullable MTPlayer getPlayerData(UUID uuid) {
        return this.playerDataManager.getPlayerData(uuid);
    }


    @Override
    @Nullable
    public Player getPlayer(UUID uuid) {
        return server.getPlayerList().getPlayer(uuid);
    }


    @Override
    public MTEventBus getEventBus() {
        return this.eventBus;
    }


    @Override
    @Nullable
    public PlayerTeam getTeam(ResourceLocation teamId) {
        return this.playerDataManager.getTeam(teamId);
    }


    private void initDirectories() {
        IOHelper.createDirectory(JOURNAL_DIR.toFile());
        IOHelper.createDirectory(QUESTS_DIR.toFile());
        IOHelper.createDirectory(STORY_DIR.toFile());
    }


    public MinecraftServer getServer() {
        return server;
    }


    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
