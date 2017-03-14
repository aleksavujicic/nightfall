package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/01/17.
 */
public abstract class DwarvenItem {
	protected final Dwarf dwarf;
	protected final ItemStack item;
	
	public ItemStack getItem() {
		return item;
	}
	
	protected DwarvenItem(Dwarf dwarf, ItemStack item) {
		this.dwarf = dwarf;
		this.item = item;
	}
	
	public boolean matchesItem(ItemStack toMatch) {
		if (item == null) return false;
		return item.isSimilar(toMatch);
	}
	public boolean isHoldingItem() {return matchesItem(dwarf.getHeldItem());}
	
	
	public float fractionComplete() {
		return -1;
	}
	
	// EVENTS
	public void update() {}
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {}
	public double onHit(GameEntity entity, DamageType type, double damage) {return damage;}
	public double onGotHit(GameEntity entity, DamageType type, double damage) {return damage;}
	public void onKill(GameEntity monster, DamageType type) {}
	public void onBlockBreak(Block block) {}
	public void onShift(boolean sneaking) {}
	
}
