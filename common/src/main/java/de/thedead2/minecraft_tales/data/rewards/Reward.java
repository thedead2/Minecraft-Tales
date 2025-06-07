package de.thedead2.minecraft_tales.data.rewards;

import com.mojang.serialization.Codec;
import de.thedead2.minecraft_tales.player.MTPlayer;


public interface Reward {
    Codec<Reward> CODEC = RewardType.TYPE_REGISTRY.byNameCodec().dispatch("type", Reward::getType, RewardType::codec);

    RewardType<?> getType();

    void rewardPlayer(MTPlayer player);
}
