package de.thedead2.minecraft_tales.data.journal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;


public record JournalEntry(String name, ResourceLocation id, ResourceLocation chapterId, String content) {

    public static final Codec<JournalEntry> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(JournalEntry::name),
            ResourceLocation.CODEC.fieldOf("id").forGetter(JournalEntry::id),
            ResourceLocation.CODEC.fieldOf("chapterId").forGetter(JournalEntry::chapterId),
            Codec.STRING.fieldOf("content").forGetter(JournalEntry::content)
        ).apply(instance, JournalEntry::new));
}
