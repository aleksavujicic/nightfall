package deimophobe.nightfall.dwarf.light;

import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 8/12/18.
 */
public class BlockLightSource implements LightSource {
	private final int tickPerLight;
	
	public BlockLightSource(int tickPerLight) {
		this.tickPerLight = tickPerLight;
	}
	
	
	@Override
	public void apply(Dwarf dwarf, Vision vision) {
		Block block = dwarf.getLocation().getBlock();
		Block above = block.getRelative(0, 1, 0);
		
		int lightAtBlock = block.getLightLevel();
		int lightAbove = above.getLightLevel();
		
		int light = Math.max(lightAtBlock, lightAbove);
		
		int visionIncrease = Math.max((light - 6) * tickPerLight , 0);
		vision.increaseVision(visionIncrease);
	}
}
