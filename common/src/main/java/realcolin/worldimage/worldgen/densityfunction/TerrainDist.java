package realcolin.worldimage.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;
import realcolin.worldimage.util.Pair;
import realcolin.worldimage.worldgen.map.MapImage;

import java.util.HashMap;
import java.util.LinkedList;

public class TerrainDist implements DensityFunction.SimpleFunction {
    public static final MapCodec<TerrainDist> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MapImage.CODEC.fieldOf("map").forGetter(src -> src.map),
            Codec.INT.fieldOf("restrict").forGetter(src -> src.restrict)
    ).apply(instance, TerrainDist::new));

    private final Holder<MapImage> map;
    private final int restrict;
    private final HashMap<Pair, Double> cache = new HashMap<>();

    public TerrainDist(Holder<MapImage> map, int restrict) {
        this.map = map;
        this.restrict = restrict;
    }

    int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // breadth first search
    @Override
    public double compute(@NotNull FunctionContext context) {
        var terrainToMatch = map.value().getTerrain(context.blockX(), context.blockZ());
        var base = new Pair(context.blockX(), context.blockZ());

        if (cache.containsKey(base)) {
            var a = cache.get(base);
            if (a != null) {
                return a;
            }
        }

        var queue = new LinkedList<Pair>();
        queue.add(base);

        double dist = 0;

        var visited = new HashMap<Pair, Boolean>();
        visited.put(base, true);

        while (!queue.isEmpty()) {
            var cur = queue.poll();
            dist = calcDist(cur, base);

            if (dist > restrict) {
                dist = restrict;
                break;
            }

            if (map.value().getTerrain(cur.x, cur.z) == terrainToMatch) {
                for (var dir : directions) {
                    var pair = new Pair(cur.x + dir[0], cur.z + dir[1]);

                    if (!visited.containsKey(pair)) {
                        queue.add(pair);
                        visited.put(pair, true);
                    }
                }
            } else {
                var val = Math.clamp(dist, minValue(), maxValue());
                cache.put(base, val);
                return val;
            }
        }
        cache.put(base, Math.clamp(dist, minValue(), maxValue()));
        return Math.clamp(dist, minValue(), maxValue());
    }

    private double calcDist(Pair one, Pair two) {
        return Math.sqrt(Math.pow(two.x - one.x, 2) + Math.pow(two.z - one.z, 2));
    }

    @Override
    public double minValue() {
        return 0;
    }

    @Override
    public double maxValue() {
        return restrict;
    }

    @Override
    public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return new KeyDispatchDataCodec<>(CODEC);
    }
}
