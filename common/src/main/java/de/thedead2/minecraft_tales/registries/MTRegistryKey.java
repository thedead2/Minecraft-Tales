package de.thedead2.minecraft_tales.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;


public record MTRegistryKey<T>(ResourceKey<DynamicRegistry<T>> registry, ResourceLocation key) {

    public static <T> Codec<MTRegistryKey<T>> codec(Codec<T> codec) {
        return RecordCodecBuilder.create((instance) -> instance.group(
                RegistryKeys.getResourceKeyCodec(codec).fieldOf("registry").forGetter(MTRegistryKey::registry),
                ResourceLocation.CODEC.fieldOf("key").forGetter(MTRegistryKey::key)
        ).apply(instance, MTRegistryKey::new));
    }

    @SuppressWarnings("unchecked")
    public static <T> MTRegistryKey<T> fromSavePath(Path registrySavePath, Path filePath){
        Path fileName = registrySavePath.relativize(filePath);
        Path registryName = MTGlobalConstants.SAVE_DIR.resolve("dynamic_registries").relativize(registrySavePath);

        String registry = registryName.toString();
        String file = fileName.toString();

        var regKey = (ResourceKey<DynamicRegistry<T>>) (Object) DynamicRegistries.createRegistryKey(registry);

        return new MTRegistryKey<>(regKey, ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, file));
    }
}
