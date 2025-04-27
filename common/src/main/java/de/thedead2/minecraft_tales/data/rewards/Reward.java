package de.thedead2.minecraft_tales.data.rewards;

import com.mojang.serialization.Codec;


public interface Reward {
    Codec<Reward> CODEC = RewardType.TYPE_REGISTRY.byNameCodec().dispatch("type", Reward::getType, RewardType::codec);

    RewardType<?> getType();
}
