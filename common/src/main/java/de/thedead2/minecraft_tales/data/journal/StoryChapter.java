package de.thedead2.minecraft_tales.data.journal;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.data.rewards.Reward;
import net.minecraft.resources.ResourceLocation;

import java.util.List;


public class StoryChapter extends JournalChapter {

    public static final MapCodec<StoryChapter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            JournalChapter.PartialSerializationResult.CODEC.fieldOf("common").forGetter(StoryChapter::wrapPartial),
            Reward.CODEC.listOf().fieldOf("rewards").forGetter(StoryChapter::getRewards)
    ).apply(instance, (partialSerializationResult, rewards) -> new StoryChapter(partialSerializationResult.title(), partialSerializationResult.id(), partialSerializationResult.icon(), partialSerializationResult.entries(), rewards)));


    private final List<Reward> rewards;

    public StoryChapter(String title, ResourceLocation id, ResourceLocation icon, List<JournalEntry> entries, List<Reward> rewards) {
        super(title, id, icon, entries);
        this.rewards = rewards;
    }


    @Override
    public JournalType<?> getType() {
        return JournalType.STORY_CHAPTER;
    }


    public ImmutableList<Reward> getRewards() {
        return ImmutableList.copyOf(rewards);
    }
}
