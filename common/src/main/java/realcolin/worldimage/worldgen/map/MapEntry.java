package realcolin.worldimage.worldgen.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import realcolin.worldimage.worldgen.terrain.Terrain;

import java.util.List;

public record MapEntry(String ID, Holder<Biome> biome, Holder<Terrain> terrain) {
    public static final Codec<List<MapEntry>> ENTRY_CODEC =
            RecordCodecBuilder.<MapEntry>create(something -> something.group(
                    Codec.STRING.fieldOf("id").forGetter(src -> src.ID),
                    Biome.CODEC.fieldOf("biome").forGetter(src -> src.biome),
                    Terrain.CODEC.fieldOf("terrain").forGetter(src -> src.terrain)
            ).apply(something, MapEntry::new)).listOf();
}
