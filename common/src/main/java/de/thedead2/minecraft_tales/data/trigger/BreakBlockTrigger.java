package de.thedead2.minecraft_tales.data.trigger;

import de.thedead2.minecraft_tales.data.predicates.BlockPredicate;
import de.thedead2.minecraft_tales.data.predicates.PlayerPredicate;
import de.thedead2.minecraft_tales.event.types.BreakBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;


public class BreakBlockTrigger extends ConditionTrigger<BreakBlockEvent, ServerLevel, BlockPos> {

    public BreakBlockTrigger(PlayerPredicate playerPredicate, BlockPredicate predicate) {
        super(playerPredicate, predicate);
    }


    @Override
    public boolean onEvent(BreakBlockEvent event) {
        return this.test(getPlayerData(event.getPlayer()), event.getLevel(), event.getPos());
    }
}
