package de.thedead2.minecraft_tales.api;

import de.thedead2.minecraft_tales.player.MTPlayer;

@FunctionalInterface
public interface DeferrableAction<T> {

    boolean execute(MTPlayer player, T arg);
}
