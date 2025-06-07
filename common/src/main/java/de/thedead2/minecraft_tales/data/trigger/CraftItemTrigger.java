package de.thedead2.minecraft_tales.data.trigger;

import de.thedead2.minecraft_tales.data.predicates.ItemPredicate;
import de.thedead2.minecraft_tales.data.predicates.PlayerPredicate;
import de.thedead2.minecraft_tales.event.types.ItemCraftedEvent;
import net.minecraft.world.item.ItemStack;


public class CraftItemTrigger extends SimpleConditionTrigger<ItemCraftedEvent, ItemStack> {

    protected CraftItemTrigger(PlayerPredicate playerPredicate, ItemPredicate predicate) {
        super(playerPredicate, predicate);
    }


    @Override
    public boolean onEvent(ItemCraftedEvent event) {
        return this.test(getPlayerData(event.getPlayer()), event.getCraftedItem());
    }
}
