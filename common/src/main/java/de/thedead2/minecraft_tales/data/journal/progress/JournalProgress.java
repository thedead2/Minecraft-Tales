package de.thedead2.minecraft_tales.data.journal.progress;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.data.journal.JournalEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;


public abstract class JournalProgress {

    public static final Codec<JournalProgress> CODEC = JournalProgressType.TYPE_REGISTRY.byNameCodec().dispatch("type", JournalProgress::getType, JournalProgressType::codec);


    private final ResourceLocation chapterId;

    private final List<JournalEntry> visibleEntries;


    public JournalProgress(ResourceLocation chapterId, List<JournalEntry> visibleEntries) {
        this.chapterId = chapterId;
        this.visibleEntries = visibleEntries;
    }


    public JournalProgress(ResourceLocation chapterId) {
        this(chapterId, Lists.newArrayList());
    }


    public ResourceLocation chapterId() {
        return chapterId;
    }


    public List<JournalEntry> visibleEntries() {
        return visibleEntries;
    }

    public void add(JournalEntry entry) {
        this.visibleEntries.add(entry);
    }

    public boolean isVisible() {
        return !this.visibleEntries.isEmpty();
    }


    public abstract JournalProgressType<?> getType();


    protected PartialSerializationResult wrapPartial() {
        return new PartialSerializationResult(this.chapterId, this.visibleEntries);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (JournalProgress) obj;
        return Objects.equals(this.chapterId, that.chapterId) &&
                Objects.equals(this.visibleEntries, that.visibleEntries);
    }


    @Override
    public int hashCode() {
        return Objects.hash(chapterId, visibleEntries);
    }


    @Override
    public String toString() {
        return "Progress[" +
                "chapterId=" + chapterId + ", " +
                "visibleEntries=" + visibleEntries + ']';
    }


    protected record PartialSerializationResult(ResourceLocation chapterId, List<JournalEntry> visibleEntries) {

        public static final MapCodec<PartialSerializationResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("chapterId").forGetter(PartialSerializationResult::chapterId),
                JournalEntry.CODEC.listOf().fieldOf("visibleEntries").forGetter(PartialSerializationResult::visibleEntries)
        ).apply(instance, PartialSerializationResult::new));
    }
}
