package realcolin.worldimage;

import realcolin.worldimage.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public class WorldImageCommon {
    public static void init() {
        Constants.LOG.info("Beginning initialization of WorldImageCommon on {}...", Services.PLATFORM.getPlatformName());

        if (Services.PLATFORM.isModLoaded("worldimage")) {
            Constants.LOG.info("Initialization of WorldImageCommon complete.");
        } else {
            Constants.LOG.error("WorldImage is not loaded.");
        }
    }
}