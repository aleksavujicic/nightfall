package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 11/03/17.
 */
class Nosovin extends Hero {
	//protected Nosovin(Player player, Type type) {
	//	super(player, type);
	//}
	
	public Nosovin(Player player) {
		super(player);
	}
	
	@Override
	public void updateCooldownBar() {
		/*
		if (isHolding(Bow.getItem(BowType.WAND))) {
			player.setExp(Math.max(0, getKit().fractionComplete()));
		} else {
			player.setExp(1f - (float)rocketCd/MAX_ROCKET_CD);
		}*/
	}
}
