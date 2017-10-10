package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Regrowth extends AbstractAle {

	private ComplexCooldown healOthersCD;

	Regrowth(Dwarf dwarf) {
		super(dwarf, 100);
		this.healOthersCD = new ComplexCooldown(20, this::tryHealOthers, null);
	}

	private final static CustomItem ITEM = DwarvenItems.getItem("ale.regrowth", Slot.MAIN_HAND);
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
	
	private void tryHealOthers() {
		Dwarf healee = dwarf.getLookingAt(3, 15, DwarfManager.getManager().getDwarves());
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
		
		
		healee.getArmour().repair(10);
		healee.heal(5);
		healee.regenMana(5);
	}
}
