package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.GamePlayer;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.effects.sound.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 23/07/17.
 */
class TitlePlayer extends BukkitRunnable {
	private static final Title[] titles = new Title[] {
			new Title(ChatColor.BLUE + "Night", 20),
			new Title(ChatColor.BLUE + "NightFall", 5),
			new Title(ChatColor.BLUE + "NightFal" + ChatColor.WHITE + "l", 5),
			new Title(ChatColor.BLUE + "NightFa" + ChatColor.WHITE + "l" + ChatColor.DARK_RED + "l", 5),
			new Title(ChatColor.BLUE + "NightF" + ChatColor.WHITE + "a" + ChatColor.DARK_RED + "ll", 5),
			new Title(ChatColor.BLUE + "Night" + ChatColor.WHITE + "F" + ChatColor.DARK_RED + "all", 5),
			new Title(ChatColor.BLUE + "Night" + ChatColor.DARK_RED + "Fall", 60),
	};
	
	static void playTitle(Dwarf dwarf) {
		new TitlePlayer(dwarf);
	}
	
	private final GamePlayer player;
	private int count;
	private int titleIndex;
	
	private TitlePlayer(GamePlayer player) {
		this.player = player;
		count = 0;
		titleIndex = -1;
		runTaskTimer(NightfallPlugin.getPlugin(), 0, 1);
	}
	
	@Override
	public void run() {
		if (count > 0) {
			count--;
		} else {
			titleIndex++;
			
			if (titleIndex >= titles.length) {
				Sounds.DWARF_INTRO_MUSIC.playSound(player);
				this.cancel();
				return;
			}
			
			Title title = titles[titleIndex];
			title.play(player);
			count = title.duration;
		}
	}
	
	private static class Title {
		private final String title;
		private final String subtitle;
		private final int duration;
		
		private Title(String title, int duration) {
			this(title, "", duration);
		}
		
		private Title(String title, String subtitle, int duration) {
			this.title = title;
			this.subtitle = subtitle;
			this.duration = duration;
		}
		
		private void play(GamePlayer player) {
			player.getPlayer().sendTitle(title, subtitle, 0, duration, 20);
		}
	}
}
