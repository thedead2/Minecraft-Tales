package de.thedead2.minecraft_tales.network;

import de.thedead2.minecraft_tales.util.helper.SerializationHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;


public interface MTNetworkPacket {

    StreamCodec<ByteBuf, MTNetworkPacket> PACKET_CODEC = SerializationHelper.streamCodecFromRegistry(MTNetworkHandler.PACKET_TYPE_REGISTRY).dispatch(MTNetworkPacket::getType, MTPacketType::codec);

    default Runnable onClient() {
        return () -> {};
    }

    default Runnable onServer() {
        return () -> {};
    }

    MTPacketType<?> getType();
}
