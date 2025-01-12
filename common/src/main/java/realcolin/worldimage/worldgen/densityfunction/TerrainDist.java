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

    public TerrainDist(Holder<MapImage> map, int restrict) {
        this.map = map;
        this.restrict = restrict;
    }


    // breadth first search
    @Override
    public double compute(@NotNull FunctionContext context) {
        var terrainToMatch = map.value().getTerrain(context.blockX(), context.blockZ());
        var queue = new LinkedList<Pair>();
        queue.add(new Pair(context.blockX(), context.blockZ()));

        int dist = 0;

        var visited = new HashMap<Pair, Boolean>();

        // dist variable isn't tracking distance it is tracking iterations - oops!
        // TODO fix above comment pls
        while (!queue.isEmpty() && dist < restrict) {

            var cur = queue.poll();
            visited.put(cur, true);

            // make sure these aren't visited or something lol
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
                return dist;
            }

            dist++;
        }
        return dist;
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
