package realcolin.worldimage.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public class RangeMap implements DensityFunction.SimpleFunction {
    public static final MapCodec<RangeMap> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("func").forGetter(src -> src.func),
            Codec.FLOAT.fieldOf("lower_noise").forGetter(src -> src.a1),
            Codec.FLOAT.fieldOf("upper_noise").forGetter(src -> src.a2),
            Codec.FLOAT.fieldOf("lower_value").forGetter(src -> src.b1),
            Codec.FLOAT.fieldOf("upper_value").forGetter(src -> src.b2)
    ).apply(instance, RangeMap::new));

    private DensityFunction func;
    private float a1;
    private float a2;
    private float b1;
    private float b2;

    public RangeMap(DensityFunction func, float a1, float a2, float b1, float b2) {
        this.func = func;
        this.a1 = a1;
        this.a2 = a2;
        this.b1 = b1;
        this.b2 = b2;
    }

    @Override
    public double compute(FunctionContext functionContext) {
        var x = func.compute(functionContext);

        var up = (x - a1) * (b2 - b1);
        var frac = up / (a2 - a1);
        var res = b1 + frac;

        return res;
    }

    @Override
    public double minValue() {
        return b1;
    }

    @Override
    public double maxValue() {
        return b2;
    }

    @Override
    public @NotNull DensityFunction mapAll(@NotNull Visitor visitor) {
        return new RangeMap(func.mapAll(visitor), a1, a2, b1, b2);
    }

    @Override
    public KeyDispatchDataCodec<RangeMap> codec() {
        return new KeyDispatchDataCodec<>(CODEC);
    }
}
