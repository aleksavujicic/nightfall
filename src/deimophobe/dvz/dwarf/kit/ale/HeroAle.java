package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 10/03/17.
 */
class HeroAle extends Ale {
	public HeroAle(Dwarf dwarf) {
		super(dwarf, AleType.HERO);
	}
	
	@Override
	public double onGotHit(GameEntity monster, DamageType type, double damage) {
		double health = dwarf.getPlayer().getHealth();
		if (health - damage <= 0.1 || health <= 16) {
			if (dwarf.tryUseMana(50)) {
				dwarf.healMax();
				dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, false);
				dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
			}
		}
		return damage;
	}
}
