package de.thedead2.minecraft_tales.platform;

import de.thedead2.minecraft_tales.api.services.IPlatformHelper;
import de.thedead2.minecraft_tales.network.MTNetworkHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.nio.file.Path;
import java.util.Optional;


public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }


    @Override
    public boolean isDevEnvironment() {
        return false;
    }

    @Override
    public boolean isClient() {
        return EffectiveSide.get().isClient();
    }


    @Override
    public boolean isServer() {
        return EffectiveSide.get().isServer();
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
