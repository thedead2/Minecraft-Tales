package de.thedead2.minecraft_tales;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MTGlobalConstants.MOD_ID)
public class Minecraft_tales {

    public Minecraft_tales(IEventBus eventBus) {
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        MTGlobalConstants.LOGGER.info("Hello NeoForge world!");
        MTMainInitiator.init();
    }
}
