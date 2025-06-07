package de.thedead2.minecraft_tales.data.predicates;

import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class ItemPredicate implements SimpleTriggerPredicate<ItemStack> {

    public static final ItemPredicate ANY = new ItemPredicate(null, MinMax.ANY_INT, Collections.emptySet(), Collections.emptySet(), NbtPredicate.ANY, null);

    private final MinMax<Integer> itemDurability;

    private final Item itemType;

    private final Set<EnchantmentPredicate> enchantments;

    private final Set<EnchantmentPredicate> storedEnchantments;

    private final NbtPredicate nbt;

    /**
     * If the item stack is a potion
     **/
    @Nullable
    private final Potion potion;


    public ItemPredicate(Item itemType, MinMax<Integer> itemDurability, Set<EnchantmentPredicate> enchantments, Set<EnchantmentPredicate> storedEnchantments, NbtPredicate nbt, @Nullable Potion potion) {
        this.itemType = itemType;
        this.itemDurability = itemDurability;
        this.enchantments = enchantments;
        this.storedEnchantments = storedEnchantments;
        this.nbt = nbt;
        this.potion = potion;
    }


    public static ItemPredicate from(Item item) {
        return new ItemPredicate(item, MinMax.ANY_INT, Collections.emptySet(), Collections.emptySet(), NbtPredicate.ANY, null);
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if(this == ANY) {
            return true;
        }
        else if(this.itemType != null && !this.itemType.equals(itemStack.getItem())) {
            return false;
        }
        else if(!this.itemDurability.isAny() && !itemStack.isDamageableItem()) {
            return false;
        }
        else if(!this.itemDurability.matches(itemStack.getMaxDamage() - itemStack.getDamageValue())) {
            return false;
        }
        else if(!this.nbt.matches(itemStack.)) {
            return false;
        }
        else {
            if(!this.enchantments.isEmpty()) {
                Map<Enchantment, Integer> map = itemStack.getAllEnchantments();

                for(EnchantmentPredicate enchantmentpredicate : this.enchantments) {
                    if(!enchantmentpredicate.matches(map)) {
                        return false;
                    }
                }
            }

            if(!this.storedEnchantments.isEmpty()) {
                Map<Enchantment, Integer> map1 = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(itemStack));

                for(EnchantmentPredicate storedEnchantment : this.storedEnchantments) {
                    if(!storedEnchantment.matches(map1)) {
                        return false;
                    }
                }
            }

            Potion potion = PotionUtils.getPotion(itemStack);
            return this.potion == null || this.potion == potion;
        }
    }
}
