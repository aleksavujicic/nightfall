package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitItemElement;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 24/03/17.
 *
 * @deprecated Do not use. Use AbstractItem and implement KitCooldown. See {@link AbstractCooldown} for why.
 */
@Deprecated
public abstract class AbstractCooldownItem extends AbstractCooldown implements KitItemElement {
	
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
		return (damage.getType() == NaturalDamageType.MELEE && isHoldingItem());
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {return false;}
	
	@Override
	public void onBlockBreak(Block block) {}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return getItem().createItemStack();
	}
	
	
}
