package realcolin.worldimage.worldgen.map;

import net.minecraft.world.level.biome.Biome;
import realcolin.worldimage.worldgen.terrain.Terrain;

import java.awt.*;

public record ShapeRegion(Shape shape, Biome biome, Terrain terrain, int order) {
    public boolean within(Point point) {
        // TODO implement this - return true if the point is in/on the shape
        return false;
    }
}
