package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.dwarf.hero.HeroType;
import deimophobe.nightfall.entity.GamePlayerManager;
import deimophobe.nightfall.event.DwarfCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfManager extends GamePlayerManager<Dwarf> {
	public static DwarfManager getManager() {
		return Game.getGame().getDwarfManager();
	}
	
	public DwarfManager() {
		super(ChatColor.AQUA + "Dwarves","dwarves", ChatColor.DARK_AQUA);
		
		// Loads all the heroes
		HeroType.getHeroList();
	}
	
	public Dwarf createDwarf(Player player, DwarfData data) {
		Dwarf dwarf = new Dwarf(player, data);
		
		DwarfCreateEvent event = new DwarfCreateEvent(dwarf);
		Bukkit.getPluginManager().callEvent(event);
		dwarf.respawn(event.getSpawnLocation());
		
		registerGamePlayer(dwarf);
		return dwarf;
	}
	
	@Override
	protected Dwarf createGamePlayerFromPlayer(Player player) {
		Dwarf dwarf = new Dwarf(player);
		DwarfCreateEvent event = new DwarfCreateEvent(dwarf);
		Bukkit.getPluginManager().callEvent(event);
		dwarf.respawn(event.getSpawnLocation());
		
		return dwarf;
	}
	
	
	
	public boolean addHero(String name, HeroType type) {
		return addHero(Bukkit.getPlayer(name), type);
	}
	public boolean addHero(Player player, HeroType type) {
		if (player == null || isGamePlayer(player)) return false;
		
		Hero hero = type.createHero(player);
		DwarfCreateEvent event = new DwarfCreateEvent(hero);
		Bukkit.getPluginManager().callEvent(event);
		hero.respawn(event.getSpawnLocation());
		
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
	
	public Set<Dwarf> getNonHeroDwarves() {
		Set<Dwarf> nonHeroes = new HashSet<>(getGamePlayers());
		nonHeroes.removeIf(d -> d instanceof Hero);
		return nonHeroes;
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
	
	
	public void onGameStart() {
		Collection<Player> players = new HashSet<>(Bukkit.getOnlinePlayers());
		selectHeroes(players);
		for (Player player : players) {
			addGamePlayer(player);
		}
	}
	
	public void selectHeroes(Collection<? extends Player> players) {
		Collection<Player> selectablePlayers = new HashSet<>();
		for (Player player : players) {
			if (!isGamePlayer(player))
				selectablePlayers.add(player);
		}
		
		int numPlayers = selectablePlayers.size();
		if (numPlayers >= 15) {
			Player hero = Misc.getRandom(selectablePlayers);
			selectablePlayers.remove(hero);
			HeroType type = Misc.getRandomFrom(HeroType.VELVETINE, HeroType.ARTHEA, HeroType.TUI);
			addHero(hero, type);
		}
		
		/*
		int numHeroes = 0;
		
		if (numPlayers >= 15) numHeroes++;
		if (numPlayers >= 25) numHeroes++;
		if (numPlayers >= 35) numHeroes++;
		
		while (numHeroes > 0) {
			Player hero = Misc.getRandom(selectablePlayers);
			players.remove(hero);
			
			Hero.Type type = Misc.getRandomFrom(Hero.Type.ARTHEA, Hero.Type.TUI, Hero.Type.NOSOVIN);
			addHero(hero, type);
			numHeroes--;
		}
		*/
	}
}
