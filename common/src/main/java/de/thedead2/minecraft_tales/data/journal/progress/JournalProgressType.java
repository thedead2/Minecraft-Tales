package de.thedead2.minecraft_tales.data.journal.progress;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public record JournalProgressType<T extends JournalProgress>(MapCodec<T> codec) {

    static final Registry<JournalProgressType<?>> TYPE_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "journal_progress_types_reg")), Lifecycle.stable());

    public static final JournalProgressType<StoryJournalProgress> STORY_CHAPTER_PROGRESS = register("story_chapter_progress", StoryJournalProgress.CODEC);
    public static final JournalProgressType<CommonJournalProgress> COMMON_JOURNAL_PROGRESS = register("common_journal_progress", CommonJournalProgress.CODEC);

    public static <T extends JournalProgress> JournalProgressType<T> register(String id, MapCodec<T> codec) {
        return Registry.register(TYPE_REGISTRY, ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, id), new JournalProgressType<>(codec));
    }
}
