package de.thedead2.minecraft_tales.event.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;


public class BreakBlockEvent extends BlockEvent{
    private final Player player;


    public BreakBlockEvent(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        super(level, pos, state);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
