package de.thedead2.minecraft_tales.data.predicates;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import de.thedead2.minecraft_tales.player.MTPlayer;
import de.thedead2.progression_reloaded.player.types.PlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;


public class EntityPredicate implements TriggerPredicate<Entity, MTPlayer> {

    public static final ResourceLocation ID = ITriggerPredicate.createId("entity");

    public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, EffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY);

    private final EntityTypePredicate entityType;

    private final DistancePredicate distanceToPlayer;

    private final LocationPredicate entityLocation;

    private final EffectsPredicate effects;

    private final NbtPredicate nbt;

    private final EntityFlagsPredicate flags;

    private final EntityEquipmentPredicate equipment;

    private final EntityPredicate vehicle;


    public EntityPredicate(EntityTypePredicate entityType, DistancePredicate distanceToPlayer, LocationPredicate entityLocation, EffectsPredicate effects, NbtPredicate nbt, EntityFlagsPredicate flags, EntityEquipmentPredicate equipment, EntityPredicate vehicle) {
        this.entityType = entityType;
        this.distanceToPlayer = distanceToPlayer;
        this.entityLocation = entityLocation;
        this.effects = effects;
        this.nbt = nbt;
        this.flags = flags;
        this.equipment = equipment;
        this.vehicle = vehicle;
    }


    public EntityPredicate(EntityTypePredicate entityType, DistancePredicate distanceToPlayer, LocationPredicate entityLocation, EffectsPredicate effects, NbtPredicate nbt, EntityFlagsPredicate flags, EntityEquipmentPredicate equipment) {
        this.entityType = entityType;
        this.distanceToPlayer = distanceToPlayer;
        this.entityLocation = entityLocation;
        this.effects = effects;
        this.nbt = nbt;
        this.flags = flags;
        this.equipment = equipment;
        this.vehicle = this;
    }


    public static EntityPredicate from(EntityType<?> entityType) {
        return new EntityPredicate(new EntityTypePredicate(entityType, null), DistancePredicate.ANY, LocationPredicate.ANY, EffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY);
    }


    public static CompoundTag getEntityTagToCompare(Entity entity) {
        CompoundTag compoundtag = entity.saveWithoutId(new CompoundTag());
        if(entity instanceof Player player) {
            ItemStack itemstack = player.getInventory().getSelected();
            if(!itemstack.isEmpty()) {
                compoundtag.put("selectedItem", itemstack.save(new CompoundTag()));
            }
        }

        return compoundtag;
    }


    @Override
    public boolean matches(Entity entity, MTPlayer player) {
        if(this == ANY) {
            return true;
        }
        else if(entity == null) {
            return false;
        }
        else if(!this.entityType.matches(entity.getType())) {
            return false;
        }
        else {
            if(player == null) {
                if(this.distanceToPlayer != DistancePredicate.ANY) {
                    return false;
                }
            }
            else {
                Vec3 playerPosition = player.getPlayer().position();
                if(!this.distanceToPlayer.matches(new DistancePredicate.DistanceInfo(playerPosition.x, playerPosition.y, playerPosition.z, entity.getX(), entity.getY(), entity.getZ()))) {
                    return false;
                }
            }

            if(!this.entityLocation.matches(new BlockPos(entity.getX(), entity.getY(), entity.getZ()), entity.getLevel())) {
                return false;
            }
            else {
                if(entity instanceof LivingEntity livingEntity && !this.effects.matches(livingEntity.getActiveEffectsMap())) {
                    return false;
                }
                else if(!this.nbt.matches(getEntityTagToCompare(entity))) {
                    return false;
                }
                else if(!this.flags.matches(entity)) {
                    return false;
                }
                else if(!this.equipment.matches(entity)) {
                    return false;
                }
                else {
                    return this.vehicle == this || this.vehicle == ANY || this.vehicle.matches(entity.getVehicle(), player);
                }
            }
        }
    }
}
