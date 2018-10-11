package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.common.Misc;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wood;
import org.bukkit.material.Wool;

/**
 * Created by Deimophobe on 12/10/18.
 */
public enum BloodColour {
	RED() {
		private final MaterialData blockData = new MaterialData(Material.REDSTONE_BLOCK);
		
		@Override
		void showPrimaryBlood(Location center, int mana) {
			int count = 8000 / (mana + 100);
			double radius = 0.4 - (double) mana/2000;
			double height = 0.25 - (double) mana/3000;
			
			center.getWorld().spawnParticle(Particle.REDSTONE, center, count, radius, height, radius, 0);
		}
		
		@Override
		void showSecondaryBlood(Location center, int mana) {
			center.getWorld().spawnParticle(Particle.BLOCK_CRACK, center, 20 - mana/10, 0.2, 0.1, 0.2, 0, blockData);
		}
	},
	BLUE(){
		private final MaterialData secondaryBlockData = new Wool(DyeColor.LIGHT_BLUE);
		
		@Override
		void showPrimaryBlood(Location center, int mana) {
			int count = 8000 / (mana + 100);
			double radius = 0.4 - (double) mana/2000;
			double height = 0.25 - (double) mana/3000;
			
			//center.getWorld().spawnParticle(Particle.REDSTONE, center, count, radius, height, radius, 0);
			Misc.spawnColouredParticles(center, count, radius*2, height*2, radius*2, 0.1, 0.85, 1);
		}
		
		@Override
		void showSecondaryBlood(Location center, int mana) {
			center.getWorld().spawnParticle(Particle.BLOCK_CRACK, center, 20 - mana/10, 0.2, 0.1, 0.2, 0, secondaryBlockData);
		}
	},
	GREEN(){
		private final MaterialData secondaryBlockData = new Wool(DyeColor.LIME);
		
		@Override
		void showPrimaryBlood(Location center, int mana) {
			int count = 8000 / (mana + 100);
			double radius = 0.4 - (double) mana/2000;
			double height = 0.25 - (double) mana/3000;
			
			//center.getWorld().spawnParticle(Particle.REDSTONE, center, count, radius, height, radius, 0);
			Misc.spawnColouredParticles(center, count, radius*2, height*2, radius*2, 0.6, 1, 0.1);
		}
		
		@Override
		void showSecondaryBlood(Location center, int mana) {
			center.getWorld().spawnParticle(Particle.BLOCK_CRACK, center, 20 - mana/10, 0.2, 0.1, 0.2, 0, secondaryBlockData);
		}
	},
	
	;
	
	
	BloodColour() {
	}
	
	abstract void showPrimaryBlood(Location center, int mana);
	abstract void showSecondaryBlood(Location center, int mana);
}
