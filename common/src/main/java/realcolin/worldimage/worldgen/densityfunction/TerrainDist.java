package realcolin.worldimage.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;
import realcolin.worldimage.util.Pair;
import realcolin.worldimage.worldgen.map.MapImage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TerrainDist implements DensityFunction.SimpleFunction {
    public static final MapCodec<TerrainDist> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MapImage.CODEC.fieldOf("map").forGetter(src -> src.map),
            Codec.INT.fieldOf("restrict").forGetter(src -> src.restrict)
    ).apply(instance, TerrainDist::new));

    private final Holder<MapImage> map;
    private final int restrict;
    private HashMap<Pair, Double> cache = new HashMap<>();

    public TerrainDist(Holder<MapImage> map, int restrict) {
        this.map = map;
        this.restrict = restrict;
    }


    // breadth first search
    @Override
    public double compute(@NotNull FunctionContext context) {
        var terrainToMatch = map.value().getTerrain(context.blockX(), context.blockZ());
        var base = new Pair(context.blockX(), context.blockZ());

        if (cache.containsKey(base)) {
            return cache.get(base);
        }

        var queue = new LinkedList<Pair>();
        queue.add(base);

        double dist = 0;

        var visited = new HashMap<Pair, Boolean>();

        while (!queue.isEmpty()) {
            var cur = queue.poll();
            visited.put(cur, true);
            dist = calcDist(cur, base);

            if (dist > restrict) {
                continue;
            }

            if (map.value().getTerrain(cur.x, cur.z) == terrainToMatch) {
                if (!visited.containsKey(new Pair(cur.x - 1, cur.z)))
                    queue.add(new Pair(cur.x - 1, cur.z));

                if (!visited.containsKey(new Pair(cur.x + 1, cur.z)))
                    queue.add(new Pair(cur.x + 1, cur.z));

                if (!visited.containsKey(new Pair(cur.x, cur.z + 1)))
                    queue.add(new Pair(cur.x, cur.z + 1));

                if (!visited.containsKey(new Pair(cur.x, cur.z - 1)))
                    queue.add(new Pair(cur.x, cur.z - 1));
            } else {
                cache.put(base, Math.clamp(dist, minValue(), maxValue()));
                return Math.clamp(dist, minValue(), maxValue());
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
