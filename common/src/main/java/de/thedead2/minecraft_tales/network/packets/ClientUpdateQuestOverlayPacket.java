package de.thedead2.minecraft_tales.network.packets;

import de.thedead2.minecraft_tales.network.MTNetworkPacket;
import de.thedead2.minecraft_tales.network.MTPacketType;
import net.minecraft.resources.ResourceLocation;


public class ClientUpdateQuestOverlayPacket implements MTNetworkPacket {

    public ClientUpdateQuestOverlayPacket(ResourceLocation objectiveId, String formattedTimeLeft) {

    }


    @Override
    public MTPacketType<ClientUpdateQuestOverlayPacket> getType() {
        return null;
    }
}
