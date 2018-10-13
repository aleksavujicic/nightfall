package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.common.Misc;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.material.MaterialData;

/**
 * Created by Deimophobe on 13/10/18.
 */
public class ColouredBlood implements BloodDisplay {
	private final double r;
	private final double g;
	private final double b;
	private final MaterialData secondaryBlockData;
	
	public ColouredBlood(double r, double g, double b, MaterialData secondaryBlockData) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.secondaryBlockData = secondaryBlockData;
	}
	
	@Override
	public void showPrimaryBlood(Location center, int mana) {
		int count = 8000 / (mana + 100);
		double radius = 0.4 - (double) mana/2000;
		double height = 0.25 - (double) mana/3000;
		
		Misc.spawnColouredParticles(center, count, radius*2, height*2, radius*2, r, g, b);
	}
	
	@Override
	public void showSecondaryBlood(Location center, int mana) {
		center.getWorld().spawnParticle(Particle.BLOCK_CRACK, center, 20 - mana/10, 0.2, 0.1, 0.2, 0, secondaryBlockData);
	}
}
