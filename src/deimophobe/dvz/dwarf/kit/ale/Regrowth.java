package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Regrowth extends Ale {
	
	Regrowth(Dwarf dwarf) {
		super(dwarf, AleType.REGROWTH, 100);
	}
	
	@Override
	protected boolean ability(Action type) {
		if (isLeftClick(type)) {
			if (!useMana()) return false;
			
			Player player = dwarf.getPlayer();
			
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, false);
			dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
			
			
			return true;
		} else {
			Dwarf healee = dwarf.getLookingAt(3,15);
			if (healee == null) return false;
			if (!dwarf.useMana(20)) return false;
			
			dwarf.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
			healee.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
			
			
			Location healerLoc = dwarf.getPlayer().getEyeLocation().subtract(0, 1.2, 0);
			Location healeeLoc = healee.getPlayer().getEyeLocation().subtract(0, 1.2, 0);
			
			Vector direction = healeeLoc.subtract(healerLoc).toVector();
			double distance = direction.length();
			Vector delta = direction.multiply(0.5/distance);
			
			int times = (int) (distance/0.5);
			for (int i = 0; i<= times; i++) {
				Location newLoc = healerLoc.add(delta.multiply(1));
				dwarf.getPlayer().getWorld().spawnParticle(Particle.HEART, newLoc, 3, 0.1, 0.1, 0.1);
			}
			
			
			healee.repairArmour(50);
			healee.heal(5);
			healee.regenMana(5);
			return true;
		}
	}
}
