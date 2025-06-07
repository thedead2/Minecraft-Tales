package de.thedead2.minecraft_tales.data.predicates;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;


public class EntityTypePredicate implements SimpleTriggerPredicate<EntityType<?>> {

    public static final EntityTypePredicate ANY = new EntityTypePredicate(null, null);

    @Nullable
    private final EntityType<?> type;

    @Nullable
    private final TagKey<EntityType<?>> tag;


    public EntityTypePredicate(@Nullable EntityType<?> type, @Nullable TagKey<EntityType<?>> tag) {
        this.type = type;
        this.tag = tag;
    }


    @Override
    public boolean matches(EntityType<?> entityType) {
        if(this == ANY) {
            return true;
        }
        else {
            if(this.type != null) {
                return this.type == entityType;
            }
            else if(this.tag != null) {
                return entityType.is(this.tag);
            }
        }
        return true;
    }
}
