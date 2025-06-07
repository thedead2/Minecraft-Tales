package de.thedead2.minecraft_tales.data.trigger;

import de.thedead2.minecraft_tales.data.predicates.PlayerPredicate;
import de.thedead2.minecraft_tales.data.predicates.SimpleTriggerPredicate;
import de.thedead2.minecraft_tales.event.types.MTEvent;
import de.thedead2.minecraft_tales.player.MTPlayer;


public abstract class SimpleConditionTrigger<Event extends MTEvent, TestVal> extends ConditionTrigger<Event, TestVal, Void> {

    protected SimpleConditionTrigger(PlayerPredicate playerPredicate, SimpleTriggerPredicate<TestVal> predicate) {
        super(playerPredicate, predicate);
    }

    protected final boolean test(MTPlayer player, TestVal testVal) {
        return this.test(player, testVal, null);
    }
}
