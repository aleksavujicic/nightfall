package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Deimophobe on 20/01/17.
 */
class GreaterRuneblade extends AbstractCooldownItem {
	private static final int CD_TIME = 400;
	
	GreaterRuneblade(Dwarf dwarf) {
		super(dwarf, CD_TIME);
	}
	
	private final static ItemStack ITEM;
	static {
		CustomItem item = DwarvenItems.getItem("sword.grb2", Collections.singletonMap("test", "this is a test"));
		item.addModifier(ItemModifierType.ATTACK, 10, "This is a test");
		item.addModifier(ItemModifierType.SPEED, 10, "Also a test");
		item.addModifier(ItemModifierType.SPEED, -100, "Bad a test");
		item.addModifier(ItemModifierType.KB_RESIST, 1);
		item.addModifier(ItemModifierType.HEALTH, 5, "HEARTS");
		item.addModifier(ItemModifierType.HEALTH, 20, "MOAR HEARTS");
		ITEM = item.createItem();
	}
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
		}.runTaskLater(Game.getGame().getPlugin(), 5);
	}
}
