package de.thedead2.minecraft_tales.data.journal.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.data.journal.JournalEntry;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;


public final class StoryJournalProgress extends JournalProgress {

    public static final MapCodec<StoryJournalProgress> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            PartialSerializationResult.CODEC.fieldOf("common").forGetter(StoryJournalProgress::wrapPartial),
            ResourceLocation.CODEC.listOf().fieldOf("completedQuests").forGetter(StoryJournalProgress::completedQuests),
            Codec.BOOL.fieldOf("rewarded").forGetter(StoryJournalProgress::rewarded)
    ).apply(instance, ((partialSerializationResult, completedQuests, rewarded) -> new StoryJournalProgress(partialSerializationResult.chapterId(), partialSerializationResult.visibleEntries(), completedQuests, rewarded))));


    private final List<ResourceLocation> completedQuests;

    private final boolean rewarded;


    public StoryJournalProgress(ResourceLocation chapterId, List<JournalEntry> visibleEntries, List<ResourceLocation> completedQuests, boolean rewarded) {
        super(chapterId, visibleEntries);
        this.completedQuests = completedQuests;
        this.rewarded = rewarded;
    }


    public StoryJournalProgress(ResourceLocation chapterId, List<JournalEntry> visibleEntries) {
        this(chapterId, visibleEntries, Lists.newArrayList(), false);
    }


    public StoryJournalProgress(ResourceLocation chapterId) {
        this(chapterId, Lists.newArrayList(), Lists.newArrayList(), false);
    }


    @Override
    public JournalProgressType<?> getType() {
        return JournalProgressType.STORY_CHAPTER_PROGRESS;
    }


    public List<ResourceLocation> completedQuests() {
        return completedQuests;
    }


    public boolean rewarded() {
        return rewarded;
    }


}
