package realcolin.worldimage;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import realcolin.worldimage.worldgen.densityfunction.ImageSampler;
import realcolin.worldimage.worldgen.map.MapImage;
import realcolin.worldimage.worldgen.terrain.Terrain;

import java.awt.event.InputEvent;

@Mod(Constants.MOD_ID)
public class WorldImageForge {

    private static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTIONS =
            DeferredRegister.create(BuiltInRegistries.DENSITY_FUNCTION_TYPE.key(), Constants.MOD_ID);

    public WorldImageForge(FMLJavaModLoadingContext context) throws ClassNotFoundException {
        WorldImageCommon.init();
        System.out.println(Class.forName("org.apache.batik.bridge.UserAgent"));
        IEventBus bus = context.getModEventBus();
        DENSITY_FUNCTIONS.register("image_sampler", () -> ImageSampler.CODEC);
        DENSITY_FUNCTIONS.register(bus);
        bus.addListener(WorldImageForge::registerData);


    }

    public static void registerData(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(WorldImageRegistries.TERRAIN, Terrain.DIRECT_CODEC, Terrain.DIRECT_CODEC);
        event.dataPackRegistry(WorldImageRegistries.MAP, MapImage.DIRECT_CODEC, MapImage.DIRECT_CODEC);
    }
}