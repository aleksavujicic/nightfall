package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.effects.sound.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 23/07/17.
 */
public class TitlePlayer extends BukkitRunnable {
	private static final Title[] titles = new Title[] {
			new Title(  "",  40),
            new Title(ChatColor.BLUE + "Nightfall",   40),
            new Title(ChatColor.DARK_RED + "N" + ChatColor.BLUE + "ightfall" ,   2),
            new Title(ChatColor.DARK_RED + "Ni" + ChatColor.BLUE + "ghtfall" ,   2),
            new Title(ChatColor.DARK_RED + "Nig" + ChatColor.BLUE + "htfall" ,   2),
            new Title(ChatColor.DARK_RED + "Nigh" + ChatColor.BLUE + "tfall" ,   2),
			new Title(ChatColor.BLUE + "N" + ChatColor.DARK_RED + "ight"  + ChatColor.BLUE + "fall",   2),
            new Title(ChatColor.BLUE + "Ni" + ChatColor.DARK_RED + "ghtf"  + ChatColor.BLUE + "all",   2),
            new Title(ChatColor.BLUE + "Nig" + ChatColor.DARK_RED + "htfa"  + ChatColor.BLUE + "ll",   2),
            new Title(ChatColor.BLUE + "Nigh" + ChatColor.DARK_RED + "tfal"  + ChatColor.BLUE + "l",   2),
            new Title(ChatColor.BLUE + "Night" + ChatColor.DARK_RED + "fall"  + ChatColor.BLUE + "",   60),
	};
	
	public static void playTitle(Player player, boolean playMusic) {
		new TitlePlayer(player, playMusic);
	}
	
	private final Player player;
	private int timeUntilNextTitle;
	private int titleIndex;
	private final boolean playMusic;
	
	private TitlePlayer(Player player, boolean playMusic) {
		this.player = player;
		this.playMusic = playMusic;
		
		timeUntilNextTitle = 0;
		titleIndex = -1;
		runTaskTimer(NightfallPlugin.getPlugin(), 0, 1);
	}
	
	@Override
	public void run() {
		if (!player.isOnline()) {
			this.cancel();
			return;
		}
		
		if (timeUntilNextTitle > 0) {
			timeUntilNextTitle--;
		} else {
			titleIndex++;
			
			if (titleIndex >= titles.length) {
				this.cancel();
				return;
			}
			
			if (titleIndex == titles.length -1 && playMusic) {
				Sounds.DWARF_INTRO_MUSIC.playSound(player);
			}
			
			if (titleIndex == 1) {
				player.playSound(player.getLocation(), "entity.wither.spawn", 1000, 0.5f);
			}
			
			Title title = titles[titleIndex];
			title.play(player);
			
			timeUntilNextTitle = title.duration;
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
		
		private void play(Player player) {
			player.sendTitle(title, "", 0, duration, 20);
		}
	}
}
