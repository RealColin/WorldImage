package realcolin.worldimage.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import realcolin.worldimage.worldgen.map.MapImage;
import realcolin.worldimage.worldgen.terrain.Terrain;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public record ImageSampler(Holder<MapImage> map, TerrainField field) implements DensityFunction.SimpleFunction {
    public static final MapCodec<ImageSampler> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MapImage.CODEC.fieldOf("map").forGetter(ImageSampler::map),
            TerrainField.CODEC.fieldOf("field").forGetter(ImageSampler::field)
    ).apply(instance, ImageSampler::new));

    static boolean joe = true;

    @Override
    public double compute(FunctionContext functionContext) {
        Terrain terrain = map.value().getTerrain(functionContext.blockX(), functionContext.blockZ());

        var a = field.read(terrain);
        if (field == TerrainField.CONTINENTS && joe) {
            System.out.println(a.compute(functionContext));
            joe = false;
        }


        return field.read(terrain).compute(functionContext);
    }

    @Override
    public double minValue() {
        return -1.0;
    }

    @Override
    public double maxValue() {
        return 1.0;
    }

    @Override
    public @NotNull KeyDispatchDataCodec<ImageSampler> codec() {
        return new KeyDispatchDataCodec<>(CODEC);
    }
}
