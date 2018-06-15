package deimophobe.nightfall.dwarf.kit.spell;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 16/06/18.
 */
class BolsterSpell implements Spell {
	@Override public String getName() {
		return ChatColor.AQUA + "Bolster";
	}
	@Override public int getCost() {
		return 20;
	}
	@Override public int getCooldown() {
		return 20*20;
	}
	
	private static final double RANGE = 6;
	
	@Override
	public void castSpell(Dwarf dwarf) {
		for (Dwarf dwarf1 : DwarfManager.getManager().getDwarves()) {
			if (dwarf.distanceTo(dwarf) > RANGE) continue;
			
			dwarf1.givePotionEffect(PotionEffectType.HEALTH_BOOST, 60*20, 3, true, false, false);
			dwarf1.playSound("entity.player.levelup", 1f, 1f, false);
		}
	}
	
}
