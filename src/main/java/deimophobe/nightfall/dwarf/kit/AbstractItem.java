package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 24/03/17.
 */
public abstract class AbstractItem extends AbstractPiece implements ItemPiece {
	
	public AbstractItem(Dwarf dwarf) {
		super(dwarf);
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
	
	protected boolean damageFromItem(MonsterDamage damage) {
		return (damage.getType() == NaturalDamageType.MELEE && isHoldingItem());
	}

	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {return false;}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {}
}
