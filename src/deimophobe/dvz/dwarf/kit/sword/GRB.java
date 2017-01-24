package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.PlayerOrAI;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.PlayerMonster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class GRB extends Sword {
	
	
	GRB(Dwarf dwarf) {
		super(dwarf, SwordType.GRB, 400);
	}
	
	@Override
	public void onKill(PlayerOrAI monster) {
		dwarf.giveProc(Dwarf.ProcType.REGULAR);
		reduceCooldown(20);
	}
	
	@Override
	protected boolean ability() {
		Player player = dwarf.getPlayer();
		
		dwarf.playSound("dash", 1f, 1f, true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12, 0), false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 12, 29), false);
		player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(5));
		
		return true;
	}
}
