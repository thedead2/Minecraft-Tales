package de.thedead2.minecraft_tales.player.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.MTMainInitiator;
import de.thedead2.minecraft_tales.data.story.StoryProgress;
import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;
import de.thedead2.minecraft_tales.player.MTPlayer;

import java.util.UUID;


public class PlayerStoryProgress {
    public static final Codec<PlayerStoryProgress> CODEC = RecordCodecBuilder.create((instance) -> instance.group());

    private final UUID playerId;
    private final StoryProgressHandler storyProgressHandler;


    public PlayerStoryProgress(StoryProgress storyProgress, UUID playerId) {
        this.playerId = playerId;
        this.storyProgressHandler = new StoryProgressHandler(playerId);

        this.storyProgressHandler.load(storyProgress);
    }


    public StoryProgressHandler getStoryProgressHandler() {
        return storyProgressHandler;
    }


    public void copyFrom(PlayerStoryProgress storyProgress) {
        this.storyProgressHandler.load(storyProgress.getStoryProgress());
    }

    public StoryProgress getStoryProgress() {
        return this.storyProgressHandler.save();
    }
}
