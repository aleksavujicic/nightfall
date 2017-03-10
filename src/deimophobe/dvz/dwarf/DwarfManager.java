package deimophobe.dvz.dwarf;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayerManager;
import deimophobe.dvz.dwarf.hero.Hero;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfManager extends GamePlayerManager<Dwarf> {
	private static DwarfManager ourManager = new DwarfManager();
	
	public DwarfManager() {
		super(ChatColor.DARK_AQUA + "DWARVES");
	}
	
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
	
	
	public boolean addHero(String name, Hero.Type type) {
		return addHero(Bukkit.getPlayer(name), type);
	}
	public boolean addHero(Player player, Hero.Type type) {
		if (player == null) return false;
		Hero hero = new Hero(player, type);
		return addGamePlayer(hero);
	}
	
	
	private final Inventory sharedChest = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "Shared Resources Chest");
	public Inventory getSharedChest() { return sharedChest; }
	
	public boolean isSharedChest(Inventory inventory) {
		return (inventory != null && sharedChest.getTitle().equals(inventory.getTitle()));
	}
	
	
	
	public Set<Dwarf> getPlagueables() {
		return new HashSet<>(getGamePlayers());
	}
}
