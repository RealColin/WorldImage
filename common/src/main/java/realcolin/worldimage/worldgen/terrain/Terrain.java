package realcolin.worldimage.worldgen.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import realcolin.worldimage.WorldImageRegistries;

public record Terrain(DensityFunction continents, DensityFunction erosion, DensityFunction height, DensityFunction ridges, DensityFunction temperature) {

//    public static final Codec<Terrain> DIRECT_CODEC =
//            RecordCodecBuilder.create(yes -> yes.group(
//                    Codec.FLOAT.fieldOf("continents").forGetter(src -> src.continents),
//                    Codec.FLOAT.fieldOf("erosion").forGetter(src -> src.erosion),
//                    Codec.FLOAT.fieldOf("height").forGetter(src -> src.height),
//                    Codec.FLOAT.fieldOf("ridges").forGetter(src -> src.ridges)
//            ).apply(yes, Terrain::new));

    public static final Codec<Terrain> DIRECT_CODEC =
            RecordCodecBuilder.create(joe -> joe.group(
                    DensityFunction.DIRECT_CODEC.fieldOf("continents").forGetter(src -> src.continents),
                    DensityFunction.DIRECT_CODEC.fieldOf("erosion").forGetter(src -> src.erosion),
                    DensityFunction.DIRECT_CODEC.fieldOf("height").forGetter(src -> src.height),
                    DensityFunction.DIRECT_CODEC.fieldOf("ridges").forGetter(src -> src.ridges),
                    DensityFunction.DIRECT_CODEC.fieldOf("temperature").forGetter(src -> src.temperature)
            ).apply(joe, Terrain::new));

    public static final Codec<Holder<Terrain>> CODEC = RegistryFileCodec.create(WorldImageRegistries.TERRAIN, DIRECT_CODEC);
}
