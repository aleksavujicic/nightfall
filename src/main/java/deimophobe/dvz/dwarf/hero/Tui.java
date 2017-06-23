package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.monster.ai.AIEntity;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 12/03/17.
 */
class Tui extends Hero {
	protected Tui(Player player, Type type) {
		super(player, type);
		
		setMaxArrows(64);
	}
	
	private int flameCD = 0;
	private static final int MAX_FLAME_CD = 5*20;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		
		if (flameCD > 0) {
			flameCD--;
		} else {
			if (halfSec && !quadSec) {
				giveArrow();
			}
		}
		
		if (sec && Math.random() < 0.1) {
			Location loc = getEyeLocation();
			double dx = 0.8*Math.random() - 0.4;
			double dz = 0.8*Math.random() - 0.4;
			loc.add(dx, 0.3, dz);
			
			double vx = 0.1 * Math.random() - 0.05;
			double vy = 0.05 * Math.random() + 0.05;
			double vz = 0.1 * Math.random() - 0.05;
			
			loc.getWorld().spawnParticle(Particle.FLAME, loc, 0, vx, vy, vz, 1);
		}
	}
	
	@Override
	public void giveArrow() {
		if (flameCD == 0)
			super.giveArrow();
	}
	
	@Override
	public void useArrows(int amt) {
		flameCD = MAX_FLAME_CD;
		super.useArrows(amt);
	}
	
	@Override
	protected ItemStack getArrow() {
		return fuel;
	}
	
	private static final ItemStack fuel = DwarvenItems.createItemStack("misc.wildfirefuel");
	
	@Override
	public double onGotHit(GameEntity entity, DamageType type, double damage) {
		damage = super.onGotHit(entity, type, damage);
		if (entity instanceof AIEntity) damage = 0;
		return damage;
	}
	
	@Override
	public void notifyDeath(Dwarf dwarf) {
		super.notifyDeath(dwarf);
		if (dwarf == this) {
			playSound("dwarf.hero.tui.death", 1000, 1, true);
		}
	}
}
