package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 12/03/17.
 */
class Tui extends Hero {
	protected Tui(Player player, HeroType type) {
		super(player, type);
		
		arrowRegen = new ComplexCooldown(1);
	}
	
	private final ComplexCooldown flameCD = new ComplexCooldown(5*20);
	private final ComplexCooldown arrowGiver = new ComplexCooldown(25, this::giveArrow);
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		
		flameCD.update();
		arrowGiver.update();
		if (flameCD.isAvailable()) arrowGiver.tryUse();
		
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
	public void useArrows(int amt) {
		super.useArrows(amt);
		flameCD.reset();
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getAttacker() instanceof AIEntity)
			damage.softCancel();
		
	}
	
	@Override
	public void notifyDeath(Dwarf dwarf) {
		super.notifyDeath(dwarf);
		if (dwarf == this) {
			playSound("dwarf.hero.tui.death", 1000, 1, true);
		}
	}
}
