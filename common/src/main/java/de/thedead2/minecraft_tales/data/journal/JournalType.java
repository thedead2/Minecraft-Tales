package de.thedead2.minecraft_tales.data.journal;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public record JournalType<T extends JournalChapter>(MapCodec<T> codec) {

    static final Registry<JournalType<?>> TYPE_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "journal_types_reg")), Lifecycle.stable());


    public static final JournalType<StoryChapter> STORY_CHAPTER = register("story_chapter", StoryChapter.CODEC);
    public static final JournalType<EntityChapter> ENTITY_CHAPTER = register("entity_chapter", EntityChapter.CODEC);


    public static <T extends JournalChapter> JournalType<T> register(String id, MapCodec<T> codec) {
        return Registry.register(TYPE_REGISTRY, ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, id), new JournalType<>(codec));
    }
}
