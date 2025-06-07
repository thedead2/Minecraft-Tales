package de.thedead2.minecraft_tales.data.predicates;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nullable;


public class LocationPredicate implements TriggerPredicate<BlockPos, ServerLevel> {

    public static final LocationPredicate ANY = new LocationPredicate(MinMax.ANY_DOUBLE, MinMax.ANY_DOUBLE, MinMax.ANY_DOUBLE, null, null, null);

    private final MinMax<Double> x;

    private final MinMax<Double> y;

    private final MinMax<Double> z;

    @Nullable
    private final ResourceKey<Biome> biome;

    @Nullable
    private final TagKey<Structure> structure;

    @Nullable
    private final ResourceKey<Level> dimension;


    public LocationPredicate(MinMax<Double> x, MinMax<Double> y, MinMax<Double> z, @Nullable ResourceKey<Biome> biome, @Nullable TagKey<Structure> structure, @Nullable ResourceKey<Level> dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.biome = biome;
        this.structure = structure;
        this.dimension = dimension;
    }


    @Override
    public boolean matches(BlockPos blockPos, ServerLevel serverLevel) {
        if(this == ANY) {
            return true;
        }

        if(!this.x.matches((double) blockPos.getX())) {
            return false;
        }
        else if(!this.y.matches((double) blockPos.getY())) {
            return false;
        }
        else if(!this.z.matches((double) blockPos.getZ())) {
            return false;
        }
        else if(serverLevel == null) {
            return true;
        }
        else if(this.dimension != null && this.dimension != serverLevel.dimension()) {
            return false;
        }
        else {
            boolean flag = serverLevel.isLoaded(blockPos);
            if(this.biome == null || flag && serverLevel.getBiome(blockPos).is(this.biome)) {
                return this.structure == null || flag && serverLevel.structureManager().getStructureWithPieceAt(blockPos, this.structure).isValid();
            }
            else {
                return false;
            }
        }
    }
}
