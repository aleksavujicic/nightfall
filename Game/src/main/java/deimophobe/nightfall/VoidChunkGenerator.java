package deimophobe.nightfall;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Deimophobe on 30/11/17.
 */
public class VoidChunkGenerator extends ChunkGenerator {
	
	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
		return Bukkit.createChunkData(world);
	}
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Collections.emptyList();
	}
}
