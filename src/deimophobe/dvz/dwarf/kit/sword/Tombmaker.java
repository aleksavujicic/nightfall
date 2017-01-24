package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.PlayerOrAI;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.PlayerMonster;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Tombmaker extends Sword {
	
	Tombmaker(Dwarf dwarf) {
		super(dwarf, SwordType.TOMBMAKER, 300);
	}
	
	@Override
	public void onKill(PlayerOrAI monster) {
		if (dwarf.hasProc())
			dwarf.giveProc(Dwarf.ProcType.REGULAR);
	}
	
	@Override
	protected boolean ability() {
		dwarf.playSound("proc", 1, 1, false);
		dwarf.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 2), true);
		return true;
	}
}
