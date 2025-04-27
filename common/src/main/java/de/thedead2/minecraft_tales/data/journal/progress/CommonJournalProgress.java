package de.thedead2.minecraft_tales.data.journal.progress;

import com.mojang.serialization.MapCodec;
import de.thedead2.minecraft_tales.data.journal.JournalEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;


public final class CommonJournalProgress extends JournalProgress {

    public static final MapCodec<CommonJournalProgress> CODEC = PartialSerializationResult.CODEC.xmap(
            partialSerializationResult -> new CommonJournalProgress(partialSerializationResult.chapterId(), partialSerializationResult.visibleEntries()),
            commonProgress -> new PartialSerializationResult(commonProgress.chapterId(), commonProgress.visibleEntries())
    );


    public CommonJournalProgress(ResourceLocation chapterId) {
        super(chapterId);
    }


    public CommonJournalProgress(ResourceLocation chapterId, List<JournalEntry> visibleEntries) {
        super(chapterId, visibleEntries);
    }


    @Override
    public JournalProgressType<?> getType() {
        return null;
    }
}
