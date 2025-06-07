package de.thedead2.minecraft_tales.event.types;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;


public class BlockEvent extends MTEvent{
    private final ServerLevel level;
    private final BlockPos pos;
    private final BlockState state;


    public BlockEvent(ServerLevel level, BlockPos pos, BlockState state) {
        this.level = level;
        this.pos = pos;
        this.state = state;
    }


    public ServerLevel getLevel() {
        return level;
    }


    public BlockPos getPos() {
        return pos;
    }


    public BlockState getBlockState() {
        return state;
    }
}
