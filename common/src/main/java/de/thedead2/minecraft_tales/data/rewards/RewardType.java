package de.thedead2.minecraft_tales.data.rewards;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public record RewardType<T extends Reward>(MapCodec<T> codec) {

    static final Registry<RewardType<?>> TYPE_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "reward_types_reg")), Lifecycle.stable());





    public static <T extends Reward> RewardType<T> register(String id, MapCodec<T> codec) {
        return Registry.register(TYPE_REGISTRY, ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, id), new RewardType<>(codec));
    }
}
