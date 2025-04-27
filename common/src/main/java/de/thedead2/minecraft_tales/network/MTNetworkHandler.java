package de.thedead2.minecraft_tales.network;

import com.mojang.serialization.Lifecycle;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;


public abstract class MTNetworkHandler {
    protected static String PROTOCOL_VERSION = "1";

    static Registry<MTPacketType<?>> PACKET_TYPE_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "mt_network_packet_types")), Lifecycle.stable());

    private static <MSG extends MTNetworkPacket> MTPacketType<MSG> registerPacketType(String id, StreamCodec<ByteBuf, MSG> codec) {
        return Registry.register(PACKET_TYPE_REGISTRY, ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "network/" + id), new MTPacketType<>(codec));
    }

    public <MSG extends MTNetworkPacket> MTPacketType<MSG> registerPacket(@NotNull Class<MSG> packetClass, StreamCodec<ByteBuf, MSG> packetCodec) {
        this.registerPacket(packetClass);
        return registerPacketType(packetClass.getName(), packetCodec);
    }

    protected abstract <MSG extends MTNetworkPacket> void registerPacket(@NotNull Class<MSG> packetClass);

    public abstract <MSG extends MTNetworkPacket> void sendToServer(@NotNull MSG message);

    public abstract <MSG extends MTNetworkPacket> void sendToClient(@NotNull MSG message, @NotNull ServerPlayer player);

    public abstract <MSG extends MTNetworkPacket> void handlePacket(@NotNull MSG message);
}
