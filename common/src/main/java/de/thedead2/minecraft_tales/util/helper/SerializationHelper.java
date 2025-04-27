package de.thedead2.minecraft_tales.util.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;


public class SerializationHelper {

    private SerializationHelper() {}

    public static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(string -> {
        try {
            return DataResult.success(UUID.fromString(string), Lifecycle.stable());
        } catch (IllegalArgumentException e) {
            return DataResult.error(() -> "Invalid UUID " + string + ": " + e.getMessage());
        }
    }, UUID::toString);


    public static final Codec<LocalDateTime> DATE_TIME_CODEC = Codec.STRING.comapFlatMap(string -> {
        try {
            return DataResult.success(MTGlobalConstants.DATE_TIME_FORMATTER.parse(string, LocalDateTime::from), Lifecycle.stable());
        }
        catch (IllegalArgumentException e) {
            return DataResult.error(() -> "Invalid LocalDateTime " + string + ": " + e.getMessage());
        }
    }, MTGlobalConstants.DATE_TIME_FORMATTER::format);


    public static final Codec<Path> PATH_CODEC = Codec.STRING.xmap(Path::of, Path::toString);


    public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> enumClass) {
        return Codec.STRING.xmap(name -> Enum.valueOf(enumClass, name), Enum::name);
    }

    public static <T> StreamCodec<ByteBuf, T> streamCodecFromRegistry(Registry<T> registry) {
        return ResourceLocation.STREAM_CODEC.map(id -> {
            T value = registry.get(id);

            if (value == null) {
                throw new IllegalArgumentException("Unknown registry entry with id: " + id);
            }

            return value;
        }, value -> {
            ResourceLocation id = registry.getKey(value);

            if (id == null) {
                throw new IllegalStateException("Unknown registry entry for value: " + value);
            }

            return id;
        });
    }
}
