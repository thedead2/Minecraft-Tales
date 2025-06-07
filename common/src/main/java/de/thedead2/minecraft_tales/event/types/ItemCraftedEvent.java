package de.thedead2.minecraft_tales.event.types;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class ItemCraftedEvent extends PlayerEvent{

    @NotNull
    private final ItemStack crafting;

    public ItemCraftedEvent(Player player, @NotNull ItemStack crafting) {
        super(player);
        this.crafting = crafting;
    }


    public @NotNull ItemStack getCraftedItem() {
        return crafting;
    }
}
