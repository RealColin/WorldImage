package realcolin.worldimage;


import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAElement;
import org.w3c.dom.svg.SVGDocument;
import realcolin.worldimage.worldgen.densityfunction.ImageSampler;
import realcolin.worldimage.worldgen.densityfunction.RangeMap;
import realcolin.worldimage.worldgen.map.MapImage;
import realcolin.worldimage.worldgen.terrain.Terrain;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

@Mod(Constants.MOD_ID)
public class WorldImageNeoForge {

    public WorldImageNeoForge(IEventBus eventBus) {
        WorldImageCommon.init();

        DENSITY_FUNCTIONS.register("range_map", () -> RangeMap.CODEC);
        DENSITY_FUNCTIONS.register("image_sampler", () -> ImageSampler.CODEC);
        DENSITY_FUNCTIONS.register(eventBus);

        eventBus.addListener(WorldImageNeoForge::registerData);
    }

    private static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTIONS = DeferredRegister.create(BuiltInRegistries.DENSITY_FUNCTION_TYPE.key(), Constants.MOD_ID);

    public static void registerData(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(WorldImageRegistries.TERRAIN, Terrain.DIRECT_CODEC);
        event.dataPackRegistry(WorldImageRegistries.MAP, MapImage.DIRECT_CODEC);
    }
}