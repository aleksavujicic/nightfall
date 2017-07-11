package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.menu.MenuItem;
import deimophobe.nightfall.menu.MenuSession;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophbe on 19/01/17.
 */
public class SpawnEggMenuItem implements MenuItem<MonsterPlayer> {
	private final Set<MobType> mobTypes;
	
	private final ItemStack item;
	
	private final boolean permanent;
	private boolean enabled;
	
	private int quantity;
	private final int maxQuantity;
	private final double spawnChance;
	
	private SpawnEggMenuItem(ConfigurationSection section) {
		this.item = CustomItem.getItem(section.getConfigurationSection("egg"), "monster-egg", Slot.HEAD).createItemStack();
		
		List<String> mobs = section.getStringList("mobtype");
		if (mobs.isEmpty())
			mobs.add(section.getString("mobtype"));
		
		this.mobTypes = new HashSet<>();
		for (String mob : mobs)
			mobTypes.add(MobType.getMobType(mob));
		
		this.quantity = 0;
		this.maxQuantity = section.getInt("quantity", 1);
		this.spawnChance = section.getDouble("chance", 0.5);
		
		this.permanent = !(section.contains("quantity") && section.contains("chance"));
		
		this.enabled = section.getBoolean("enabled", true);
	}
	
	boolean tryRestock() {
		double rand = Math.random();
		if (rand <= spawnChance) {
			quantity = maxQuantity;
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isAvailable() {
		return (enabled && (permanent || quantity != 0));
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
		if (permanent)
			return item;
		
		if (isAvailable()) {
			ItemStack newitem = item.clone();
				newitem.setAmount(quantity);
			return newitem;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		if (!isAvailable()) return false;
		
		MonsterPlayer monster = session.getData();
		
		if (!Game.getGame().getPhase().hasGameStarted()) {
			monster.sendMessage(ChatColor.RED + "You must wait until the mobs are released!");
			return false;
		}
		
		if (DoomManager.getManager().isDoom()) {
			monster.sendMessage(ChatColor.RED + "You cannot spawn during doom!");
			return false;
		}
		
		monster.spawnMob(Misc.getRandom(mobTypes));
		quantity -= 1;
		session.closeSession();
		return false;
	}
	
	
	
	
	private static final Map<String, SpawnEggMenuItem> eggMap = new HashMap<>();
	static {
		Configuration spawnConfig = Misc.getInternalFileConfig("spawn-eggs.yml");
		for (String key : spawnConfig.getKeys(false)) {
			SpawnEggMenuItem egg = new SpawnEggMenuItem(spawnConfig.getConfigurationSection(key));
			eggMap.put(key.toLowerCase(), egg);
		}
	}
	public static SpawnEggMenuItem getEgg(String key) {
		return eggMap.get(key);
	}
	public static SpawnEggMenuItem getEgg(MobType type) {
		return eggMap.get(type.toString().toLowerCase());
	}
	public static void resetEggs() {
		for (SpawnEggMenuItem egg : eggMap.values())
			egg.quantity = 0;
	}
}
