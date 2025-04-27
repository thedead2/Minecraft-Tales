package de.thedead2.minecraft_tales.api.services;

import de.thedead2.minecraft_tales.api.GameSide;
import de.thedead2.minecraft_tales.network.MTNetworkHandler;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;
import java.util.Optional;


public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevEnvironment();

    boolean isClient();

    boolean isServer();

    default GameSide getGameSide() {
        if(isClient()) {
            return GameSide.CLIENT;
        }
        else return GameSide.SERVER;
    }

    Path getGameDirectory();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevEnvironment() ? "development" : "production";
    }


    Optional<MinecraftServer> getServer();

    MTNetworkHandler getNetworkHandler();
}
