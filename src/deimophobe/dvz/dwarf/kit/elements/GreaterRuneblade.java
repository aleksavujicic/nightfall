package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 20/01/17.
 */
class GreaterRuneblade extends AbstractCooldownItem {
	GreaterRuneblade(Dwarf dwarf) {
		super(dwarf, 400);
	}
	
	private final static ItemStack ITEM = DwarvenItems.getItem2("sword.grb2");
	@Override public ItemStack getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	}
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {
		if (type == DamageType.REGULAR_MELEE && isHoldingItem())
			dwarf.giveProc(Dwarf.ProcType.REGULAR);
		
		reduceCooldown(20);
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			Player player = dwarf.getPlayer();
			
			dwarf.playSound("dash", 1f, 1f, true);
			dwarf.giveProc(Dwarf.ProcType.RUNEDASH);
			player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(5));
			resetCooldown();
			
			return true;
		}
		return false;
	}
	
	@Override
	public double onGotHit(GameEntity monster, DamageType type, double damage) {
		if (getCooldown() >= 300 && type == DamageType.FALL) {
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
		}.runTaskLater(Game.getGame().getPlugin(), 5);
	}
}
