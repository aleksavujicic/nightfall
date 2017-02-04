package deimophobe.dvz.monster;

import deimophobe.dvz.Game;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.GamePlayerManager;
import deimophobe.dvz.menu.Menu;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.monster.spawnmenu.SpawnMenu;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class MonsterManager extends GamePlayerManager<MonsterPlayer> {
	private static MonsterManager ourManager = new MonsterManager();
	
	public static MonsterManager getManager() {
		return ourManager;
	}
	
	public void setupManager() {
		Plugin plugin = Game.getGame().getPlugin();
		Bukkit.getPluginManager().registerEvents(new MobListener(), plugin);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (MonsterPlayer mob : getGamePlayers()) {
					mob.update();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
		
		setupTeams("mobs", ChatColor.DARK_RED);
	}
	
	@Override
	protected MonsterPlayer createGamePlayerFromPlayer(Player player) {
		return new MonsterPlayer(player);
	}
	
	public Collection<GameEntity> getMobsAndAIs() {
		Collection<GameEntity> entities = new ArrayList<>();
		entities.addAll(getGamePlayers());
		entities.addAll(AIManager.getManager().getAIs());
		return entities;
	}
	
	
	// --------------------------------------------------------
	//                   MENUS N STUFF
	// --------------------------------------------------------
	
	private final SpawnMenu menu = new SpawnMenu();
	
	public void onMobRelease() {
		AIManager.getManager().setup();
		DoomManager.getManager().setup();
		
		// For mob xp
		new BukkitRunnable() {
			@Override
			public void run() {
				for (MonsterPlayer mob : getGamePlayers()) {
					mob.updateXP();
				}
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 20, 20);
		
		menu.setup();
	}
	
	public void showMobMenu(MonsterPlayer monster) {
		menu.showTo(monster);
	}
	
	public void onClick(int slot, Inventory clickedInventory, MonsterPlayer monster) {
		Menu menu = Menu.getMenuFromInv(clickedInventory);
		if (menu != null) {
			menu.select(slot, monster);
		}
	}
}
	
