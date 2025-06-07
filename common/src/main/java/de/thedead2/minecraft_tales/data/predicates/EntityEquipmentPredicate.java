package de.thedead2.minecraft_tales.data.predicates;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;


public class EntityEquipmentPredicate implements SimpleTriggerPredicate<Entity> {

    public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);

    private final ItemPredicate head;

    private final ItemPredicate chest;

    private final ItemPredicate legs;

    private final ItemPredicate feet;

    private final ItemPredicate mainhand;

    private final ItemPredicate offhand;


    public EntityEquipmentPredicate(ItemPredicate head, ItemPredicate chest, ItemPredicate legs, ItemPredicate feet, ItemPredicate mainhand, ItemPredicate offhand) {
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.mainhand = mainhand;
        this.offhand = offhand;
    }


    @Override
    public boolean matches(Entity entity) {
        if(this == ANY) {
            return true;
        }
        else if(!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        else {
            if(!this.head.matches(livingEntity.getItemBySlot(EquipmentSlot.HEAD))) {
                return false;
            }
            else if(!this.chest.matches(livingEntity.getItemBySlot(EquipmentSlot.CHEST))) {
                return false;
            }
            else if(!this.legs.matches(livingEntity.getItemBySlot(EquipmentSlot.LEGS))) {
                return false;
            }
            else if(!this.feet.matches(livingEntity.getItemBySlot(EquipmentSlot.FEET))) {
                return false;
            }
            else if(!this.mainhand.matches(livingEntity.getItemBySlot(EquipmentSlot.MAINHAND))) {
                return false;
            }
            else {
                return this.offhand.matches(livingEntity.getItemBySlot(EquipmentSlot.OFFHAND));
            }
        }
    }
}
