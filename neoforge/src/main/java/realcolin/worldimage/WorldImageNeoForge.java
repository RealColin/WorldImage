package realcolin.worldimage;


import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import realcolin.worldimage.worldgen.biome.WorldImageBiomeSource;
import realcolin.worldimage.worldgen.densityfunction.ImageSampler;
import realcolin.worldimage.worldgen.densityfunction.RangeMap;
import realcolin.worldimage.worldgen.densityfunction.TerrainDist;
import realcolin.worldimage.worldgen.map.MapImage;
import realcolin.worldimage.worldgen.terrain.Terrain;

@Mod(Constants.MOD_ID)
public class WorldImageNeoForge {

    public WorldImageNeoForge(IEventBus eventBus) {
        WorldImageCommon.init();

        DENSITY_FUNCTIONS.register("terrain_dist", () -> TerrainDist.CODEC);
        DENSITY_FUNCTIONS.register("range_map", () -> RangeMap.CODEC);
        DENSITY_FUNCTIONS.register("image_sampler", () -> ImageSampler.CODEC);
        DENSITY_FUNCTIONS.register(eventBus);

        BIOME_SOURCES.register(Constants.BIOME_SOURCE_ID, () -> WorldImageBiomeSource.CODEC);
        BIOME_SOURCES.register(eventBus);

        eventBus.addListener(WorldImageNeoForge::registerData);
    }

    private static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTIONS = DeferredRegister.create(BuiltInRegistries.DENSITY_FUNCTION_TYPE.key(), Constants.MOD_ID);
    private static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(BuiltInRegistries.BIOME_SOURCE, Constants.MOD_ID);

    public static void registerData(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(WorldImageRegistries.TERRAIN, Terrain.DIRECT_CODEC);
        event.dataPackRegistry(WorldImageRegistries.MAP, MapImage.DIRECT_CODEC);
    }
}