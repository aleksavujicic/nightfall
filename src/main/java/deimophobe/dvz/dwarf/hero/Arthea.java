package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.monster.ai.AIEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

/**
 * Created by Deimophobe on 7/05/17.
 */
public class Arthea extends Hero {
	protected Arthea(Player player, Hero.Type type) {
		super(player, type);
	}
	
	private static final String ARTHEA_TEAM_NAME = "arthea";
	static {
		Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(ARTHEA_TEAM_NAME);
		if (team != null)
			team.unregister();
		
		team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(ARTHEA_TEAM_NAME);
		team.setPrefix(ChatColor.DARK_RED.toString());
		team.addEntry(ChatColor.GOLD + "Arthea");
	}
	
	@Override
	public void notifyDeath(Dwarf dwarf) {
		super.notifyDeath(dwarf);
		if (dwarf == this) {
			//playSound("dwarf.hero.tui.death", 1000, 1, true);
		}
	}
}
