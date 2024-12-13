package realcolin.worldimage.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import realcolin.worldimage.worldgen.terrain.Terrain;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TerrainField implements StringRepresentable {
    CONTINENTS("continents") {
        @Override
        public float read(Terrain terrain) {
            return terrain.continents();
        }
    },
    EROSION("erosion") {
        @Override
        public float read(Terrain terrain) {
            return terrain.erosion();
        }
    },
    HEIGHT("height") {
        @Override
        public float read(Terrain terrain) {
            return terrain.height();
        }
    },
    RIDGES("ridges") {
        @Override
        public float read(Terrain terrain) {
            return terrain.ridges();
        }
    },
    TEMPERATURE("temperature") {
        @Override
        public float read(Terrain terrain) {
            return -0.3f;
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

    public abstract float read(Terrain terrain);
}
