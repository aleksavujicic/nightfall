package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitItemElement;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 24/03/17.
 */
abstract class AbstractItem extends AbstractElement implements KitItemElement {
	
	public AbstractItem(Dwarf dwarf) {
		super(dwarf);
	}
	
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		if (type == DamageType.REGULAR_MELEE && isHoldingItem())
			return onSelfHit(monster, type, damage);
		else
			return damage;
	}
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {
		if (type == DamageType.REGULAR_MELEE && isHoldingItem())
			onSelfKill(monster, type);
	}
	
	
	@Override
	public boolean matchesItem(ItemStack toMatch) {
		if (toMatch == null) return false;
		return getItem().isSimilar(toMatch);
	}
	
	@Override
	public boolean isHoldingItem() {
		return (matchesItem(dwarf.getHeldItem()));
	}
	
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {return false;}
	
	@Override
	public void onBlockBreak(Block block) {}
	
	public double onSelfHit(GameEntity monster, DamageType type, double damage) {return damage;}
	public void onSelfKill(GameEntity monster, DamageType type) {}
}
