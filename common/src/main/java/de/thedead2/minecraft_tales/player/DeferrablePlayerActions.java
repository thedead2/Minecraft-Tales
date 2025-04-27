package de.thedead2.minecraft_tales.player;

import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.player.progress.PlayerProgress;
import de.thedead2.minecraft_tales.registries.DeferrableActions;

import java.io.IOException;


public class DeferrablePlayerActions {

    public static final DeferrableActions.RegisteredAction<PlayerTeam> ADD_TO_TEAM = DeferrableActions.register("add_player_to_team", PlayerTeam.CODEC, (player, team) -> {
        if(false) return false; // TODO: Ask player if he wants to join the team

        try {
            player.backupProgress();
        }
        catch (IOException e) {
            MTGlobalConstants.LOGGER.error("Could not backup progress for {}", player.getName(), e);
        }

        player.setTeam(team);

        return team != null && player.isInTeam(team);
    });

    public static final DeferrableActions.RegisteredAction<PlayerTeam> REMOVE_FROM_TEAM = DeferrableActions.register("remove_player_from_team", PlayerTeam.CODEC, (player, team) -> {
        player.setTeam(null);
        PlayerProgress teamProgress = team.getTeamProgress();

        if(true) { //TODO: Check if player wants progress to keep
            player.getProgress().copyFrom(teamProgress);
        }
        else {
            try {
                player.restoreProgress();
            }
            catch (IOException e) {
                MTGlobalConstants.LOGGER.error("Could not restore progress for {}", player.getName(), e);
                player.setProgress(new PlayerProgress(player.getUUID()));
            }
        }

        return true;
    });
}
