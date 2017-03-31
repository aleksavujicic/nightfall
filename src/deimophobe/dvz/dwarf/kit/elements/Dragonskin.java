package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Dragonskin extends AbstractBow {
	
	Dragonskin(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static ItemStack ITEM = DwarfManager.getManager().getItem("bow.dragonskin", Slot.MAIN_HAND);
	@Override public ItemStack getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.BOW; }
	@Override public String getBowIdentifier() {return "DRAGONSKIN";}
	@Override public int getPower() {return 40;}
	
	@Override
	public void onSelfKill(GameEntity monster, DamageType type) {
		dwarf.giveProc(Dwarf.ProcType.DRAGONSKIN);
	}
}
