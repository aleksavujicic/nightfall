package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.dwarf.hero.HeroType;
import deimophobe.nightfall.entity.GamePlayerManager;
import deimophobe.nightfall.event.DwarfCreateEvent;
import deimophobe.nightfall.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	
	@Override
	public boolean removeGamePlayer(UUID uuid, boolean reset) {
		Dwarf dwarf = getGamePlayer(uuid);
		notifyCloseEvent(dwarf);
		return super.removeGamePlayer(uuid, reset);
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
	
	
	// ------ SHARED CHEST ------
	private final Inventory sharedChest = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "Shared Resources Chest");
	private final Map<Dwarf, Block> openedChests = new HashMap<>();
	
	public boolean isSharedChest(Inventory inventory) {
		return (inventory != null && sharedChest.getTitle().equals(inventory.getTitle()));
	}
	
	public void openSharedChest(Dwarf dwarf, Block chestBlock) {
		// Force close any already open chests
		notifyCloseEvent(dwarf);
		
		// Update open animation
		if (chestBlock != null) {
			openedChests.put(dwarf, chestBlock);
			updateChestState(chestBlock);
		}
		
		dwarf.getPlayer().openInventory(sharedChest);
	}
	
	public void openSharedChest(Dwarf dwarf) {
		openSharedChest(dwarf, null);
	}
	
	public void notifyCloseEvent(Dwarf dwarf) {
		Block viewingBlock = openedChests.remove(dwarf);
		if (viewingBlock != null) {
			updateChestState(viewingBlock);
		}
	}
	
	private void updateChestState(Block block) {
		boolean open = openedChests.values().contains(block);
		Sound chestSound = (open ? Sound.BLOCK_CHEST_OPEN : Sound.BLOCK_CHEST_CLOSE);
		block.getWorld().playSound(block.getLocation(), chestSound, 1f, 1f);
		PacketUtil.setChestOpen(block, open);
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
		
		int numPlayers = players.size();
		int numHeroes = 0;
		if (numPlayers >= 15) numHeroes++;
		if (numPlayers >= 25) numHeroes++;
		if (numPlayers >= 35) numHeroes++;
		
		while (numHeroes > 0) {
			Player hero = Misc.getRandom(players);
			players.remove(hero);
			
			HeroType type = Misc.getRandomFrom(HeroType.VELVETINE, HeroType.ARTHEA, HeroType.TUI, HeroType.HERANA);
			addHero(hero, type);
			numHeroes--;
		}
		
		for (Player player : players) {
			addGamePlayer(player);
		}
	}
}
