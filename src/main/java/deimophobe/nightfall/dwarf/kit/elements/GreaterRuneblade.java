package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.GameEntity;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 20/01/17.
 */
class GreaterRuneblade extends AbstractCooldownItem {
	private static final int CD_TIME = 400;
	
	GreaterRuneblade(Dwarf dwarf) {
		super(dwarf, CD_TIME);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.grb");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	}
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {
		if (type == DamageType.REGULAR_MELEE && isHoldingItem())
			dwarf.giveProc(ProcType.REGULAR);
		
		reduceCooldown(20);
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			Player player = dwarf.getPlayer();
			
			dwarf.playSound("dash", 1f, 1f, true);
			dwarf.giveProc(ProcType.RUNEDASH);
			player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(5));
			resetCooldown();
			
			return true;
		}
		return false;
	}
	
	private static final int SAFEFALL_TIME = 60;
	
	@Override
	public double onGotHit(GameEntity monster, DamageType type, double damage) {
		if (getCooldown() >= CD_TIME - SAFEFALL_TIME && type == DamageType.FALL) {
			return -1;
		}
		return damage;
	}
	
	@Override
	public void onOffCD() {
		dwarf.playSound("offcd", 1, 1.5f, false);
		new BukkitRunnable() {
			@Override
			public void run() {
				dwarf.playSound("offcd", 1, 2f, false);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 5);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		reduceCooldown();
		
	}
	
}
