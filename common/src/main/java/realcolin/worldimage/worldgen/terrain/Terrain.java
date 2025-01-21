package realcolin.worldimage.worldgen.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import realcolin.worldimage.WorldImageRegistries;

public record Terrain(DensityFunction continents, DensityFunction erosion, DensityFunction ridges, DensityFunction height, DensityFunction temperature, DensityFunction humidity, DensityFunction base) {
    public static final Codec<Terrain> DIRECT_CODEC =
            RecordCodecBuilder.create(joe -> joe.group(
                    DensityFunction.DIRECT_CODEC.fieldOf("continents").forGetter(src -> src.continents),
                    DensityFunction.DIRECT_CODEC.fieldOf("erosion").forGetter(src -> src.erosion),
                    DensityFunction.DIRECT_CODEC.fieldOf("ridges").forGetter(src -> src.ridges),
                    DensityFunction.HOLDER_HELPER_CODEC.fieldOf("height").forGetter(src -> src.height),
                    DensityFunction.DIRECT_CODEC.fieldOf("temperature").forGetter(src -> src.temperature),
                    DensityFunction.DIRECT_CODEC.fieldOf("humidity").forGetter(src -> src.humidity),
                    DensityFunction.HOLDER_HELPER_CODEC.fieldOf("base").forGetter(src -> src.base)
            ).apply(joe, Terrain::new));

    public static final Codec<Holder<Terrain>> CODEC = RegistryFileCodec.create(WorldImageRegistries.TERRAIN, DIRECT_CODEC);
}
