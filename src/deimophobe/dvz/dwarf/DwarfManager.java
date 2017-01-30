package deimophobe.dvz.dwarf;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.kit.ArmourType;
import deimophobe.dvz.dwarf.kit.Loadout;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfManager implements Listener {
	private static DwarfManager ourManager = new DwarfManager();
	public static DwarfManager getManager() {
		return ourManager;
	}
	
	public void setupManager(Plugin plugin) {
		dwarves = new HashMap<>();
		Bukkit.getPluginManager().registerEvents(new DwarfListener(), plugin);
		config = YamlConfiguration.loadConfiguration(plugin.getResource("dwarf-items.yml"));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Dwarf dwarf : dwarves.values()) {
					dwarf.update();
				}
			}
		}.runTaskTimer(plugin, 20, 20);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Dwarf dwarf : dwarves.values()) {
					dwarf.quickUpdate();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		dwarfTeam = board.registerNewTeam("dwarves");
		dwarfTeam.setAllowFriendlyFire(false);
		dwarfTeam.setDisplayName(ChatColor.DARK_AQUA + "Dwarves");
		dwarfTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
		dwarfTeam.setPrefix(String.valueOf(ChatColor.DARK_AQUA));
	}
	
	
	private Map<String, Dwarf> dwarves;
	private Team dwarfTeam;
	private Configuration config;
	
	public Configuration getConfig() {
		return config;
	}
	
	public boolean addDwarf(Player player) { return addDwarf(player.getName()); }
	public boolean addDwarf(String name) {
		Player player = Bukkit.getPlayer(name);
		
		if (player == null) return false;
		
		name = player.getName();
		if (dwarves.containsKey(name)) return false;
		
		Map<ConsumableType, Integer> consumables = new HashMap<>();
		consumables.put(ConsumableType.LAMP, 5);
		//consumables.put(ConsumableType.SLAB, 5);
		consumables.put(ConsumableType.SOS, 5);
		consumables.put(ConsumableType.WRENCH, 5);
		consumables.put(ConsumableType.MORTAR, 5);
		consumables.put(ConsumableType.WIZARD_MORTAR, 5);
		consumables.put(ConsumableType.ARMOUR_ITEM, 5);
		Loadout loadout = new Loadout("Ranger", false, null, SwordType.GRB, BowType.DRAGONSKIN, AleType.REGROWTH, consumables, ArmourType.STUDDED, Collections.singleton(Passive.QUICKFEET));
		
		dwarves.put(name, new Dwarf(player, loadout));
		dwarfTeam.addEntry(name);
		Game.getGame().updateSidebar();
		return true;
	}
	
	public boolean addDwarf(String name, Loadout loadout) {
		Player player = Bukkit.getPlayer(name);
		
		if (player == null) return false;
		name = player.getName();
		if (dwarves.containsKey(name)) return false;
		
		dwarves.put(name, new Dwarf(player, loadout));
		dwarfTeam.addEntry(name);
		Game.getGame().updateSidebar();
		return true;
	}
	
	public Dwarf getDwarf(Player player) {
		if (player == null) return null;
		return getDwarf(player.getName());
	}
	
	public Dwarf getDwarf(String name) {
		return dwarves.get(name);
	}
	
	public Dwarf getDwarf(Entity entity) {
		if (entity instanceof Player)
			return getDwarf((Player)entity);
		else
			return null;
	}
	
	public boolean isDwarf(Player player) {
		if (player == null) return false;
		return isDwarf(player.getName());
	}
	
	public boolean isDwarf(String name) {
		return dwarves.containsKey(name);
	}
	
	public boolean removeDwarf(Player player) {
		return removeDwarf(player.getName());
	}
	
	public boolean removeDwarf(Dwarf dwarf) {
		return removeDwarf(dwarf.getName());
	}
	
	public boolean removeDwarf(String name) {
		Dwarf dwarf = dwarves.remove(name);
		if (dwarf == null) return false;
		dwarf.remove();
		dwarfTeam.removeEntry(name);
		Game.getGame().updateSidebar();
		return true;
	}
	
	public Collection<Dwarf> getDwarves() {
		return dwarves.values();
	}
	
	
	
	private static final Map<String, Dwarf> offline = new HashMap<>();
	public boolean goOnline(Player player) {
		String name = player.getName();
		if (!offline.containsKey(name)) return false;
		
		Dwarf dwarf = offline.remove(name);
		//TODO
		//dwarf.setPlayer(player);
		//dwarf.setTitle(dwarf.getTitle());
		dwarves.put(name, dwarf);
		return true;
	}
	
	public boolean goOffline(Player player) {
		String name = player.getName();
		if (!dwarves.containsKey(name)) return false;
		
		Dwarf dwarf = dwarves.remove(name);
		offline.put(name, dwarf);
		return true;
	}
}
