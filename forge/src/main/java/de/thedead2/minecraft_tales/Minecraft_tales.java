package de.thedead2.minecraft_tales;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Minecraft_tales {

    public Minecraft_tales() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();

    }
}
