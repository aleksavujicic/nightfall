package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 27/03/17.
 */
class StuddedArmour extends AbstractElement{
	
	public StuddedArmour(Dwarf dwarf) {
		super(dwarf);
		dwarf.givePotionEffect(PotionEffectType.SLOW, 10*60*60*20, -2, true, true, true);
	}
}
