package de.thedead2.minecraft_tales.data.predicates;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.thedead2.progression_reloaded.util.helper.SerializationHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;

import javax.annotation.Nullable;


public class DamageSourcePredicate implements ITriggerPredicate<DamageSource> {

    public static final DamageSourcePredicate ANY = new DamageSourcePredicate(null, null, null, null, null, EntityPredicate.ANY);

    @Nullable
    private final Boolean isProjectile;

    @Nullable
    private final Boolean isExplosion;

    @Nullable
    private final Boolean isFire;

    @Nullable
    private final Boolean isMagic;

    @Nullable
    private final Boolean isLightning;

    private final EntityPredicate sourceEntity;


    public DamageSourcePredicate(@Nullable Boolean isProjectile, @Nullable Boolean isExplosion, @Nullable Boolean isFire, @Nullable Boolean isMagic, @Nullable Boolean isLightning, EntityPredicate sourceEntity) {
        this.isProjectile = isProjectile;
        this.isExplosion = isExplosion;
        this.isFire = isFire;
        this.isMagic = isMagic;
        this.isLightning = isLightning;
        this.sourceEntity = sourceEntity;
    }

    @Override
    public boolean matches(DamageSource damageSource, Object... addArgs) {
        if(this == ANY) {
            return true;
        }
        else if(this.isProjectile != null && this.isProjectile != damageSource.isProjectile()) {
            return false;
        }
        else if(this.isExplosion != null && this.isExplosion != damageSource.isExplosion()) {
            return false;
        }
        else if(this.isFire != null && this.isFire != damageSource.isFire()) {
            return false;
        }
        else if(this.isMagic != null && this.isMagic != damageSource.isMagic()) {
            return false;
        }
        else if(this.isLightning != null && this.isLightning != (damageSource == DamageSource.LIGHTNING_BOLT)) {
            return false;
        }
        else {
            return this.sourceEntity.matches(damageSource.getEntity(), addArgs[0]);
        }
    }
}
