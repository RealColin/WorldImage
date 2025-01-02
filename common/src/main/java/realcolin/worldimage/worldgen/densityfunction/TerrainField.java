package realcolin.worldimage.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.DensityFunction;
import realcolin.worldimage.worldgen.terrain.Terrain;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TerrainField implements StringRepresentable {
    CONTINENTS("continents") {
        @Override
        public DensityFunction read(Terrain terrain) {
            return terrain.continents();
        }
    },
    EROSION("erosion") {
        @Override
        public DensityFunction read(Terrain terrain) {
            return terrain.erosion();
        }
    },
    HEIGHT("height") {
        @Override
        public DensityFunction read(Terrain terrain) {
            return terrain.height();
        }
    },
    RIDGES("ridges") {
        @Override
        public DensityFunction read(Terrain terrain) {
            return terrain.ridges();
        }
    },
    TEMPERATURE("temperature") {
        @Override
        public DensityFunction read(Terrain terrain) {
            return terrain.temperature();
        }
    },
    HUMIDITY("humidity") {
        @Override
        public DensityFunction read(Terrain terrain) {
            return terrain.humidity();
        }
    };

    public static final Codec<TerrainField> CODEC = StringRepresentable.fromEnum(TerrainField::values);
    private final String name;

    TerrainField(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public abstract DensityFunction read(Terrain terrain);
}
