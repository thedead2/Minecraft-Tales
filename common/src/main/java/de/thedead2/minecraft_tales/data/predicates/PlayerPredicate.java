package de.thedead2.minecraft_tales.data.predicates;

import de.thedead2.minecraft_tales.player.MTPlayer;
import de.thedead2.minecraft_tales.player.PlayerTeam;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

import javax.annotation.Nullable;
import java.util.UUID;


public class PlayerPredicate implements SimpleTriggerPredicate<MTPlayer> {

    public static final PlayerPredicate ANY = new PlayerPredicate(null, MinMax.ANY_INT, null, null);

    @Nullable
    private final UUID uuid;

    private final MinMax<Integer> xp;

    @Nullable
    private final GameType gameMode;

    @Nullable
    private final PlayerTeam team;


    public PlayerPredicate(@Nullable UUID uuid, MinMax<Integer> xp, @Nullable GameType gameMode, @Nullable PlayerTeam team) {
        this.uuid = uuid;
        this.xp = xp;
        this.gameMode = gameMode;
        this.team = team;
    }


    @Override
    public boolean matches(MTPlayer player) {
        if(this == ANY) {
            return true;
        }
        ServerPlayer serverplayer = (ServerPlayer) player.getPlayer();

        if(this.uuid != null && this.uuid != serverplayer.getUUID()) {
            return false;
        }
        else if(this.team != null && !player.isInTeam(this.team)) {
            return false;
        }
        else if(!this.xp.matches(serverplayer.experienceLevel)) {
            return false;
        }
        else {
            return this.gameMode == null || this.gameMode == serverplayer.gameMode.getGameModeForPlayer();
        }
    }
}
