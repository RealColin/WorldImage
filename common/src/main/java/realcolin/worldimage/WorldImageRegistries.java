package realcolin.worldimage;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import realcolin.worldimage.worldgen.map.MapImage;
import realcolin.worldimage.worldgen.terrain.Terrain;

public class WorldImageRegistries {
    public static final ResourceKey<Registry<Terrain>> TERRAIN = ResourceKey.createRegistryKey(ResourceLocation.parse("worldgen/terrain"));
    public static final ResourceKey<Registry<MapImage>> MAP = ResourceKey.createRegistryKey(ResourceLocation.parse("worldgen/map"));
}
