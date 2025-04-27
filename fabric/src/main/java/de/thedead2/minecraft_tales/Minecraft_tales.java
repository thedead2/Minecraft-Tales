package de.thedead2.minecraft_tales;

import net.fabricmc.api.ModInitializer;

public class Minecraft_tales implements ModInitializer {

    @Override
    public void onInitialize() {

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        MTGlobalConstants.LOGGER.info("Hello Fabric world!");
        MTMainInitiator.init();
    }
}
