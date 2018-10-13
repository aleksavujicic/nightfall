package deimophobe.nightfall.dwarf;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.material.MaterialData;

/**
 * Created by Deimophobe on 13/10/18.
 */
public interface BloodDisplay {
	void showPrimaryBlood(Location center, int mana);
	void showSecondaryBlood(Location center, int mana);
	
	
	BloodDisplay DEFAULT = new BloodDisplay() {
		private final MaterialData blockData = new MaterialData(Material.REDSTONE_BLOCK);
		
		@Override
		public void showPrimaryBlood(Location center, int mana) {
			int count = 8000 / (mana + 100);
			double radius = 0.4 - (double) mana/2000;
			double height = 0.25 - (double) mana/3000;
			
			center.getWorld().spawnParticle(Particle.REDSTONE, center, count, radius, height, radius, 0);
		}
		
		@Override
		public void showSecondaryBlood(Location center, int mana) {
			center.getWorld().spawnParticle(Particle.BLOCK_CRACK, center, 20 - mana/10, 0.2, 0.1, 0.2, 0, blockData);
		}
	};
}
