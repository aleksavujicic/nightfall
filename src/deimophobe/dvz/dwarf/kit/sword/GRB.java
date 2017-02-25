package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
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
	public void onKill(GameEntity monster, DamageType type) {
		if (type.isMelee() && isHoldingItem())
			dwarf.giveProc(Dwarf.ProcType.REGULAR);
		
		reduceCooldown(20);
	}
	
	@Override
	protected boolean ability() {
		Player player = dwarf.getPlayer();
		
		dwarf.playSound("dash", 1f, 1f, true);
		dwarf.giveProc(Dwarf.ProcType.RUNEDASH);
		player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(5));
		
		return true;
	}
}
