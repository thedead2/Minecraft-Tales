package de.thedead2.minecraft_tales.data.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.util.helper.SerializationHelper;
import net.minecraft.resources.ResourceLocation;


public record QuestObjective(String name, ResourceLocation id, ResourceLocation parentId, String description, boolean optional, long duration) {

    public static final Codec<QuestObjective> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(QuestObjective::name),
            ResourceLocation.CODEC.fieldOf("id").forGetter(QuestObjective::id),
            ResourceLocation.CODEC.fieldOf("parentId").forGetter(QuestObjective::parentId),
            Codec.STRING.fieldOf("description").forGetter(QuestObjective::description),
            Codec.BOOL.fieldOf("optional").forGetter(QuestObjective::optional),
            Codec.LONG.fieldOf("duration").forGetter(QuestObjective::duration)
    ).apply(instance, QuestObjective::new));


    public record Progress(ResourceLocation objectiveId, long timeRemaining, int counts, Status status) {

        public static final Codec<Progress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("objectiveId").forGetter(Progress::objectiveId),
                Codec.LONG.fieldOf("timeRemaining").forGetter(Progress::timeRemaining),
                Codec.INT.fieldOf("counts").forGetter(Progress::counts),
                SerializationHelper.enumCodec(Status.class).fieldOf("status").forGetter(Progress::status)
        ).apply(instance, Progress::new));
    }

    public enum Status {
        ACTIVE,
        COMPLETED,
        FAILED
    }
}
