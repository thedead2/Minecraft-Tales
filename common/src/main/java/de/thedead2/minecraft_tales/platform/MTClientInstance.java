package de.thedead2.minecraft_tales.platform;

import de.thedead2.minecraft_tales.api.services.MTInstance;
import de.thedead2.minecraft_tales.event.MTEventBus;
import de.thedead2.minecraft_tales.player.MTPlayer;
import de.thedead2.minecraft_tales.player.PlayerTeam;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;


public class MTClientInstance implements MTInstance {

    private MTPlayer clientData;
    private final MTEventBus eventBus;

    public MTClientInstance() {
        this.eventBus = new MTEventBus();
    }

    @Override
    public void init() {

    }


    @Override
    @Nullable
    public MTPlayer getPlayerData(UUID uuid) {
        if(clientData == null) {
            return null;
        }
        else {
            if(clientData.getUUID().equals(uuid)) {
                return clientData;
            }
            else throw new IllegalArgumentException("Tried to access clientData for other local player! Local player: " + clientData.getUUID() + " Required data: " + uuid);
        }
    }


    @Override
    @Nullable
    public Player getPlayer(UUID uuid) {
        Player player = Minecraft.getInstance().player;

        if(player == null || player.getUUID().equals(uuid)) {
            return player;
        }
        else throw new IllegalArgumentException("Tried to access other local player! Local player: " + player.getUUID() + " Required player: " + uuid);
    }


    @Override
    public MTEventBus getEventBus() {
        return eventBus;
    }


    @Override
    public @Nullable PlayerTeam getTeam(ResourceLocation teamId) {
        if (clientData != null) {
            Optional<PlayerTeam> team = clientData.getTeam();
            if (team.isPresent()) {
                if (team.get().getId().equals(teamId))
                    return team.get();
                else
                    throw new IllegalArgumentException("Local player is in team: " + team.get().getId() + " Required team: " + teamId);
            }
        }
        return null;
    }
}
