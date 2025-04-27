package de.thedead2.minecraft_tales.event.types;

import net.minecraft.world.entity.player.Player;


public class PlayerLoggedInEvent extends PlayerEvent {

    public PlayerLoggedInEvent(Player player) {
        super(player);
    }
}
