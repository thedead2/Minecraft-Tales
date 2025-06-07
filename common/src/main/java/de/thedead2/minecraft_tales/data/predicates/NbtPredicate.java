package de.thedead2.minecraft_tales.data.predicates;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;


public class NbtPredicate implements SimpleTriggerPredicate<Tag> {

    public static final NbtPredicate ANY = new NbtPredicate(null);

    @Nullable
    private final CompoundTag tag;


    public NbtPredicate(@Nullable CompoundTag pTag) {
        this.tag = pTag;
    }


    @Override
    public boolean matches(Tag tag) {
        if(tag == null) {
            return this == ANY;
        }
        else {
            return this.tag == null || NbtUtils.compareNbt(this.tag, tag, true);
        }
    }
}
