package de.thedead2.minecraft_tales.data.predicates;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;


public class EntityFlagsPredicate implements SimpleTriggerPredicate<Entity> {

    public static final EntityFlagsPredicate ANY = new EntityFlagsPredicate(null, null, null, null, null);

    @Nullable
    private final Boolean isOnFire;

    @Nullable
    private final Boolean isSneaking;

    @Nullable
    private final Boolean isSprinting;

    @Nullable
    private final Boolean isSwimming;

    @Nullable
    private final Boolean isBaby;


    public EntityFlagsPredicate(@Nullable Boolean isOnFire, @Nullable Boolean isSneaking, @Nullable Boolean isSprinting, @Nullable Boolean isSwimming, @Nullable Boolean isBaby) {
        this.isOnFire = isOnFire;
        this.isSneaking = isSneaking;
        this.isSprinting = isSprinting;
        this.isSwimming = isSwimming;
        this.isBaby = isBaby;
    }

    @Override
    public boolean matches(Entity entity) {
        if(this.isOnFire != null && entity.isOnFire() != this.isOnFire) {
            return false;
        }
        else if(this.isSneaking != null && entity.isCrouching() != this.isSneaking) {
            return false;
        }
        else if(this.isSprinting != null && entity.isSprinting() != this.isSprinting) {
            return false;
        }
        else if(this.isSwimming != null && entity.isSwimming() != this.isSwimming) {
            return false;
        }
        else {
            return this.isBaby == null || !(entity instanceof LivingEntity livingEntity) || livingEntity.isBaby() == this.isBaby;
        }
    }
}
