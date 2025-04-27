package de.thedead2.minecraft_tales.platform;

import de.thedead2.minecraft_tales.api.services.IPlatformHelper;
import de.thedead2.minecraft_tales.network.MTNetworkHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;
import java.util.Optional;


public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }


    @Override
    public boolean isDevEnvironment() {
        return false;
    }


    @Override
    public boolean isClient() {
        return false;
    }


    @Override
    public boolean isServer() {
        return false;
    }


    @Override
    public Path getGameDirectory() {
        return null;
    }


    @Override
    public Optional<MinecraftServer> getServer() {
        return Optional.empty();
    }


    @Override
    public MTNetworkHandler getNetworkHandler() {
        return null;
    }
}
