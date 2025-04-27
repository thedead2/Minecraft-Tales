package de.thedead2.minecraft_tales.api.services;

import de.thedead2.minecraft_tales.event.MTEventBus;
import de.thedead2.minecraft_tales.player.MTPlayer;
import de.thedead2.minecraft_tales.player.PlayerTeam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;


public interface MTInstance {

    void init();

    @Nullable
    MTPlayer getPlayerData(UUID uuid);

    @Nullable
    Player getPlayer(UUID uuid);

    MTEventBus getEventBus();

    @Nullable
    PlayerTeam getTeam(ResourceLocation teamId);
}
