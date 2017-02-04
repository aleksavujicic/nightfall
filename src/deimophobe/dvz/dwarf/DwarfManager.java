package deimophobe.dvz.dwarf;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayerManager;
import deimophobe.dvz.dwarf.kit.ArmourType;
import deimophobe.dvz.Loadout;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfManager extends GamePlayerManager<Dwarf> {
	private static DwarfManager ourManager = new DwarfManager();
	public static DwarfManager getManager() {
		return ourManager;
	}
	
	public void setupManager() {
		Plugin plugin = Game.getGame().getPlugin();
		Bukkit.getPluginManager().registerEvents(new DwarfListener(), plugin);
		config = YamlConfiguration.loadConfiguration(plugin.getResource("dwarf-items.yml"));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Dwarf dwarf : getGamePlayers()) {
					dwarf.update();
				}
			}
		}.runTaskTimer(plugin, 20, 20);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Dwarf dwarf : getGamePlayers()) {
					dwarf.quickUpdate();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
		
		setupTeams("dwarves", ChatColor.DARK_AQUA);
	}
	
	private Configuration config;
	public Configuration getConfig() {
		return config;
	}
	
	
	@Override
	protected Dwarf createGamePlayerFromPlayer(Player player) {
		return new Dwarf(player);
	}
}
