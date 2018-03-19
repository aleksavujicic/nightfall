package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.Mob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Deimophobe on 7/03/17.
 */
class ZombiePlague extends Plague {
	
	private int numZombiesAlive = 0;
	private final Collection<Dwarf> convertingDwarves = new HashSet<>();
	
	@Override
	public void startPlague() {
		for (Dwarf dwarf : getPlagueds()) {
			convertToZombie(dwarf);
		}
		
		// TODO Do this every 30sec or so?
		checkPlagueCount(true);
	}
	
	@Override
	public void endPlague() {
		super.endPlague();
		convertingDwarves.clear();
	}
	
	private static final String SICK_MSG = ChatColor.GREEN + "You begin to feel a little " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "sick" + ChatColor.GREEN + "!";
	private static final int SICK_MSG_TIME = 160;
	
	boolean convertToZombie(Dwarf dwarf) {
		if (hasEnded()) return false;
		
		// If dwarf is plagued, make sure to plague
		// Otherwise stop if the dwarf is not plagueable, or amt to kill is zero.
		if (!isPlagued(dwarf)) {
			if (getAmountToKill(true) == 0) return false;
			if (!isPlagueable(dwarf)) return false;
		}
		
		convertingDwarves.add(dwarf);
		
		dwarf.sendMessage(SICK_MSG);
		dwarf.givePotionEffect(PotionEffectType.CONFUSION, SICK_MSG_TIME + 3*20, 1, true, false, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				//TODO check if offline
				
				// Convert to zombie.
				Player player = dwarf.getPlayer();
				player.removePotionEffect(PotionEffectType.CONFUSION);
				
				ItemStack[] inv = player.getInventory().getContents();
				DwarfManager.getManager().removeGamePlayer(dwarf, false);
				
				MonsterPlayer mp = MonsterManager.getManager().addGamePlayer(player);
				player.getInventory().setContents(inv);
				Mob zombie = new PlaguedZombie(mp, ZombiePlague.this, !isPlagued(dwarf));
				mp.spawnMob(zombie);
				
				numZombiesAlive++;
				
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), SICK_MSG_TIME);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				checkPlagueCount(false);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 600);
		return true;
	}
	
	void notifyZombieDeath() {
		numZombiesAlive--;
		boolean plagueMore = (numZombiesAlive == 0);
		checkPlagueCount(plagueMore);
	}
	
	private void checkPlagueCount(boolean plagueMore) {
		if (hasEnded()) return;
		
		if (getAmountToKill(true) == 0) {
			endPlague();
			return;
		}
		
		if (plagueMore) {
			int toPlague = (int) Math.ceil((double) getAmountToKill(false) / 4);
			for (int i = 0; i < toPlague; i++) {
				Dwarf dwarf = getRandomPlagueable();
				convertToZombie(dwarf);
			}
		}
	}
	
	@Override
	protected boolean isPlagueable(Dwarf dwarf) {
		if (convertingDwarves.contains(dwarf)) return false;
		return super.isPlagueable(dwarf);
	}
}
