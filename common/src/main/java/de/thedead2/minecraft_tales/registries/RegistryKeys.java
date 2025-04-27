package de.thedead2.minecraft_tales.registries;

import com.mojang.serialization.Codec;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.data.journal.JournalChapter;
import de.thedead2.minecraft_tales.data.quests.QuestObjective;
import de.thedead2.minecraft_tales.data.quests.StoryQuest;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public class RegistryKeys {
    public static final ResourceKey<DynamicRegistry<StoryQuest>> QUESTS_KEY = DynamicRegistries.createRegistryKey("quest_registry");
    public static final ResourceKey<DynamicRegistry<JournalChapter>> CHAPTERS_KEY = DynamicRegistries.createRegistryKey("journal_registry");
    public static final ResourceKey<DynamicRegistry<QuestObjective>> QUEST_OBJECTIVES_KEY = DynamicRegistries.createRegistryKey("quest_objectives_registry");

    static <T> Codec<ResourceKey<DynamicRegistry<T>>> getResourceKeyCodec() {
        return ResourceKey.codec(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "mt_main_registry")));
    }

    static <T> Codec<ResourceKey<DynamicRegistry<T>>> getResourceKeyCodec(Codec<T> ignored) {
        return getResourceKeyCodec();
    }
}
