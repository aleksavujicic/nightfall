package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

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
		if (Game.getGame().potionsDisabled()) {
			return false;
		}
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
	
	private static final double AOE_REGEN_RANGE = 5;
	@Override
	protected void heal() {
		super.heal();
		
		dwarf.givePotionEffect(PotionEffectType.REGENERATION, 10*20, 3, true, false, false);
		dwarf.playSound("healing", 0.5f, 2f, false);
		
		for (Dwarf other : DwarfManager.getManager().getDwarves()) {
			if (other == dwarf) continue;
			if (other.distanceTo(dwarf) > AOE_REGEN_RANGE) continue;
			
			other.heal(8);
			other.givePotionEffect(PotionEffectType.REGENERATION, 5*20, 1, true, false, false);
			other.playSound("healing", 0.5f, 2f, false);
		}
	}
	
	private void tryHealOthers() {
		if (target == null || dwarf.distanceTo(target) > 20)
			target = dwarf.getLookingAt(20, 3, DwarfManager.getManager().getDwarves());
		
		targetClearer.reset();
		
		if (target == null) return;
		if (!dwarf.hasMana(25)) return;
		boolean canConnect = dwarf.canConnectToPlayer(target, 0.5,
				(location) -> location.getWorld().spawnParticle(Particle.HEART, location.subtract(0,1.2,0), 3, 0.1, 0.1, 0.1)
		);
		if (!canConnect) return;
		
		dwarf.useMana(25);
		
		dwarf.playSound("healing", 0.5f, 1f, false);
		target.playSound("healing", 0.5f, 1f, false);
		
		target.getArmour().repair(8);
		target.heal(5);
		target.regenMana(2);
	}
}
