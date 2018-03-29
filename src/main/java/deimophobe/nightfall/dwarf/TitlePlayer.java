package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.game.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 23/07/17.
 */
class TitlePlayer extends BukkitRunnable {
	private static final Title[] titles = new Title[] {
			new Title(  "", ChatColor.BLUE + "Be prepared for", 40),
            /*new Title(ChatColor.BLUE + "N", ChatColor.BLUE + "Be prepared for", 2),
            new Title(ChatColor.BLUE + "Ni", ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.BLUE + "Nig", ChatColor.BLUE + "Be prepared for", 2),
            new Title(ChatColor.BLUE + "Nigh", ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.BLUE + "Night", ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.BLUE + "NightF", ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.BLUE + "NightFa", ChatColor.BLUE + "Be prepared for", 2),
            new Title(ChatColor.BLUE + "NightFal", ChatColor.BLUE + "Be prepared for",  2),*/
            new Title(ChatColor.BLUE + "Nightfall", ChatColor.BLUE + "Be prepared for",  40),
            new Title(ChatColor.DARK_RED + "N" + ChatColor.BLUE + "ightfall" , ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.DARK_RED + "Ni" + ChatColor.BLUE + "ghtfall" , ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.DARK_RED + "Nig" + ChatColor.BLUE + "htfall" , ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.DARK_RED + "Nigh" + ChatColor.BLUE + "tfall" , ChatColor.BLUE + "Be prepared for",  2),
			new Title(ChatColor.BLUE + "N" + ChatColor.DARK_RED + "ight"  + ChatColor.BLUE + "fall", ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.BLUE + "Ni" + ChatColor.DARK_RED + "ghtf"  + ChatColor.BLUE + "all", ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.BLUE + "Nig" + ChatColor.DARK_RED + "htfa"  + ChatColor.BLUE + "ll", ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.BLUE + "Nigh" + ChatColor.DARK_RED + "tfal"  + ChatColor.BLUE + "l", ChatColor.BLUE + "Be prepared for",  2),
            new Title(ChatColor.BLUE + "Night" + ChatColor.DARK_RED + "fall"  + ChatColor.BLUE + "", ChatColor.BLUE + "Be prepared for",  60, false),
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
				this.cancel();
				return;
			}
			
			if (titleIndex == titles.length -1 && Game.getGame().getPhase() != Phase.STARTING)
				Sounds.DWARF_INTRO_MUSIC.playSound(player);
			
			if (titleIndex == 1) {
				//player.playSound("entity.wolf.howl", 1000, 0.8f, false);
				player.playSound("entity.wither.spawn", 1000, 0.5f, false);
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
		private final boolean permanent;
		
		private Title(String title, int duration) {
			this(title, "", duration);
		}
		
		private Title(String title, String subtitle, int duration) {
			this(title, subtitle, duration, true);
		}
		
		public Title(String title, String subtitle, int duration, boolean permanent) {
			this.title = title;
			this.subtitle = subtitle;
			this.duration = duration;
			this.permanent = permanent;
		}
		
		private void play(GamePlayer player) {
			player.getPlayer().sendTitle(title, "", 0, duration, 20);
		}
	}
}
