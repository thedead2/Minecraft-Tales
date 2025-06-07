package de.thedead2.minecraft_tales.data.trigger;

import de.thedead2.minecraft_tales.MTMainInitiator;
import de.thedead2.minecraft_tales.data.predicates.PlayerPredicate;
import de.thedead2.minecraft_tales.data.predicates.TriggerPredicate;
import de.thedead2.minecraft_tales.event.types.MTEvent;
import de.thedead2.minecraft_tales.player.MTPlayer;
import net.minecraft.world.entity.player.Player;


public abstract class ConditionTrigger<Event extends MTEvent, TestVal, AddArg> {

    protected final PlayerPredicate playerPredicate;
    protected final TriggerPredicate<TestVal, AddArg> predicate;


    protected ConditionTrigger(PlayerPredicate playerPredicate, TriggerPredicate<TestVal, AddArg> predicate) {
        this.playerPredicate = playerPredicate;
        this.predicate = predicate;
    }

    public abstract boolean onEvent(Event event);

    protected MTPlayer getPlayerData(Player player) {
        return MTMainInitiator.getModInstance().getPlayerData(player);
    }

    protected final boolean test(MTPlayer player, TestVal toTest, AddArg addArg) {
        boolean playerTest = this.playerPredicate.matches(player);
        boolean predicateTest = this.predicate.matches(toTest, addArg);

        return playerTest && predicateTest;
    }
}
