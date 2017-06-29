package deimophobe.dvz.dwarf;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayerManager;
import deimophobe.dvz.dwarf.armour.Armour;
import deimophobe.dvz.dwarf.hero.Hero;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfManager extends GamePlayerManager<Dwarf> {
	private static DwarfManager ourManager = new DwarfManager();
	public static DwarfManager getManager() {
		return ourManager;
	}
	
	public DwarfManager() {
		super(ChatColor.DARK_AQUA + "DWARVES");
	}
	
	
	private BukkitRunnable runner;
	
	public void setupManager() {
		Plugin plugin = Game.getGame().getPlugin();
		
		runner = new BukkitRunnable() {
			int counter = 0;
			@Override
			public void run() {
				counter++;
				for (Dwarf dwarf : getGamePlayers()) {
					dwarf.update(
							(counter % 5) == 0,
							(counter % 10) == 0,
							(counter % 20) == 0,
							(counter % 40) == 0,
							(counter % 80) == 0
					);
				}
			}
		};
		runner.runTaskTimer(plugin, 1, 1);
		
		setupTeams("dwarves", ChatColor.DARK_AQUA);
	}
	public void reset() {
		if (runner != null)
			runner.cancel();
		removeAllGamePlayers();
		ourManager = new DwarfManager();
	}
	
	
	public Dwarf createDwarf(Player player, DwarfData data) {
		Dwarf dwarf = new Dwarf(player, data);
		registerGamePlayer(dwarf);
		return dwarf;
	}
	
	@Override
	protected Dwarf createGamePlayerFromPlayer(Player player) {
		return new Dwarf(player);
	}
	
	
	public boolean addHero(String name, Hero.Type type) {
		return addHero(Bukkit.getPlayer(name), type);
	}
	public boolean addHero(Player player, Hero.Type type) {
		if (player == null || isGamePlayer(player)) return false;
		
		Hero hero = type.createHero(player);
		registerGamePlayer(hero);
		
		return true;
	}
	
	
	private final Inventory sharedChest = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "Shared Resources Chest");
	public Inventory getSharedChest() { return sharedChest; }
	
	public boolean isSharedChest(Inventory inventory) {
		return (inventory != null && sharedChest.getTitle().equals(inventory.getTitle()));
	}
	
	
	
	public Collection<Dwarf> getDwarves() {
		return getGamePlayers();
	}
	
	public Set<Dwarf> getPlagueables() {
		Set<Dwarf> plagueables = new HashSet<>(getGamePlayers());
		plagueables.removeIf(Dwarf::isPlagueImmune);
		plagueables.removeIf(Dwarf::isForcePlagued);
		return plagueables;
	}

	public Set<Dwarf> getPlagued() {
		Set<Dwarf> plagued = new HashSet<>(getGamePlayers());
		plagued.removeIf(Dwarf::isPlagueImmune);
		plagued.removeIf((Dwarf d) -> !d.isForcePlagued());
		return plagued;
	}
}
