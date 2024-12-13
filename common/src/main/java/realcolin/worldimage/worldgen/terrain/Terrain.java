package realcolin.worldimage.worldgen.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import realcolin.worldimage.WorldImageRegistries;

public record Terrain(float continents, float erosion, float height, float ridges) {

    public static final Codec<Terrain> DIRECT_CODEC =
            RecordCodecBuilder.create(yes -> yes.group(
                    Codec.FLOAT.fieldOf("continents").forGetter(src -> src.continents),
                    Codec.FLOAT.fieldOf("erosion").forGetter(src -> src.erosion),
                    Codec.FLOAT.fieldOf("height").forGetter(src -> src.height),
                    Codec.FLOAT.fieldOf("ridges").forGetter(src -> src.ridges)
            ).apply(yes, Terrain::new));

    public static final Codec<Holder<Terrain>> CODEC = RegistryFileCodec.create(WorldImageRegistries.TERRAIN, DIRECT_CODEC);
}
