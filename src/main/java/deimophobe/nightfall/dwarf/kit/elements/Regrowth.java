package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Regrowth extends AbstractAle {
	private final static int MANA_COST = 100;

	private final ComplexCooldown healOthersCD = new ComplexCooldown(20, this::tryHealOthers);

	Regrowth(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("regrowth", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		boolean selfHealSuccess = false;
		boolean otherHealSuccess = false;
		if (Misc.isLeftClick(action)) {
			selfHealSuccess = super.onUse(action, clickedBlock, blockFace);
		}
		else if (Misc.isRightClick(action) && cooldown.isAvailable()) {
			healOthersCD.tryUse();
			otherHealSuccess = true;
		}

		return  (selfHealSuccess || otherHealSuccess);
	}

	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		healOthersCD.update();
	}

	private void tryHealOthers() {
		Dwarf healee = dwarf.getLookingAt(3, 15, DwarfManager.getManager().getDwarves());
		if (healee == null) return;
		if (!dwarf.tryUseMana(20)) return;

		Location healerLoc = dwarf.getPlayer().getEyeLocation().subtract(0, 1.2, 0);
		Location healeeLoc = healee.getPlayer().getEyeLocation().subtract(0, 1.2, 0);
		
		Vector direction = healeeLoc.subtract(healerLoc).toVector();
		double distance = direction.length();
		Vector delta = direction.multiply(0.5 / distance);
		List<Location> locs = new ArrayList<>();

		int times = (int) (distance / 0.5);
		for (int i = 0; i <= times; i++) {
			Location newLoc = healerLoc.add(delta.multiply(1));
			locs.add(newLoc);
			if (newLoc.getBlock().getType().isSolid()) return;
		}
		for (Location loc : locs) {
			dwarf.getPlayer().getWorld().spawnParticle(Particle.HEART, loc, 3, 0.1, 0.1, 0.1);
		}

		dwarf.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
		healee.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
		
		healee.getArmour().repair(15);
		healee.heal(5);
		healee.regenMana(5);
	}
}
