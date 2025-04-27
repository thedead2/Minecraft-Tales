package de.thedead2.minecraft_tales.data.quests;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.data.rewards.Reward;
import de.thedead2.minecraft_tales.util.helper.SerializationHelper;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;


public class StoryQuest {

    public static final Codec<StoryQuest> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(StoryQuest::getTitle),
            ResourceLocation.CODEC.fieldOf("id").forGetter(StoryQuest::getId),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(StoryQuest::getIcon),
            Codec.STRING.fieldOf("questType").forGetter(StoryQuest::getQuestType),
            ResourceLocation.CODEC.fieldOf("chapterId").forGetter(StoryQuest::getChapterId),
            QuestObjective.CODEC.listOf().fieldOf("objectives").forGetter(StoryQuest::getObjectives),
            Reward.CODEC.listOf().fieldOf("rewards").forGetter(StoryQuest::getRewards)
    ).apply(instance, StoryQuest::new));

    private final String title;
    private final ResourceLocation id;
    private final ResourceLocation icon;
    private final String questType;
    private final ResourceLocation chapterId;
    private final List<QuestObjective> objectives;
    private final List<Reward> rewards;

    public StoryQuest(String title, ResourceLocation id, ResourceLocation icon, String questType, ResourceLocation chapterId, List<QuestObjective> objectives, List<Reward> rewards) {
        this.title = title;
        this.id = id;
        this.icon = icon;
        this.questType = questType;
        this.chapterId = chapterId;
        this.objectives = objectives;
        this.rewards = rewards;
    }


    public String getTitle() {
        return title;
    }


    public ResourceLocation getId() {
        return id;
    }


    public ResourceLocation getIcon() {
        return icon;
    }


    public String getQuestType() {
        return questType;
    }


    public ResourceLocation getChapterId() {
        return chapterId;
    }

    public ImmutableList<QuestObjective> getObjectives() {
        return ImmutableList.copyOf(objectives);
    }

    public ImmutableList<Reward> getRewards() {
        return ImmutableList.copyOf(rewards);
    }



    public record Progress(ResourceLocation questId, List<QuestObjective.Progress> completeObjectives, boolean rewarded, Status status) {

        public static final Codec<Progress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("questId").forGetter(Progress::questId),
                QuestObjective.Progress.CODEC.listOf().fieldOf("completeObjectives").forGetter(Progress::completeObjectives),
                Codec.BOOL.fieldOf("rewarded").forGetter(Progress::rewarded),
                SerializationHelper.enumCodec(Status.class).fieldOf("status").forGetter(Progress::status)
        ).apply(instance, Progress::new));


        public Progress(ResourceLocation questId) {
            this(questId, Lists.newArrayList(), false, Status.ACTIVE);
        }
    }

    public enum Status {
        ACTIVE,
        COMPLETED,
        FAILED,
        PAUSED
    }
}
