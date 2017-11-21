package deimophobe.nightfall.dwarf.kit.elements.healing;

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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class Regrowth extends AbstractAle {
	private final static int MANA_COST = 100;
	
	private Dwarf target = null;
	private void resetTarget() { target = null; }
	private final ComplexCooldown targetClearer = new ComplexCooldown(20, null, this::resetTarget);
	
	private final ComplexCooldown healOthersCD = new ComplexCooldown(12, this::tryHealOthers);

	public Regrowth(Dwarf dwarf) {
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
		else if (Misc.isRightClick(action)) {
			healOthersCD.tryUse();
			otherHealSuccess = true;
		}

		return  (selfHealSuccess || otherHealSuccess);
	}

	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		healOthersCD.update();
		targetClearer.update();
	}

	private void tryHealOthers() {
		if (target == null || dwarf.distanceTo(target) > 20)
			target = dwarf.getLookingAt(3, 20, DwarfManager.getManager().getDwarves());
		
		targetClearer.reset();
		
		if (target == null) return;

		Location healerLoc = dwarf.getPlayer().getEyeLocation();
		Location healeeLoc = target.getPlayer().getEyeLocation();
		
		Vector direction = healeeLoc.subtract(healerLoc).toVector();
		double distance = direction.length();
		Vector delta = direction.multiply(0.5 / distance);
		Set<Location> locs = new HashSet<>();

		int times = (int) (distance / 0.5);
		for (int i = 0; i <= times; i++) {
			Location newLoc = healerLoc.add(delta.multiply(1));
			locs.add(newLoc.clone());
			if (newLoc.getBlock().getType().isSolid()) return;
		}
		
		if (!dwarf.tryUseMana(25)) return;
		
		dwarf.playSound("entity.experience_orb.pickup", 0.5f, 0.5f, false);
		target.playSound("entity.experience_orb.pickup", 0.5f, 0.5f, false);
		
		target.getArmour().repair(10);
		target.heal(5);
		target.regenMana(2);
		
		for (Location loc : locs) {
			dwarf.getPlayer().getWorld().spawnParticle(Particle.HEART, loc.subtract(0,1.2,0), 3, 0.1, 0.1, 0.1);
		}
	}
}
