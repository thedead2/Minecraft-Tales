package de.thedead2.minecraft_tales.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.data.journal.JournalChapter;
import de.thedead2.minecraft_tales.data.quests.QuestObjective;
import de.thedead2.minecraft_tales.data.quests.StoryQuest;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public class DynamicRegistries {
    private static final Registry<DynamicRegistry<?>> MT_MAIN_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "mt_main_registry")), Lifecycle.stable());

    public static final DynamicRegistry<StoryQuest> QUESTS_REGISTRY = register(RegistryKeys.QUESTS_KEY, StoryQuest.CODEC);
    public static final DynamicRegistry<QuestObjective> QUEST_OBJECTIVES_REGISTRY = register(RegistryKeys.QUEST_OBJECTIVES_KEY, QuestObjective.CODEC);
    public static final DynamicRegistry<JournalChapter> CHAPTERS_REGISTRY = register(RegistryKeys.CHAPTERS_KEY, JournalChapter.CODEC);

    @SuppressWarnings("unchecked")
    public static <T> DynamicRegistry<T> register(ResourceKey<DynamicRegistry<T>> key, Codec<T> elementsCodec) {
        return Registry.register((Registry<DynamicRegistry<T>>) (Object) MT_MAIN_REGISTRY, key, new DynamicRegistry<>(key, elementsCodec));
    }

    public static <T> DynamicRegistry<T> register(String id, Codec<T> elementsCodec) {
        ResourceKey<DynamicRegistry<T>> key = createRegistryKey(id);

        return register(key, elementsCodec);
    }

    @SuppressWarnings("unchecked")
    public static <T> ResourceKey<DynamicRegistry<T>> createRegistryKey(String id) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "dynamic_registries/" + id);

        return ResourceKey.create((ResourceKey<? extends Registry<DynamicRegistry<T>>>) MT_MAIN_REGISTRY.key(), resourceLocation);
    }

    @SuppressWarnings("unchecked")
    public static <T> DynamicRegistry<T> getRegistry(ResourceKey<DynamicRegistry<T>> key) {
        return (DynamicRegistry<T>) MT_MAIN_REGISTRY.get((ResourceKey<DynamicRegistry<?>>) (Object) key);
    }

    public static <T> DynamicRegistry<T> getRegistry(String id) {
        return getRegistry(createRegistryKey(id));
    }

    public static void saveRegistryContents(){
        MT_MAIN_REGISTRY.forEach(DynamicRegistry::saveContentsToDisk);
    }

    public static void loadRegistryContents(){
        MT_MAIN_REGISTRY.forEach(DynamicRegistry::loadContentsFromDisk);
    }
}
