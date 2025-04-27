package de.thedead2.minecraft_tales.data.journal;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;


public abstract class JournalChapter {

    public static final Codec<JournalChapter> CODEC = JournalType.TYPE_REGISTRY.byNameCodec().dispatch("type", JournalChapter::getType, JournalType::codec);

    protected final String title;
    protected final ResourceLocation id;
    protected final ResourceLocation icon;
    protected final List<JournalEntry> entries;

    public JournalChapter(String title, ResourceLocation id, ResourceLocation icon, List<JournalEntry> entries) {
        this.title = title;
        this.id = id;
        this.icon = icon;
        this.entries = entries;
    }


    public ResourceLocation getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public ResourceLocation getIcon() {
        return icon;
    }

    public abstract JournalType<?> getType();

    protected PartialSerializationResult wrapPartial() {
        return new PartialSerializationResult(this.title, this.id, this.icon, this.entries);
    }

    
    protected record PartialSerializationResult(String title, ResourceLocation id, ResourceLocation icon, List<JournalEntry> entries) {

        public static final Codec<PartialSerializationResult> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("title").forGetter(PartialSerializationResult::title),
                ResourceLocation.CODEC.fieldOf("id").forGetter(PartialSerializationResult::id),
                ResourceLocation.CODEC.fieldOf("icon").forGetter(PartialSerializationResult::icon),
                JournalEntry.CODEC.listOf().fieldOf("entries").forGetter(PartialSerializationResult::entries)
        ).apply(instance, PartialSerializationResult::new));
    }
}
