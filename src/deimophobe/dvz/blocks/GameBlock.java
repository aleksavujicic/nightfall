package deimophobe.dvz.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class GameBlock {
	private final Material material;
	private final byte data;
	/** When comparing blocks, does data matter, or only the material? */
	private final boolean general;
	
	public GameBlock(Material material, byte data, boolean general) {
		this.material = material;
		this.data = data;
		this.general = general;
	}
	
	public GameBlock(Material material) {
		this(material, (byte) 0, true);
	}
	
	public GameBlock(Material material, byte data) {
		this(material, data, false);
	}
	
	public boolean isSimilar(Block block) {
		boolean dataMatches = (general || data == block.getData());
		return (block.getType() == material && dataMatches);
	}
	
	public void setBlock(Block block) {
		block.setType(material);
		block.setData(data);
	}
}
