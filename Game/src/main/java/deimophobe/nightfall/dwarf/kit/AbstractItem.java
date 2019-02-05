package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 24/03/17.
 */
public abstract class AbstractItem extends AbstractPiece implements ItemPiece {
	
	public AbstractItem(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	@Override
	public boolean doesItemMatch(@NotNull ItemStack toMatch) {
		return getItem().isSimilar(toMatch);
	}
	
	@Override
	public boolean isHoldingItem() {
		return (doesItemMatch(dwarf.getHeldItem()));
	}
	
	protected boolean isMeleeDamageFromItem(MonsterDamage damage) {
		return (damage.getType() == GameDamageType.MELEE && isHoldingItem());
	}
	
	protected final boolean setShiny(boolean shiny) {
		boolean updated = false;
		for (ItemStack item : dwarf.getPlayer().getInventory().getStorageContents()) {
			updated |= trySetShiny(item, shiny);
		}
		updated |= trySetShiny(dwarf.getPlayer().getItemOnCursor(), shiny);
		
		if (updated) dwarf.getPlayer().updateInventory();
		
		return updated;
	}
	
	private boolean trySetShiny(ItemStack item, boolean shiny) {
		if (!doesItemMatch(item)) return false;
		
		if (shiny) {
			if (item.containsEnchantment(Enchantment.DURABILITY)) return false;
			item.addEnchantment(Enchantment.DURABILITY, 1);
		} else {
			if (!item.containsEnchantment(Enchantment.DURABILITY)) return false;
			item.removeEnchantment(Enchantment.DURABILITY);
		}
		return true;
	}

	@Override
	public boolean onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {return false;}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {}
}
