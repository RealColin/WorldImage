package realcolin.worldimage.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.DensityFunctions;
import realcolin.worldimage.WorldImageRegistries;
import realcolin.worldimage.worldgen.map.MapImage;
import realcolin.worldimage.worldgen.terrain.Terrain;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;

public class ImageSampler implements DensityFunction.SimpleFunction {
    public static final MapCodec<ImageSampler> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MapImage.CODEC.fieldOf("map").forGetter(src -> src.map),
            TerrainField.CODEC.fieldOf("field").forGetter(src -> src.field)
    ).apply(instance, ImageSampler::new));

    private final Holder<MapImage> map;
    private final TerrainField field;
    private final HashMap<Terrain, DensityFunction> functions;

    public ImageSampler(Holder<MapImage> map, TerrainField field) {
        this(map, field, null);
    }

    public ImageSampler(Holder<MapImage> map, TerrainField field, HashMap<Terrain, DensityFunction> functions) {
        this.map = map;
        this.field = field;
        this.functions = functions;
    }

    @Override
    public double compute(FunctionContext functionContext) {
        Terrain terrain = map.value().getTerrain(functionContext.blockX(), functionContext.blockZ());

        if (functions.containsKey(terrain)) {
            return functions.get(terrain).compute(functionContext);
        }

        return field.read(terrain).compute(functionContext);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        var terrains = map.value().getTerrains();
        var tempFunctions = new HashMap<Terrain, DensityFunction>();

        for (var t : terrains) {
            var func = field.read(t).mapAll(visitor);
            tempFunctions.put(t, func);
        }

        return visitor.apply(new ImageSampler(this.map, this.field, tempFunctions));
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
