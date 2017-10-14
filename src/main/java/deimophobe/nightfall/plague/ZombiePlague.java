package deimophobe.nightfall.plague;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
class ZombiePlague extends AbstractPlague {
	
	private int numZombiesAlive = 0;
	
	@Override
	public void startPlague(Set<Dwarf> plagueables, Set<Dwarf> plagued, int killAmt) {
		super.startPlague(plagueables, plagued, killAmt);
		for (Dwarf dwarf : new HashSet<>(plagued)) {
			convertToZombie(dwarf);
		}
		infectMore();
	}
	
	private void infectMore() {
		if (getAmountToKill() == 0) return;
		
		int toPlague = (int) Math.ceil((double) getAmountToKill()/4);
		for (int i=0; i<toPlague; i++) {
			Dwarf dwarf = Misc.getRandom(plagueables);
			convertToZombie(dwarf);
		}
	}
	
	private static final String SICK_MSG = ChatColor.GREEN + "You begin to feel a little " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "sick" + ChatColor.GREEN + "!";
	private static final int SICK_MSG_TIME = 160;
	
	boolean convertToZombie(Dwarf dwarf) {
		// If dwarf is plagued, make sure to plague
		// Otherwise stop if the dwarf is not plagueable, or amt to kill is zero.
		if (!isPlagued(dwarf) && (getAmountToKill() == 0 || !isPlaguable(dwarf))) return false;
		
		removeDwarf(dwarf);
		
		dwarf.sendMessage(SICK_MSG);
		dwarf.givePotionEffect(PotionEffectType.CONFUSION, SICK_MSG_TIME + 3*20, 1, true, false, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				// Convert to zombie.
				Player player = dwarf.getPlayer();
				player.removePotionEffect(PotionEffectType.CONFUSION);
				
				ItemStack[] inv = player.getInventory().getContents();
				DwarfManager.getManager().removeGamePlayer(dwarf, false);
				MonsterManager.getManager().addGamePlayer(player);
				player.getInventory().setContents(inv);
				MonsterPlayer mp = MonsterManager.getManager().getGamePlayer(player);
				mp.spawnMob(new PlaguedZombie(mp, ZombiePlague.this, !plagued.contains(dwarf)));
				
				numZombiesAlive++;
				
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), SICK_MSG_TIME);
		
		if (getAmountToKill() == 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					notifyEnd();
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), 600);
		}
		return true;
	}
	
	void notifyZombieDeath() {
		numZombiesAlive--;
		if (numZombiesAlive == 0) {
			infectMore();
		}
	}
	
	
}
