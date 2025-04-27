package de.thedead2.minecraft_tales.data.journal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;


public class EntityChapter extends JournalChapter {

    public static final MapCodec<EntityChapter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            JournalChapter.PartialSerializationResult.CODEC.fieldOf("common").forGetter(EntityChapter::wrapPartial),
            Codec.STRING.fieldOf("type").forGetter(EntityChapter::getEntityType),
            ResourceLocation.CODEC.fieldOf("image").forGetter(EntityChapter::getImage),
            ResourceLocation.CODEC.fieldOf("entity").forGetter(EntityChapter::getEntity)
    ).apply(instance, (partialSerializationResult, type, image, entity) -> new EntityChapter(partialSerializationResult.title(), partialSerializationResult.id(), partialSerializationResult.icon(), partialSerializationResult.entries(), type, image, entity)));

    private final String type;
    private final ResourceLocation image;
    private final ResourceLocation entity;

    public EntityChapter(String title, ResourceLocation id, ResourceLocation icon, List<JournalEntry> entries, String type, ResourceLocation image, ResourceLocation entity) {
        super(title, id, icon, entries);
        this.type = type;
        this.image = image;
        this.entity = entity;
    }

    public String getEntityType() {
        return type;
    }


    public ResourceLocation getImage() {
        return image;
    }

    public ResourceLocation getEntity() {
        return entity;
    }


    @Override
    public JournalType<?> getType() {
        return JournalType.ENTITY_CHAPTER;
    }
}
