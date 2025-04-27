package de.thedead2.minecraft_tales.player;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public class TeamSaveData extends SavedData {

    private static final Codec<TeamSaveData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, PlayerTeam.CODEC).fieldOf("teams").forGetter(TeamSaveData::getTeams)
    ).apply(instance, TeamSaveData::new));

    private final Map<ResourceLocation, PlayerTeam> teams = new HashMap<>();


    TeamSaveData(Map<ResourceLocation, PlayerTeam> teams) {
        this.teams.putAll(teams);
        this.setDirty();
    }


    public static TeamSaveData load(CompoundTag tag, HolderLookup.Provider ignored) {
        return CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }


    void addTeam(PlayerTeam team) {
        this.teams.put(team.getId(), team);
        this.setDirty();
    }

    @Nullable
    public PlayerTeam getTeamForPlayer(KnownPlayer player) {
        return this.teams.values().stream().filter(playerTeam -> playerTeam.isPlayerInTeam(player)).findAny().orElse(
                null);
    }

    @Nullable
    public PlayerTeam getTeam(String teamName) {
        return getTeam(PlayerTeam.createId(teamName));
    }

    @Nullable
    public PlayerTeam getTeam(ResourceLocation id) {
        return this.teams.get(id);
    }


    public ImmutableCollection<PlayerTeam> allTeams() {
        return ImmutableSet.copyOf(this.teams.values());
    }


    private Map<ResourceLocation, PlayerTeam> getTeams() {
        return teams;
    }


    boolean removeTeam(PlayerTeam team, PlayerDataManager playerDataManager) {
        return removeTeam(team.getId(), playerDataManager);
    }


    boolean removeTeam(ResourceLocation id, PlayerDataManager playerDataManager) {
        this.setDirty();
        PlayerTeam team = this.teams.remove(id);
        if (team != null) {
            playerDataManager.getPlayersOfTeam(team).forEach(singlePlayer -> singlePlayer.setTeam(null)); //FIXME: If team gets deleted schedule removal for non online players
        }

        return team != null;
    }


    void clearAll(PlayerDataManager playerDataManager) {
        this.teams.forEach((id, team) -> playerDataManager.getPlayersOfTeam(team).forEach(singlePlayer -> singlePlayer.setTeam(null))); //FIXME: If team gets deleted schedule removal for non online players
        this.teams.clear();
        this.setDirty();
    }


    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        return (CompoundTag) CODEC.encode(this, NbtOps.INSTANCE, compoundTag).getOrThrow();
    }
}
