package deimophobe.nightfall.dwarf.kit.spell;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import org.bukkit.ChatColor;

/**
 * Created by Deimophobe on 14/06/18.
 */
class GiveCobble implements Spell {
	@Override public String getName() { return ChatColor.GRAY + "More Cobble"; }
	@Override public int getCost() { return 1; }
	@Override public int getCooldown() { return 10;	}
	
	@Override
	public void castSpell(Dwarf dwarf) {
		dwarf.playSound("block.anvil.place", 0.2f, 0.8f, true);
		dwarf.playSound("block.anvil.break", 1f, 0.8f, true);
		dwarf.giveConsumable(ConsumableType.COBBLESTONE, 8);
	}
}
