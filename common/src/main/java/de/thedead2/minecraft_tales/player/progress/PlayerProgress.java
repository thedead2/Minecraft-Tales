package de.thedead2.minecraft_tales.player.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.util.helper.SerializationHelper;

import java.util.UUID;


public class PlayerProgress {

    public static final Codec<PlayerProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.UUID_CODEC.fieldOf("playerId").forGetter(PlayerProgress::getPlayerId),
            PlayerStoryProgress.CODEC.fieldOf("storyProgress").forGetter(PlayerProgress::getStoryProgress),
            PlayerQuestProgress.CODEC.fieldOf("questProgress").forGetter(PlayerProgress::getQuestProgress),
            PlayerJournalProgress.CODEC.fieldOf("journalProgress").forGetter(PlayerProgress::getJournalProgress)
    ).apply(instance, PlayerProgress::new));

    private final UUID playerId;
    private final PlayerStoryProgress storyProgress;
    private final PlayerQuestProgress questProgress;
    private final PlayerJournalProgress journalProgress;


    public PlayerProgress(UUID playerId) {
        this(playerId, new PlayerStoryProgress(), new PlayerQuestProgress(), new PlayerJournalProgress());
    }

    private PlayerProgress(UUID playerId, PlayerStoryProgress storyProgress, PlayerQuestProgress questProgress, PlayerJournalProgress journalProgress) {
        this.playerId = playerId;
        this.storyProgress = storyProgress;
        this.questProgress = questProgress;
        this.journalProgress = journalProgress;
    }


    public UUID getPlayerId() {
        return playerId;
    }


    public PlayerStoryProgress getStoryProgress() {
        return storyProgress;
    }


    public PlayerQuestProgress getQuestProgress() {
        return questProgress;
    }


    public PlayerJournalProgress getJournalProgress() {
        return journalProgress;
    }

    public void copyFrom(PlayerProgress other) {
        this.storyProgress.copyFrom(other.getStoryProgress());
        this.questProgress.copyFrom(other.getQuestProgress());
        this.journalProgress.copyFrom(other.getJournalProgress());
    }
}
