package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Deimophbe on 19/01/17.
 */
public class SpawnEggMenuItem implements MenuItem<MonsterPlayer> {
	private final Set<MobType> mobTypes;
	
	private final ItemStack item;
	
	private final boolean permanent;
	private boolean enabled;
	
	private int quantity;
	private int maxQuantity;
	private double spawnChance;
	private final String name;
	
	public SpawnEggMenuItem(ConfigurationSection section, String name) {
		this.item = CustomItem.getItem(section.getConfigurationSection("egg"), "monster-egg").createItemStack();
		this.name = name;
		
		List<String> mobs = section.getStringList("mobtype");
		if (mobs.isEmpty())
			mobs.add(section.getString("mobtype"));
		
		this.mobTypes = new HashSet<>();
		for (String mob : mobs) {
			try {
				mobTypes.add(MobType.getMobType(mob));
			} catch (UnknownEnumElementException e) {
				Bukkit.getLogger().severe("Unknown mob " + mob + " when creating spawnegg " + section.getName());
				e.printStackTrace();
			}
		}
		
		this.quantity = 0;
		this.maxQuantity = section.getInt("quantity", 1);
		this.spawnChance = section.getDouble("chance", 0.5);
		this.permanent = section.getBoolean("permanent", false);
		
		this.enabled = section.getBoolean("enabled", true);
	}
	
	public SpawnEggMenuItem(CustomItem item, String name, MobType type, int maxQuantity, double chance) {
		this.item = item.createItemStack();
		this.name = name;
		
		this.mobTypes = Collections.singleton(type);
		
		this.quantity = 0;
		this.maxQuantity = maxQuantity;
		this.spawnChance = chance;
		this.permanent = false;
		
		this.enabled = true;
	}
	
	public boolean tryRestock() {
		double rand = Math.random();
		if (rand <= spawnChance) {
			quantity = maxQuantity;
			return true;
		} else {
			return false;
		}
	}
	
	public void setMax(int max) { this.maxQuantity = max; }
	public void setSpawnChance(double chance) { this.spawnChance = chance; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	
	public String getName() {
		return name;
	}
	
	public void restock() {
		quantity = maxQuantity;
	}
	
	private boolean isAvailable() {
		return (enabled && (permanent || quantity != 0));
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
		if (permanent) {
			return item;
		}
		if (isAvailable()) {
			ItemStack newitem = item.clone();
			Player player = session.getPlayer();
			if (Game.getGame().isDebug(player)) newitem.setAmount(quantity);
			return newitem;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		if (!isAvailable()) return true; // Updates menu display
		
		MonsterPlayer monster = session.getData();
		
		if (!Game.getGame().getPhase().hasGameStarted() && Game.getGame().getPhase() != Phase.STARTING) {
			monster.sendMessage(ChatColor.RED + "You must wait until the mobs are released!");
			return false;
		}
		
		if (DoomManager.getManager().isDoom()) {
			monster.sendMessage(ChatColor.RED + "You cannot spawn during doom!");
			return false;
		}
		
		if (monster.isAlive()) {
			monster.sendMessage(ChatColor.RED + "You have already spawned as a mob!");
			session.closeSession();
			return false;
		}

		monster.spawnMob(Misc.getRandom(mobTypes));
		quantity -= 1;
		session.closeSession();
		return false;
	}
}
