package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.ai.AIManager;
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
		tryPlagueMore();
	}
	
	@Override
	public void endPlague() {
		super.endPlague();
		convertingDwarves.clear();
		AIManager.getManager().removeAllAIs();
	}
	
	private static final String SICK_MSG
			= ChatColor.GREEN + "You begin to feel a little "
			+ ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "sick"
			+ ChatColor.GREEN + "!";
	private static final String TURNED_MSG_1
			= ChatColor.LIGHT_PURPLE + "You have turned into a "
			+ ChatColor.GREEN + "zombie"
			+ ChatColor.LIGHT_PURPLE + "!";
	private static final String TURNED_MSG_2
			= ChatColor.GREEN + "You feel the urge to "
			+ ChatColor.LIGHT_PURPLE + "spread the plague "
			+ ChatColor.GREEN + "by "
			+ ChatColor.RED + "attacking the dwarves"
			+ ChatColor.GREEN + "!";
	
	private static final int SICK_MSG_TIME = 160;
	
	boolean convertToZombie(Dwarf dwarf) {
		if (hasEnded()) return false;
		
		// If dwarf is plagued, make sure to plague
		// Otherwise stop if the dwarf is not plagueable, or amt to kill is zero.
		if (!isPlagued(dwarf)) {
			if (getAmountToInfect() <= 0) return false;
			if (!canConvert(dwarf)) return false;
		}
		
		convertingDwarves.add(dwarf);
		
		dwarf.sendMessage(SICK_MSG);
		dwarf.givePotionEffect(PotionEffectType.CONFUSION, SICK_MSG_TIME + 3*20, 1, true, false, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				// Convert to zombie.
				Player player = dwarf.getPlayer();
				
				// If dwarf is still online and still alive.
				if (DwarfManager.getManager().isGamePlayer(player)) {
					player.removePotionEffect(PotionEffectType.CONFUSION);
					
					ItemStack[] inv = player.getInventory().getContents();
					DwarfManager.getManager().removeGamePlayer(dwarf);
					
					MonsterPlayer mp = MonsterManager.getManager().addGamePlayer(player);
					player.getInventory().setContents(inv);
					Mob zombie = new PlaguedZombie(mp, ZombiePlague.this, !isPlagued(dwarf));
					mp.spawnMob(zombie, SpawnMethod.NONE);
					
					numZombiesAlive++;
					convertingDwarves.remove(dwarf);
					mp.sendMessage(TURNED_MSG_1);
					mp.sendMessage(TURNED_MSG_2);
				} else {
					DwarfManager.getManager().removeOfflinePlayer(player.getUniqueId());
					tryPlagueMore();
				}
				
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), SICK_MSG_TIME);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				checkEnd();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 600);
		return true;
	}
	
	void notifyZombieDeath() {
		numZombiesAlive--;
		tryPlagueMore();
	}
	
	private void tryPlagueMore() {
		checkEnd();
		if (hasEnded()) return;
		
		int toPlague = (int) Math.ceil((double) getAmountToKill(false) / 4);
		for (int i = 0; i < toPlague; i++) {
			Dwarf dwarf = getRandomPlagueable();
			convertToZombie(dwarf);
		}
	}
	
	private boolean canConvert(Dwarf dwarf) {
		return !convertingDwarves.contains(dwarf) && isPlagueable(dwarf);
	}
	
	private int getAmountToInfect() {
		return getAmountToKill(true) - convertingDwarves.size();
	}
}
