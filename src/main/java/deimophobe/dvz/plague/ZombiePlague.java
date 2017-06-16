package deimophobe.dvz.plague;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
class ZombiePlague extends AbstractPlague {
	
	private int numZombiesAlive = 0;
	
	@Override
	public void startPlague(Set<Dwarf> plagueables, int killAmt) {
		super.startPlague(plagueables, killAmt);
		infectMore();
	}
	
	private void infectMore() {
		int toPlague = (int) Math.ceil((double) toKill/4);
		for (int i=0; i<toPlague; i++) {
			Dwarf dwarf = Misc.getRandom(plagueables);
			convertToZombie(dwarf);
		}
	}
	
	private static final String SICK_MSG = ChatColor.GREEN + "You begin to feel a little " + ChatColor.LIGHT_PURPLE + "sick" + ChatColor.GREEN + "!";
	private static final int SICK_MSG_TIME = 160;
	
	void convertToZombie(Dwarf dwarf) {
		if (toKill == 0 || !plagueables.contains(dwarf)) return;
		
		removeDwarf(dwarf);
		
		dwarf.sendMessage(SICK_MSG);
		dwarf.givePotionEffect(PotionEffectType.CONFUSION, SICK_MSG_TIME + 3*20, 1, true, false, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				// Convert to zombie.
				Player player = dwarf.getPlayer();
				player.removePotionEffect(PotionEffectType.CONFUSION);
				
				DwarfManager.getManager().removeGamePlayer(dwarf, false);
				MonsterManager.getManager().addGamePlayer(player);
				MonsterPlayer mp = MonsterManager.getManager().getGamePlayer(player);
				mp.spawnMobAt(new PlaguedZombie(mp, ZombiePlague.this),null);
				
				numZombiesAlive++;
				
			}
		}.runTaskLater(Game.getGame().getPlugin(), SICK_MSG_TIME);
		
		if (toKill == 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					notifyEnd();
				}
			}.runTaskLater(Game.getGame().getPlugin(), 600);
		}
	}
	
	void notifyZombieDeath() {
		numZombiesAlive--;
		if (numZombiesAlive == 0) {
			Dwarf dwarf = Misc.getRandom(plagueables);
			convertToZombie(dwarf);
			infectMore();
		}
	}
	
	
}
