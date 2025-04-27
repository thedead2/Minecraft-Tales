package de.thedead2.minecraft_tales.player;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.player.progress.PlayerProgress;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Consumer;


public final class PlayerTeam {

    public static final Codec<PlayerTeam> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("teamName").forGetter(PlayerTeam::getName),
            ResourceLocation.CODEC.fieldOf("teamId").forGetter(PlayerTeam::getId),
            Codec.STRING.fieldOf("description").forGetter(PlayerTeam::getDescription),
            KnownPlayer.CODEC.fieldOf("teamLeader").forGetter(PlayerTeam::getLeader),
            PlayerProgress.CODEC.fieldOf("teamProgress").forGetter(PlayerTeam::getTeamProgress),
            KnownPlayer.CODEC.listOf().fieldOf("knownMembers").forGetter(playerTeam -> playerTeam.getMembers().asList())
    ).apply(instance, PlayerTeam::new));

    private String teamName;

    private final ResourceLocation id;

    private String description;

    private KnownPlayer leader;

    private final PlayerProgress teamProgress;

    private final Set<KnownPlayer> knownMembers = new HashSet<>();



    public PlayerTeam(String teamName, ResourceLocation id, String description, KnownPlayer leader, PlayerProgress teamProgress, Collection<KnownPlayer> knownMembers) {
        this.teamName = teamName;
        this.id = id;
        this.description = description;
        this.leader = leader;
        this.teamProgress = teamProgress;
        this.knownMembers.addAll(knownMembers);
    }


    public static ResourceLocation createId(String name) {
        return ResourceLocation.tryBuild(MTGlobalConstants.MOD_ID, name.toLowerCase().replaceAll(" ", "_"));
    }


    public KnownPlayer getLeader() {
        return leader;
    }

    public boolean isLeader(KnownPlayer knownPlayer) {
        return this.leader.equals(knownPlayer);
    }


    public String getDescription() {
        return description;
    }


    public String getName() {
        return teamName;
    }


    public ResourceLocation getId() {
        return this.id;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    void addPlayers(Collection<KnownPlayer> players) {
        players.forEach(this::addPlayer);
    }

    void addMTPlayers(Collection<MTPlayer> players) {
        players.forEach(this::addPlayer);
    }

    boolean addPlayer(KnownPlayer knownPlayer) {
        return this.knownMembers.add(knownPlayer);
    }


    void addPlayer(MTPlayer mtPlayer) {
        this.addPlayer(KnownPlayer.fromMTPlayer(mtPlayer));
    }


    public boolean isPlayerInTeam(KnownPlayer player) {
        return this.knownMembers.contains(player);
    }


    void removePlayers(Collection<KnownPlayer> players) {
        players.forEach(this::removePlayer);
    }

    void removeMTPlayers(Collection<MTPlayer> players) {
        players.forEach(this::removePlayer);
    }

    void removePlayer(KnownPlayer knownPlayer) {
        if(this.isLeader(knownPlayer))
            this.knownMembers.stream().findFirst().ifPresent(this::setLeader);
        this.knownMembers.remove(knownPlayer);
    }


    public void setLeader(KnownPlayer player) {
        this.leader = player;
    }


    public ImmutableCollection<KnownPlayer> getMembers() {
        return ImmutableSet.copyOf(this.knownMembers);
    }


    public void forEachMember(Consumer<KnownPlayer> action) {
        this.knownMembers.forEach(action);
    }


    void removePlayer(MTPlayer mtPlayer) {
        this.removePlayer(KnownPlayer.fromMTPlayer(mtPlayer));
    }


    public PlayerProgress getTeamProgress() {
        return teamProgress;
    }
}
