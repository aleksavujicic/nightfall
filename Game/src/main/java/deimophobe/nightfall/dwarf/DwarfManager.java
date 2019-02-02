package deimophobe.nightfall.dwarf;

import com.google.common.collect.Sets;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.common.player.settings.Setting;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.dwarf.hero.HeroType;
import deimophobe.nightfall.event.DwarfCreateEvent;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameSize;
import deimophobe.nightfall.game.Sidebar;
import deimophobe.nightfall.game.entity.GamePlayerManager;
import deimophobe.nightfall.util.PacketUtil;
import org.bukkit.*;
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
		return Game.getGame().getManager(DwarfManager.class);
	}
	
	public DwarfManager() {
		super(ChatColor.AQUA + "Dwarves","dwarves", ChatColor.DARK_AQUA);
		
		// Loads all the heroes
		//HeroType.getHeroList();
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
	public void registerGamePlayer(Dwarf player) {
		super.registerGamePlayer(player);
		updateDwarfSidebar();
	}
	
	@Override
	public boolean removeGamePlayer(UUID uuid) {
		Dwarf dwarf = getGamePlayer(uuid);
		notifyCloseEvent(dwarf);
		boolean removed = super.removeGamePlayer(uuid);
		updateDwarfSidebar();
		return removed;
	}
	
	@Override
	public boolean goOnline(Player player) {
		boolean wentOnline = super.goOnline(player);
		if (wentOnline) updateDwarfSidebar();
		return wentOnline;
	}
	
	@Override
	public boolean goOffline(Player player) {
		boolean wentOffline = super.goOffline(player);
		if (wentOffline) updateDwarfSidebar();
		return wentOffline;
	}
	
	private void updateDwarfSidebar() {
		Sidebar sidebar = Sidebar.getGameSidebar();
		int dwarfCount = getNumberOfPlayers();
		sidebar.setEntryValue(Sidebar.Entry.DWARF_COUNT, dwarfCount);
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
		dwarf.sendDebugMsg("Opening shared chest: " + (chestBlock == null ? "null" : chestBlock.getType()));
		
		// Force close any already open chests
		notifyCloseEvent(dwarf);
		
		// Update open animation
		if (chestBlock != null) {
			// Find right chest block (for double chests)
			chestBlock = getPrimaryChestBlock(chestBlock);
			
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
			dwarf.sendDebugMsg("Closing shared chest");
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
	private Block getPrimaryChestBlock(Block block) {
		Material type = block.getType();
		if (type == Material.CHEST || type == Material.TRAPPED_CHEST) {
			Block test1 = block.getRelative(-1,0,0);
			Block test2 = block.getRelative(0,0,-1);
			
			if (test1.getType() == type) {
				return test1;
			} else if (test2.getType() == type) {
				return test2;
			}
		}
		return block;
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
	
	public void onGameStart(Game game) {
		PlayerManager manager = PlayerManager.getManager();
		
		Set<Player> players = new HashSet<>(Bukkit.getOnlinePlayers());
		players.removeIf(player -> player.getGameMode() == GameMode.SPECTATOR);
		
		GameSize size = game.getGameSize();
		int numHeroes = size.getNumHeroes();
		
		Set<HeroType> chosenHeroes = new HashSet<>();
		while (numHeroes > 0) {
			Set<HeroType> possibleHeroes = new HashSet<>(PRIMARY_HEROES);
			if (chosenHeroes.size() >= 1) possibleHeroes.addAll(SECONDARY_HEROES);
			possibleHeroes.removeAll(chosenHeroes);
			
			Collection<Player> heroCandidates = new HashSet<>(players);
			heroCandidates.removeIf(player -> !manager.getSettings(player).getValueOfSetting(Setting.HERO_ENABLED));
			Player hero = Misc.getRandom(heroCandidates);
			if (hero == null) break; // No more players to hero up
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
