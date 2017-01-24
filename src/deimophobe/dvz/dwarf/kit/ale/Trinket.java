package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Trinket extends Ale {
	Trinket(Dwarf dwarf) {
		super(dwarf, AleType.TRINKET, 200);
	}
	
	@Override
	protected boolean ability(Action type) {
		if (!useMana()) return false;
		
		Player player = dwarf.getPlayer();
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 720000, 9, true, false), true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 720000, 3, true, false), true);
		player.playSound(player.getLocation(), Sound.ENTITY_EVOCATION_ILLAGER_CAST_SPELL, 10, 1.5f);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
		
		return true;
	}
}
