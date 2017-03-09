package deimophobe.dvz.plague;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
class ZombiePlague extends Plague {
	@Override
	public void onStart() {
		for (Dwarf dwarf : plagueables)
			convertToZombie(dwarf);
	}
	
	private static final String SICK_MSG = ChatColor.GREEN + "You begin to feel a little " + ChatColor.LIGHT_PURPLE + " sick" + ChatColor.GREEN + "!";
	private static final int SICK_MSG_TIME = 100;
	private void convertToZombie(Dwarf dwarf) {
		dwarf.sendMessage(SICK_MSG);
		dwarf.givePotionEffect(PotionEffectType.CONFUSION, SICK_MSG_TIME, 1, true, true, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				// Convert to zombie.
				DwarfManager.getManager().removeGamePlayer(dwarf);
				MonsterManager.getManager().addGamePlayer(dwarf.getPlayer());
			}
		}.runTaskLater(Game.getGame().getPlugin(), SICK_MSG_TIME);
	}
}
