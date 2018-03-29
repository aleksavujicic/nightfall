package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.NightfallPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Deimophobe on 29/03/18.
 */
class Title {
	private final int cycleTime;
	private final String title;
	private final List<String> subtitle;
	
	Title(int cycleTime, String title, String... subtitles) {
		this.cycleTime = cycleTime;
		this.title = title;
		this.subtitle = Arrays.asList(subtitles);
	}
	
	void playTitle(Player player) {
		player.sendTitle(ChatColor.RED + title, "", 20, 100, 20);
		
		for (int i=0; i < subtitle.size(); i++) {
			final int index = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					player.sendTitle(null, ChatColor.GOLD + subtitle.get(index), 0, cycleTime + 20, 20);
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), cycleTime*(i+1));
		}
	}
}
