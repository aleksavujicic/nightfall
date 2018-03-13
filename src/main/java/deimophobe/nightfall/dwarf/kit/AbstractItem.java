package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
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
	
	protected boolean isMeleeDamageFromItem(MonsterDamage damage) {
		return (damage.getType() == GameDamageType.MELEE && isHoldingItem());
	}
	
	protected final void setShiny(boolean shiny) {
		for (ItemStack item : dwarf.getPlayer().getInventory().getStorageContents()) {
			trySetShiny(item, shiny);
		}
		trySetShiny(dwarf.getPlayer().getItemOnCursor(), shiny);
		
		dwarf.getPlayer().updateInventory();
	}
	
	private void trySetShiny(ItemStack item, boolean shiny) {
		if (!matchesItem(item)) return;
		
		if (shiny)
			item.addEnchantment(Enchantment.DURABILITY, 1);
		else
			item.removeEnchantment(Enchantment.DURABILITY);
	}

	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {return false;}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {}
}
