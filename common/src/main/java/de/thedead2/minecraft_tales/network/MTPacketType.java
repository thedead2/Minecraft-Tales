package de.thedead2.minecraft_tales.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;


public record MTPacketType<T extends MTNetworkPacket>(StreamCodec<ByteBuf, T> codec) {
}
