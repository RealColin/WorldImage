package realcolin.worldimage.worldgen.biome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.NotNull;
import realcolin.worldimage.worldgen.map.MapEntry;
import realcolin.worldimage.worldgen.map.MapImage;

import java.util.stream.Stream;

public class WorldImageBiomeSource extends BiomeSource {

    public static final MapCodec<WorldImageBiomeSource> CODEC =
            RecordCodecBuilder.mapCodec(yes -> yes.group(
                    MapImage.CODEC.fieldOf("map").forGetter(src -> src.map)
            ).apply(yes, yes.stable(WorldImageBiomeSource::new)));

    private final Holder<MapImage> map;

    public WorldImageBiomeSource(Holder<MapImage> map) {
        this.map = map;
    }

    @Override
    protected @NotNull MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        return map.value().getEntries().stream().map(MapEntry::biome);
    }

    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.@NotNull Sampler sampler) {
        return map.value().getBiome(x * 4, z * 4);
    }
}
