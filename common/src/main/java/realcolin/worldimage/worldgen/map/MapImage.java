package realcolin.worldimage.worldgen.map;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;
import realcolin.worldimage.WorldImageRegistries;
import realcolin.worldimage.worldgen.terrain.Terrain;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MapImage {

    public static final Codec<MapImage> DIRECT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("image").forGetter(src -> src.res),
                    Codec.INT.fieldOf("ppi").forGetter(src -> src.ppi),
                    Biome.CODEC.fieldOf("default_biome").forGetter(src -> src.defaultBiome),
                    Terrain.CODEC.fieldOf("default_terrain").forGetter(src -> src.defaultTerrain),
                    MapEntry.ENTRY_CODEC.fieldOf("entries").forGetter(src -> src.entries)
            ).apply(instance, MapImage::new));

    public static final Codec<Holder<MapImage>> CODEC = RegistryFileCodec.create(WorldImageRegistries.MAP, DIRECT_CODEC);

    private final int ppi;
    private final ResourceLocation res;
    private final Holder<Biome> defaultBiome;
    private final Holder<Terrain> defaultTerrain;
    private final List<MapEntry> entries;
    private final GraphicsNode node;
    private final int width;
    private final int height;

    private final List<Terrain> terrains;

    private final ConcurrentHashMap<Pair<Integer, Integer>, BufferedImage> cache;

    /* I know I NEED the stuff below */
    private List<ShapeRegion> shapeRegions;

    public MapImage(ResourceLocation res, int ppi, Holder<Biome> defaultBiome, Holder<Terrain> defaultTerrain, List<MapEntry> entries) {
        this.res = res;
        this.ppi = ppi;
        this.defaultBiome = defaultBiome;
        this.defaultTerrain = defaultTerrain;
        this.entries = entries;
        this.cache = new ConcurrentHashMap<>();

        String PATH = "assets/%s/map/%s".formatted(res.getNamespace(), res.getPath());
        System.out.println(PATH);

        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            InputStream svgFile = MapImage.class.getResourceAsStream("/" + PATH);
            SVGDocument svgDocument = factory.createSVGDocument(null, svgFile);

            GVTBuilder builder = new GVTBuilder();
            BridgeContext ctx = new BridgeContext(new UserAgentAdapter());
            node = builder.build(ctx, svgDocument);

            this.width = Math.round((svgDocument.getRootElement().getWidth().getBaseVal().getValue() / 96) * ppi);
            this.height = Math.round((svgDocument.getRootElement().getHeight().getBaseVal().getValue() / 96) * ppi);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        terrains = new ArrayList<>();
        terrains.add(defaultTerrain.value());
        for (var entry : entries) {
            var t = entry.terrain().value();
            if (!terrains.contains(t))
                terrains.add(entry.terrain().value());
        }
    }

    public List<MapEntry> getEntries() {
        return entries;
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public Holder<Biome> getBiome(int x, int z) {
        int color = this.getColorAtPixel(x, z);

        if (color != -1) {
            for (var entry : entries) {
                var c = new Color(entry.color());
                if (c.getRGB() == color)
                    return entry.biome();
            }
        }
        return this.defaultBiome;
    }

    public Terrain getTerrain(int block_x, int block_z) {
        var point = convertToSVGCoordinates(block_x, block_z);
        ShapeRegion region = new ShapeRegion(null, defaultBiome.value(), defaultTerrain.value(), -1);

        for (var sr : shapeRegions) {
            if (sr.within(point) && sr.order() > region.order()) {
                region = sr;
            }
        }

        var ter = region.terrain();
        // return ter

        // TODO finish above, get rid of below

        int color = getColorAtPixel(block_x, block_z);

        if (color != -1) {
            for (var entry : entries) {
                var c = new Color(entry.color());
                if (c.getRGB() == color)
                    return entry.terrain().value();
            }
        }

        return this.defaultTerrain.value();
    }

    private Point convertToSVGCoordinates(int blockX, int blockZ) {
        // TODO math to convert them

        return new Point(blockX, blockZ);
    }


    private int getColorAtPixel(int x, int y) {
        if (outsideRange(x, y))
            return -1;

        int whatever = 16;
        var fort = new Pair<>(x / whatever, y / whatever);
        BufferedImage img;

        if (cache.containsKey(fort))
            img = cache.get(fort);
        else {
            img = new BufferedImage(whatever, whatever, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            int svgX = fort.getFirst() * whatever;
            int svgY = fort.getSecond() * whatever;
            g2d.translate(-svgX, -svgY);
            g2d.scale((double) ppi / 96.0, (double)ppi / 96.0);
            this.node.paint(g2d);

            cache.put(fort, img);
        }

        return img.getRGB(x % whatever, y % whatever);
    }

    private boolean outsideRange(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }
}
