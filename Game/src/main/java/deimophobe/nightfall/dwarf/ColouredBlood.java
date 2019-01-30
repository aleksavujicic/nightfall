package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.common.Misc;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;

/**
 * Created by Deimophobe on 13/10/18.
 */
public class ColouredBlood implements BloodDisplay {
	private final Particle.DustOptions dustOptions;
	private final BlockData secondaryBlockData;
	
	public ColouredBlood(Color color, BlockData secondaryBlockData) {
		this.dustOptions = new Particle.DustOptions(color, 1);
		this.secondaryBlockData = secondaryBlockData;
	}
	
	@Override
	public void showPrimaryBlood(Location center, int mana) {
		int count = 8000 / (mana + 100);
		double radius = 0.4 - (double) mana/2000;
		double height = 0.25 - (double) mana/3000;
		
		center.getWorld().spawnParticle(Particle.REDSTONE, center, count, radius, height, radius, dustOptions);
	}
	
	@Override
	public void showSecondaryBlood(Location center, int mana) {
		center.getWorld().spawnParticle(Particle.BLOCK_CRACK, center, 20 - mana/10, 0.2, 0.1, 0.2, 0, secondaryBlockData);
	}
}
