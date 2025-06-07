package de.thedead2.minecraft_tales.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.MTMainInitiator;
import de.thedead2.minecraft_tales.data.story.StoryProgressHandler;
import de.thedead2.minecraft_tales.player.progress.PlayerProgress;
import de.thedead2.minecraft_tales.util.helper.IOHelper;
import de.thedead2.minecraft_tales.util.helper.SerializationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


public class MTPlayer {

    public static final Codec<MTPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("playerName").forGetter(MTPlayer::getName),
        SerializationHelper.UUID_CODEC.fieldOf("playerUUID").forGetter(MTPlayer::getUUID),
        ResourceLocation.CODEC.optionalFieldOf("teamId").forGetter(mtPlayer -> mtPlayer.getTeam().map(PlayerTeam::getId)),
        SerializationHelper.PATH_CODEC.fieldOf("savePath").forGetter(MTPlayer::getSavePath),
        Codec.INT.fieldOf("extraLives").forGetter(MTPlayer::getExtraLives),
        PlayerProgress.CODEC.fieldOf("progress").forGetter(MTPlayer::getProgress)
    ).apply(instance, (name, uuid, teamId, savePath, extraLives, progress) -> {
        var modInstance = MTMainInitiator.getModInstance();
        AtomicReference<PlayerTeam> team = new AtomicReference<>();

        teamId.ifPresent(id -> team.set(modInstance.getTeam(id)));

        return new MTPlayer(name, team.get(), uuid, modInstance.getPlayer(uuid), savePath, extraLives, progress);
    }));



    private final String playerName;

    private final UUID uuid;

    private final Player player;

    @Nullable
    private PlayerTeam team;

    private int extraLives;

    private PlayerProgress progress;

    private Path savePath;


    private MTPlayer(Player player, Path savePath) {
        this.playerName = player.getScoreboardName();
        this.team = null;
        this.uuid = player.getUUID();
        this.player = player;
        this.extraLives = 0;
        this.progress = new PlayerProgress(this.uuid);
        this.savePath = savePath;
    }


    private MTPlayer(String playerName, @Nullable PlayerTeam team, UUID uuid, Player player, Path savePath, int extraLives, PlayerProgress progress) {
        this.playerName = playerName;
        this.team = team;
        this.uuid = uuid;
        this.player = player;
        this.extraLives = extraLives;
        this.progress = progress;
        this.savePath = savePath;
    }


    static MTPlayer loadFromFile(Player player, File playerDataFile) throws IOException {
        return IOHelper.loadFromFile(CODEC, playerDataFile.toPath(), false).orElse(new MTPlayer(player, playerDataFile.toPath()));
    }


    void saveToFile(File playerFile) throws IOException {
        if(!this.savePath.equals(playerFile.toPath()))
            this.savePath = playerFile.toPath();

        IOHelper.saveToFile(this, CODEC, playerFile.toPath(), false);
    }

    public PlayerProgress getProgress() {
        if(this.isInTeam()) {
            return this.team.getTeamProgress();
        }
        else return this.progress;
    }

    public Path getSavePath() {
        return savePath;
    }

    public Optional<PlayerTeam> getTeam() {
        return Optional.ofNullable(team);
    }


    public Player getPlayer() {
        return this.player;
    }


    public String getName() {
        return playerName;
    }


    public UUID getUUID() {
        return uuid;
    }


    public boolean isInTeam(PlayerTeam team) {
        return team.equals(this.team);
    }

    void setTeam(@Nullable PlayerTeam playerTeam) {
        this.team = playerTeam;
    }

    public boolean isInTeam() {
        return team != null;
    }


    void addExtraLife(int amount) {
        this.extraLives += amount;
    }

    public boolean hasExtraLife() {
        return this.extraLives > 0;
    }

    void removeExtraLife(int amount) {
        if(this.extraLives <= 0) return;

        this.extraLives -= amount;
    }


    public int getExtraLives() {
        return this.extraLives;
    }

    void backupProgress() throws IOException {
        IOHelper.saveToFile(this.progress, PlayerProgress.CODEC, this.savePath.resolve("progress_backup.dat"), true);
    }

    void restoreProgress() throws IOException {
        this.progress = IOHelper.loadFromFile(PlayerProgress.CODEC, this.savePath.resolve("progress_backup.dat"), true).orElse(new PlayerProgress(this.uuid));
    }


    void setProgress(PlayerProgress playerProgress) {
        this.progress = playerProgress;
    }


    public StoryProgressHandler getStoryProgressHandler() {
        return this.progress.getStoryProgress().getStoryProgressHandler();
    }
}
