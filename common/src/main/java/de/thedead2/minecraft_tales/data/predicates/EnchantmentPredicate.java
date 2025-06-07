package de.thedead2.minecraft_tales.data.predicates;

import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.Nullable;
import java.util.Map;


public class EnchantmentPredicate implements SimpleTriggerPredicate<Map<Enchantment, Integer>> {

    public static final EnchantmentPredicate ANY = new EnchantmentPredicate(null, MinMax.ANY_INT);

    @Nullable
    private final Enchantment enchantment;

    private final MinMax<Integer> level;


    public EnchantmentPredicate(@Nullable Enchantment enchantment, MinMax<Integer> level) {
        this.enchantment = enchantment;
        this.level = level;
    }


    @Override
    public boolean matches(Map<Enchantment, Integer> enchantments) {
        if(this.enchantment != null) {
            if(!enchantments.containsKey(this.enchantment)) {
                return false;
            }

            int i = enchantments.get(this.enchantment);
            return this.level == MinMax.ANY_INT || this.level.matches(i);
        }
        else if(this.level != MinMax.ANY_INT) {
            for(Integer integer : enchantments.values()) {
                if(this.level.matches(integer)) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }
}
