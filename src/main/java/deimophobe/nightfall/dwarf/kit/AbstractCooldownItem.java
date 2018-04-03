package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 24/03/17.
 *
 * @deprecated Do not use. Use AbstractItem and implement KitCooldown. See {@link AbstractCooldown} for why.
 */
@Deprecated
public abstract class AbstractCooldownItem extends AbstractCooldown implements ItemPiece {
	
	public AbstractCooldownItem(Dwarf dwarf, int maxCooldown) {
		super(dwarf, maxCooldown);
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
	
	protected boolean itemCausedDamage(MonsterDamage damage) {
		return (damage.getType() == GameDamageType.MELEE && isHoldingItem());
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {return false;}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {}
	
	
}
