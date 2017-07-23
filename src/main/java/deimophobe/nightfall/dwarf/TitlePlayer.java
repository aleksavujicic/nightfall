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
			new Title(ChatColor.BLUE + "Be prepared for", "subtitle", 104),
			new Title(ChatColor.BLUE + "", 20),
			new Title(ChatColor.BLUE + "N", 3),
			new Title(ChatColor.BLUE + "Ni", 3),
			new Title(ChatColor.BLUE + "Nig" , 3),
			new Title(ChatColor.BLUE + "Nigh" , 3),
			new Title(ChatColor.BLUE + "Night" , 3),
			new Title(ChatColor.BLUE + "NightF" , 3),
			new Title(ChatColor.BLUE + "NightFa" , 3),
			new Title(ChatColor.BLUE + "NightFal" , 3),
			new Title(ChatColor.BLUE + "NightFall" , 60),
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
