package deimophobe.dvz.plague;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.monster.mob.PlaguedZombie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
class ZombiePlague extends Plague {
	@Override
	public void onStart() {
		Dwarf dwarf = Misc.getRandom(plagueables);
		convertToZombie(dwarf);
	}
	
	private static final String SICK_MSG = ChatColor.GREEN + "You begin to feel a little " + ChatColor.LIGHT_PURPLE + "sick" + ChatColor.GREEN + "!";
	private static final int SICK_MSG_TIME = 100;
	
	private void convertToZombie(Dwarf dwarf) {
		if (toKill == 0 || !plagueables.contains(dwarf)) return;
		
		removeDwarf(dwarf);
		
		dwarf.sendMessage(SICK_MSG);
		dwarf.givePotionEffect(PotionEffectType.CONFUSION, 10000, 1, true, true, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				// Convert to zombie.
				Player player = dwarf.getPlayer();
				player.removePotionEffect(PotionEffectType.CONFUSION);
				
				DwarfManager.getManager().removeGamePlayer(dwarf);
				MonsterPlayer mp = MonsterManager.getManager().createAndSpawnMob(player, MobType.PLAGUE_ZOMBIE);
				//((PlaguedZombie) mp.getMob()).setPlague(ZombiePlague.this);
				
			}
		}.runTaskLater(Game.getGame().getPlugin(), SICK_MSG_TIME);
		
		if (toKill == 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					endPlague();
				}
			}.runTaskLater(Game.getGame().getPlugin(), 300);
		}
	}
}
