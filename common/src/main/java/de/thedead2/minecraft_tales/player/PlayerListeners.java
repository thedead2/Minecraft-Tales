package de.thedead2.minecraft_tales.player;

import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.MTMainInitiator;
import de.thedead2.minecraft_tales.api.GameSide;
import de.thedead2.minecraft_tales.event.MTEventBus;
import de.thedead2.minecraft_tales.event.types.MTEvent;
import de.thedead2.minecraft_tales.event.types.PlayerLoadFromFileEvent;
import de.thedead2.minecraft_tales.event.types.PlayerLoggedInEvent;
import de.thedead2.minecraft_tales.event.types.PlayerSaveToFileEvent;
import de.thedead2.minecraft_tales.platform.MTServerInstance;

import java.io.IOException;


@MTEventBus.EvenBusSubscriber(GameSide.SERVER)
public class PlayerListeners {

    @MTEvent.Subscriber
    public static void onPlayerLogin(final PlayerLoggedInEvent event) {
        var modInstance = MTMainInitiator.getModInstance();

        MTPlayer mtPlayer = modInstance.getPlayerData(event.getPlayer().getUUID());
        PlayerLoginHandler loginHandler = ((MTServerInstance) modInstance).getPlayerDataManager().getPlayerLoginHandler();

        loginHandler.dispatchForPlayer(mtPlayer);
    }

    @MTEvent.Subscriber
    public static void onPlayerFileLoad(final PlayerLoadFromFileEvent event) {
        var modInstance = MTMainInitiator.getModInstance();
        PlayerDataManager playerDataManager = ((MTServerInstance) modInstance).getPlayerDataManager();

        try {
            playerDataManager.loadPlayerData(event.getPlayer(), event.getPlayerFile());
        }
        catch (IOException e) {
            MTGlobalConstants.LOGGER.error("Error while loading add player data", e);
        }
    }

    @MTEvent.Subscriber
    public static void onPlayerFileSave(final PlayerSaveToFileEvent event) {
        var modInstance = MTMainInitiator.getModInstance();
        PlayerDataManager playerDataManager = ((MTServerInstance) modInstance).getPlayerDataManager();

        try {
            playerDataManager.savePlayerData(event.getPlayer(), event.getPlayerFile());
        }
        catch (IOException e) {
            MTGlobalConstants.LOGGER.error("Error while saving additional player data", e);
        }

        if(event.isPlayerLoggedOut()) {
            playerDataManager.clearPlayerData(event.getPlayer());
        }
    }
}
