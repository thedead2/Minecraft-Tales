package de.thedead2.minecraft_tales.data.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.util.helper.SerializationHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;


public record QuestObjective(String name, ResourceLocation id, ResourceLocation parentId, String description, boolean optional, long duration) {

    public QuestObjective(String name, ResourceLocation id, ResourceLocation parentId, String description, boolean optional) {
        this(name, id, parentId, description, optional, Long.MAX_VALUE);
    }

    public static final Codec<QuestObjective> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(QuestObjective::name),
            ResourceLocation.CODEC.fieldOf("id").forGetter(QuestObjective::id),
            ResourceLocation.CODEC.fieldOf("parentId").forGetter(QuestObjective::parentId),
            Codec.STRING.fieldOf("description").forGetter(QuestObjective::description),
            Codec.BOOL.fieldOf("optional").forGetter(QuestObjective::optional),
            Codec.LONG.fieldOf("duration").forGetter(QuestObjective::duration)
    ).apply(instance, QuestObjective::new));


    public boolean isTimeConstrained() {
        return duration != Long.MAX_VALUE;
    }


    public static final class Progress {

            public static final Codec<Progress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("objectiveId").forGetter(Progress::objectiveId),
                    Codec.LONG.fieldOf("timeRemaining").forGetter(Progress::timeRemaining),
                    Codec.INT.fieldOf("counts").forGetter(Progress::counts),
                    SerializationHelper.enumCodec(Status.class).fieldOf("status").forGetter(Progress::status)
            ).apply(instance, Progress::new));

        private final ResourceLocation objectiveId;

        private long timeRemaining;

        private int counts;

        private Status status;


        public Progress(ResourceLocation objectiveId, long timeRemaining, int counts, Status status) {
            this.objectiveId = objectiveId;
            this.timeRemaining = timeRemaining;
            this.counts = counts;
            this.status = status;
        }


        public ResourceLocation objectiveId() {
            return objectiveId;
        }


        public long timeRemaining() {
            return timeRemaining;
        }


        public int counts() {
            return counts;
        }


        public Status status() {
            return status;
        }


        public void setStatus(Status status) {
            this.status = status;
        }


        public void setTimeRemaining(long timeRemaining) {
            this.timeRemaining = timeRemaining;
        }


        public void setCounts(int counts) {
            this.counts = counts;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (Progress) obj;
            return Objects.equals(this.objectiveId, that.objectiveId) &&
                    this.timeRemaining == that.timeRemaining &&
                    this.counts == that.counts &&
                    Objects.equals(this.status, that.status);
        }


        @Override
        public int hashCode() {
            return Objects.hash(objectiveId, timeRemaining, counts, status);
        }


        @Override
        public String toString() {
            return "Progress[" +
                    "objectiveId=" + objectiveId + ", " +
                    "timeRemaining=" + timeRemaining + ", " +
                    "counts=" + counts + ", " +
                    "status=" + status + ']';
        }
    }

    public enum Status {
        NOT_STARTED,
        ACTIVE,
        PAUSED,
        COMPLETED,
        FAILED
    }
}
