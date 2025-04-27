package de.thedead2.minecraft_tales.player.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.data.journal.JournalChapter;
import de.thedead2.minecraft_tales.data.journal.StoryChapter;
import de.thedead2.minecraft_tales.data.journal.progress.CommonJournalProgress;
import de.thedead2.minecraft_tales.data.journal.progress.JournalProgress;
import de.thedead2.minecraft_tales.data.journal.progress.StoryJournalProgress;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;


public class PlayerJournalProgress {

    public static final Codec<PlayerJournalProgress> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, JournalProgress.CODEC).fieldOf("journalProgress").forGetter(PlayerJournalProgress::getJournalProgress)
    ).apply(instance, PlayerJournalProgress::new));

    private final Map<ResourceLocation, JournalProgress> journalProgress;

    public PlayerJournalProgress() {
        journalProgress = new HashMap<>();
    }

    public PlayerJournalProgress(Map<ResourceLocation, JournalProgress> journalProgress) {
        this.journalProgress = journalProgress;
    }

    public JournalProgress getOrStartProgress(JournalChapter chapter) {
        return this.journalProgress.computeIfAbsent(chapter.getId(), chapterId -> {
            if(!(chapter instanceof StoryChapter)) {
                return new CommonJournalProgress(chapterId);
            }
            else return new StoryJournalProgress(chapterId);
        });
    }

    private Map<ResourceLocation, JournalProgress> getJournalProgress() {
        return journalProgress;
    }


    public void copyFrom(PlayerJournalProgress journalProgress) {
        this.journalProgress.clear();
        this.journalProgress.putAll(journalProgress.getJournalProgress());
    }
}
