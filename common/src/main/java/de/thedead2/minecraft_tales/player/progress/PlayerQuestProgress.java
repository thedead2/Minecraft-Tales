package de.thedead2.minecraft_tales.player.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.data.quests.StoryQuest;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;


public class PlayerQuestProgress {

    public static final Codec<PlayerQuestProgress> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, StoryQuest.Progress.CODEC).fieldOf("questProgress").forGetter(PlayerQuestProgress::getQuestProgress)
    ).apply(instance, PlayerQuestProgress::new));

    private final Map<ResourceLocation, StoryQuest.Progress> questProgress;

    public PlayerQuestProgress() {
        questProgress = new HashMap<>();
    }

    private PlayerQuestProgress(Map<ResourceLocation, StoryQuest.Progress> questProgress) {
        this.questProgress = questProgress;
    }

    public StoryQuest.Progress getOrStartProgress(StoryQuest quest) {
        return this.questProgress.computeIfAbsent(quest.getId(), StoryQuest.Progress::new);
    }

    private Map<ResourceLocation, StoryQuest.Progress> getQuestProgress() {
        return questProgress;
    }


    public void copyFrom(PlayerQuestProgress questProgress) {
        this.questProgress.clear();
        this.questProgress.putAll(questProgress.getQuestProgress());
    }
}
