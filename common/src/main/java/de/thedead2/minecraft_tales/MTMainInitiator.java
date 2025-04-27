package de.thedead2.minecraft_tales;


import de.thedead2.minecraft_tales.api.services.MTInstance;
import de.thedead2.minecraft_tales.platform.MTClientInstance;
import de.thedead2.minecraft_tales.platform.MTServerInstance;

import static de.thedead2.minecraft_tales.MTGlobalConstants.*;


public class MTMainInitiator {

    private static MTInstance instance;

    public static void init() {
        instance = switch (PLATFORM.getGameSide()) {
            case SERVER -> new MTServerInstance(PLATFORM.getServer().get());
            default -> new MTClientInstance();
        };

        instance.init();
    }

    public static MTInstance getModInstance() {
        return instance;
    }
}
