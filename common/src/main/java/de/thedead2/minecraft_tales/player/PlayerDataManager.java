package de.thedead2.minecraft_tales.player;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.registries.DeferrableActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class PlayerDataManager {

    public static final int maxDaysOffline = 183;

    private TeamSaveData teamSaveData = null;

    private final Map<UUID, MTPlayer> mtPlayers = Maps.newHashMap();

    private final PlayerLoginHandler playerLoginHandler = new PlayerLoginHandler();


    MTPlayer loadPlayerData(Player player, File playerDataFile) throws IOException {
        MTPlayer mtPlayer = MTPlayer.loadFromFile(player, playerDataFile);
        mtPlayers.put(mtPlayer.getUUID(), mtPlayer);

        return mtPlayer;
    }


    void loadData(ServerLevel level) {
        var dataStorage = level.getDataStorage();
        teamSaveData = dataStorage.computeIfAbsent(new SavedData.Factory<>(() -> new TeamSaveData(new HashMap<>()), TeamSaveData::load, DataFixTypes.PLAYER), "teams");
    }


    MTPlayer savePlayerData(Player player, File playerFile) throws IOException {
        MTPlayer playerData = getPlayerData(player);
        if (playerData == null) throw new IllegalStateException("Cannot save mt player data for player " + player + "! There's no additional save data?!");
        playerData.saveToFile(playerFile);
        return playerData;
    }


    PlayerLoginHandler getPlayerLoginHandler() {
        return playerLoginHandler;
    }

    public Collection<MTPlayer> getPlayersOfTeam(PlayerTeam team) {
        Set<MTPlayer> players = Sets.newHashSet();
        team.getMembers().stream().map(KnownPlayer::uuid).forEach(uuid -> {
            if(mtPlayers.containsKey(uuid))
                players.add(mtPlayers.get(uuid));
        });

        return players;
    }

    public MTPlayer getPlayerData(KnownPlayer player) {
        return mtPlayers.get(player.uuid());
    }


    @Nullable
    public MTPlayer getPlayerData(Player player) {
        return getPlayerData(player.getUUID());
    }

    @Nullable
    public MTPlayer getPlayerData(UUID playerId) {
        return mtPlayers.get(playerId);
    }

    @Nullable
    public PlayerTeam getTeam(String teamName) {
        return getTeamData().map(teamData -> teamData.getTeam(teamName)).orElse(null);
    }


    public void addTeam(PlayerTeam team) {
        getTeamData().ifPresent(teamData -> teamData.addTeam(team));
    }

    @Nullable
    public PlayerTeam getTeam(KnownPlayer player) {
        return getTeamData().map(teamData -> teamData.getTeamForPlayer(player)).orElse(null);
    }

    @Nullable
    public PlayerTeam getTeam(ResourceLocation id) {
        return getTeamData().map(teamData -> teamData.getTeam(id)).orElse(null);
    }


    public ImmutableCollection<PlayerTeam> allTeams() {
        return getTeamData().map(TeamSaveData::allTeams).orElse(ImmutableSet.of());
    }


    private Optional<TeamSaveData> getTeamData() {
        return Optional.ofNullable(teamSaveData);
    }

    public boolean removePlayerFromTeam(KnownPlayer player, PlayerTeam team) {
        MTPlayer mtPlayer = getPlayerData(player);

        DeferrableActions.DeferrableActionInstance<PlayerTeam> instance = DeferrablePlayerActions.REMOVE_FROM_TEAM.asInstance(team);

        if (mtPlayer == null) return this.playerLoginHandler.scheduleActionForPlayer(player, instance);
        else return instance.execute(mtPlayer);
    }

    public boolean addPlayerToTeam(KnownPlayer player, PlayerTeam team) {
        boolean bool = team.addPlayer(player);
        boolean bool2;
        MTPlayer mtPlayer = getPlayerData(player);

        DeferrableActions.DeferrableActionInstance<PlayerTeam> instance = DeferrablePlayerActions.ADD_TO_TEAM.asInstance(team);

        if (mtPlayer == null) bool2 = this.playerLoginHandler.scheduleActionForPlayer(player, instance);
        else bool2 = instance.execute(mtPlayer);

        return bool && bool2;
    }

    public boolean isPlayerOnline(KnownPlayer player) {
        MTPlayer mtPlayer = getPlayerData(player);

        return mtPlayer != null;
    }


    public boolean deleteTeam(ResourceLocation teamId) {
        AtomicBoolean bool = new AtomicBoolean(false);
        getTeamData().ifPresent(teamData -> bool.set(teamData.removeTeam(teamId, this)));
        return bool.get();
    }


    public boolean deleteTeam(PlayerTeam team) {
        AtomicBoolean bool = new AtomicBoolean(false);
        getTeamData().ifPresent(teamData -> bool.set(teamData.removeTeam(team, this)));
        return bool.get();
    }


    public boolean clearTeams() {
        AtomicBoolean bool = new AtomicBoolean(false);
        getTeamData().ifPresent(teamData -> {
            teamData.clearAll(this);
            bool.set(true);
        });
        return bool.get();
    }


    public ImmutableCollection<MTPlayer> allPlayers() {
        return ImmutableSet.copyOf(mtPlayers.values());
    }


    public void clearPlayerData(Player player) {
        clearPlayerData(player.getUUID());
    }


    public void clearPlayerData(UUID playerId) {
        mtPlayers.remove(playerId);
    }



    public boolean checkLastLogin(KnownPlayer player) {
        LocalDateTime lastOnline = player.lastOnline();
        LocalDateTime now = LocalDateTime.now();
        long diff = ChronoUnit.DAYS.between(lastOnline, now);
        if(diff > maxDaysOffline) {
            MTGlobalConstants.LOGGER.info("Clearing data of player {} as it wasn't online for a long time! Last online: {}", player.name(), lastOnline.format(MTGlobalConstants.DATE_TIME_FORMATTER));
            return true;
        }
        else {
            return false;
        }
    }
}
