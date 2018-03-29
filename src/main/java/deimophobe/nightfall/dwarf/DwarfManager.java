package deimophobe.nightfall.dwarf;

import com.google.common.collect.Sets;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.GameSize;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.dwarf.hero.HeroType;
import deimophobe.nightfall.entity.GamePlayerManager;
import deimophobe.nightfall.event.DwarfCreateEvent;
import deimophobe.nightfall.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
		//HeroType.getHeroList();
		ConsumableType.resetConsumables();
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
	
	@Override
	public boolean removeGamePlayer(UUID uuid, boolean reset) {
		Dwarf dwarf = getGamePlayer(uuid);
		notifyCloseEvent(dwarf);
		return super.removeGamePlayer(uuid, reset);
	}
	
	
	public Hero addHero(String name, HeroType type) {
		return addHero(Bukkit.getPlayer(name), type);
	}
	public Hero addHero(Player player, HeroType type) {
		if (player == null || isGamePlayer(player)) return null;
		
		Hero hero = type.createHero(player);
		DwarfCreateEvent event = new DwarfCreateEvent(hero);
		Bukkit.getPluginManager().callEvent(event);
		hero.respawn(event.getSpawnLocation());
		
		registerGamePlayer(hero);
		
		return hero;
	}
	
	
	// ------ SHARED CHEST ------
	private final Inventory sharedChest = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "Shared Resources Chest");
	private final Map<Dwarf, Block> dwarfToChestMap = new HashMap<>();
	private final Set<Block> openedChests = new HashSet<>();
	
	public boolean isSharedChest(Inventory inventory) {
		return (inventory != null && sharedChest.getTitle().equals(inventory.getTitle()));
	}
	
	public void openSharedChest(Dwarf dwarf, Block chestBlock) {
		// Force close any already open chests
		notifyCloseEvent(dwarf);
		
		// Update open animation
		if (chestBlock != null) {
			dwarfToChestMap.put(dwarf, chestBlock);
			updateChestState(chestBlock);
		}
		
		dwarf.getPlayer().openInventory(sharedChest);
	}
	
	public void openSharedChest(Dwarf dwarf) {
		openSharedChest(dwarf, null);
	}
	
	public void notifyCloseEvent(Dwarf dwarf) {
		Block viewingBlock = dwarfToChestMap.remove(dwarf);
		if (viewingBlock != null) {
			updateChestState(viewingBlock);
		}
	}
	
	private void updateChestState(Block block) {
		boolean oldState = openedChests.contains(block);
		boolean newState = dwarfToChestMap.values().contains(block);
		if (oldState == newState) return;
		
		Sound chestSound;
		if (newState) {
			chestSound = Sound.BLOCK_CHEST_OPEN;
			openedChests.add(block);
		} else {
			chestSound = Sound.BLOCK_CHEST_CLOSE;
			openedChests.remove(block);
		}
		block.getWorld().playSound(block.getLocation(), chestSound, 1f, 1f);
		PacketUtil.setChestOpen(block, newState);
	}
	
	public void addItemToChest(ItemStack item) {
		sharedChest.addItem(item);
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
		plagueables.removeIf(d -> d.getPlagueStatus() == Dwarf.PlagueStatus.IMMUNE);
		return plagueables;
	}

	public Set<Dwarf> getPlagued() {
		Set<Dwarf> plagued = new HashSet<>(getGamePlayers());
		plagued.removeIf(d -> d.getPlagueStatus() != Dwarf.PlagueStatus.PLAGUED);
		return plagued;
	}
	
	private static final Set<HeroType> PRIMARY_HEROES = Sets.newHashSet(HeroType.VELVETINE, HeroType.TUI, HeroType.HERANA);
	private static final Set<HeroType> SECONDARY_HEROES = Sets.newHashSet(HeroType.ARTHEA);
	
	public void onGameStart() {
		Collection<Player> players = new HashSet<>(Bukkit.getOnlinePlayers());
		players.removeIf(player -> player.getGameMode() == GameMode.SPECTATOR);
		
		int numPlayers = players.size();
		int numHeroes = 0;
		if (numPlayers >= 15) numHeroes++;
		if (numPlayers >= 25) numHeroes++;
		if (numPlayers >= 35) numHeroes++;
		
		GameSize size = GameSize.getSizeFromHeroCount(numHeroes);
		Game.getGame().setGameSize(size);
		
		Set<HeroType> chosenHeroes = new HashSet<>();
		while (numHeroes > 0) {
			Set<HeroType> possibleHeroes = new HashSet<>(PRIMARY_HEROES);
			if (chosenHeroes.size() >= 1) possibleHeroes.addAll(SECONDARY_HEROES);
			possibleHeroes.removeAll(chosenHeroes);
			
			Player hero = Misc.getRandom(players);
			players.remove(hero);
			
			HeroType type = Misc.getRandom(possibleHeroes);
			chosenHeroes.add(type);
			
			addHero(hero, type);
			numHeroes--;
		}
		
		for (Player player : players) {
			addGamePlayer(player);
		}
	}
}
