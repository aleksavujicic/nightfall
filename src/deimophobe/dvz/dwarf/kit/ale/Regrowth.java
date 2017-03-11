package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Regrowth extends Ale {
	
	Regrowth(Dwarf dwarf) {
		super(dwarf, AleType.REGROWTH);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (isOffCD()) {
			if (Misc.isLeftClick(action)) {
				if (!dwarf.tryUseMana(100)) return;
				
				Player player = dwarf.getPlayer();
				
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, false);
				dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
			} else {
				Dwarf healee = dwarf.getLookingAt(3, 15, DwarfManager.getManager());
				if (healee == null) return;
				if (!dwarf.tryUseMana(20)) return;
				
				dwarf.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
				healee.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
				
				
				Location healerLoc = dwarf.getPlayer().getEyeLocation().subtract(0, 1.2, 0);
				Location healeeLoc = healee.getPlayer().getEyeLocation().subtract(0, 1.2, 0);
				
				Vector direction = healeeLoc.subtract(healerLoc).toVector();
				double distance = direction.length();
				Vector delta = direction.multiply(0.5 / distance);
				
				int times = (int) (distance / 0.5);
				for (int i = 0; i <= times; i++) {
					Location newLoc = healerLoc.add(delta.multiply(1));
					dwarf.getPlayer().getWorld().spawnParticle(Particle.HEART, newLoc, 3, 0.1, 0.1, 0.1);
				}
				
				
				healee.repairArmour(50);
				healee.heal(5);
				healee.regenMana(5);
			}
			resetCooldown();
		}
	}
}
