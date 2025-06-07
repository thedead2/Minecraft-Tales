package de.thedead2.minecraft_tales.data.predicates;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;


public class BlockPredicate implements TriggerPredicate<ServerLevel, BlockPos> {

    public static final BlockPredicate ANY = new BlockPredicate(null, null, NbtPredicate.ANY);

    @Nullable
    private final TagKey<Block> tag;

    @Nullable
    private final Block block;

    private final NbtPredicate nbt;


    public BlockPredicate(@Nullable TagKey<Block> tag, @Nullable Block block, NbtPredicate nbt) {
        this.tag = tag;
        this.block = block;
        this.nbt = nbt;
    }


    public static BlockPredicate from(Block block) {
        return new BlockPredicate(null, block, NbtPredicate.ANY);
    }

    @Override
    public boolean matches(ServerLevel serverLevel, BlockPos pos) {
        if (this == ANY) return true;
        if (!serverLevel.isLoaded(pos)) return false;

        BlockState state = serverLevel.getBlockState(pos);
        if (this.tag != null && !state.is(this.tag)) return false;
        if (this.block != null && !this.block.equals(state.getBlock())) return false;

        if (this.nbt != NbtPredicate.ANY) {
            BlockEntity entity = serverLevel.getBlockEntity(pos);
            return entity != null && this.nbt.matches(entity.saveWithFullMetadata(serverLevel.registryAccess()));
        }

        return true;
    }
}
