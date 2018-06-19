package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class Regrowth extends AbstractAle {
	private static final int MANA_COST = 100;
	private static final int HEAL_COST = 20;
	private static final char ARMOUR_CHAR = (char) 0x9000;
	private static final char FULL_CHAR = (char) 0x25C6;
	private static final char EMPTY_CHAR = (char) 0x25C7;
	
	private final static CustomItem ITEM = getAle("regrowth", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	private Dwarf target = null;
	private void resetTarget() { target = null; }
	private final Cooldown targetClearer = new ComplexCooldown(16, null, this::resetTarget);
	
	private final Cooldown healOthersCD = new UseCooldown(12, this::tryHealOthers);

	public Regrowth(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (Game.getGame().potionsDisabled()) return false;
		
		if (click.isLeftClick()) {
			return super.onUse(click, clickedBlock, blockFace);
		} else {
			healOthersCD.tryUse();
			return true;
		}
	}

	@Override
	public void update() {
		super.update();
		healOthersCD.update();
		targetClearer.update();
	}
	
	private static final double AOE_REGEN_RANGE = 5;
	@Override
	protected void heal() {
		super.heal();
		
		dwarf.givePotionEffect(PotionEffectType.REGENERATION, 10*20, 3, true, false, false);
		dwarf.playSound("healing", 0.5f, 2f, false);
		
		Location location = dwarf.getEyeLocation();
		location.getWorld().spawnParticle(Particle.HEART, location,1, 0, 0, 0);
		
		
		for (Dwarf other : DwarfManager.getManager().getDwarves()) {
			if (other == dwarf) continue;
			if (other.distanceTo(dwarf) > AOE_REGEN_RANGE) continue;
			
			other.heal(8);
			other.givePotionEffect(PotionEffectType.REGENERATION, 5*20, 1, true, false, false);
			other.playSound("healing", 0.5f, 2f, false);
		}
	}
	
	private void tryHealOthers() {
		if (!isTargetValid()) {
			target = dwarf.getLookingAt(20, 3, DwarfManager.getManager().getDwarves());
		}
		
		targetClearer.reset();
		
		if (!isTargetValid()) return;
		if (!dwarf.hasMana(HEAL_COST)) return;
		boolean canConnect = dwarf.canConnectToPlayer(target, 0.5,
				(location) -> location.getWorld().spawnParticle(Particle.HEART, location.subtract(0,1.2,0), 3, 0.1, 0.1, 0.1)
		);
		if (!canConnect) return;
		
		// Heal the target
		dwarf.useMana(HEAL_COST);
		
		dwarf.playSound("healing", 0.5f, 1f, false);
		target.playSound("healing", 0.5f, 1f, false);
		
		target.getArmour().repair(8);
		target.heal(5);
		target.regenMana(2);
		
		
		// Show info about target armour
		double fullness = target.getArmour().getFullness();
		ChatColor colour;
		if (fullness <= 0.1) {
			colour = ChatColor.DARK_GRAY;
		} else if (fullness <= 0.3) {
			colour = ChatColor.GRAY;
		} else if (fullness <= 0.6) {
			colour = ChatColor.GOLD;
		} else if (fullness < 1) {
			colour = ChatColor.YELLOW;
		} else {
			colour = ChatColor.AQUA;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(colour.toString());
		sb.append(ARMOUR_CHAR);
		sb.append(' ');
		
		int numFulls = (int) (10 * fullness);
		int numEmtpys = 10 - numFulls;
		Util.doNTimes(numFulls, () -> sb.append(FULL_CHAR));
		Util.doNTimes(numEmtpys, () -> sb.append(EMPTY_CHAR));
		
		sb.append(' ');
		sb.append(ARMOUR_CHAR);
		
		dwarf.sendTitleMessage(sb.toString());
	}
	
	private boolean isTargetValid() {
		return (target != null)
				&& target.isOnline()
				&& (dwarf.distanceTo(target) <= 20)
		;
	}
}
