package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 21/06/17.
 */
public class NakedArmour implements Armour {
	
	public NakedArmour(Dwarf dwarf) {
		dwarf.getPlayer().getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
	}
	
	@Override public boolean isArmoured() {return true;}
	@Override public void putOn() {}
	@Override public void addModifier(ItemModifierType type, int value, String reason) {}
	@Override public void increaseMax(int amt) {}
	@Override public boolean isAtMax() {return false;}
	@Override public void damage(int damage) {}
	@Override public void repair(int amount) {}
	
	@Override
	public double getResistance() {
		return 0;
	}
	
	@Override
	public int getManaRegenRate() {
		return 0;
	}
	
	@Override public int getMaxArmor() {return 0;}
}
