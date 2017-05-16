package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.Misc;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.menu.MenuSession;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophbe on 19/01/17.
 */
public class SpawnEggMenuItem implements MenuItem<MonsterPlayer> {
	private final MobType mobType;
	
	private final ItemStack item;
	
	private final boolean permanent;
	private boolean enabled;
	
	private int quantity;
	private final int maxQuantity;
	private final double spawnChance;
	
	private SpawnEggMenuItem(ConfigurationSection section) {
		this.item = CustomItem.getItem(section.getConfigurationSection("egg"), "monster-egg", Slot.HEAD).createItemStack();
		
		this.mobType = MobType.getMobType(section.getString("mobtype"));
		
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
		if (isAvailable())
			return item;
		else
			return null;
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		if (!isAvailable()) return false;
		
		MonsterPlayer monster = session.getData();
		if (!DoomManager.getManager().isDoom()) {
			monster.spawnAs(mobType);
			quantity -= 1;
			session.closeSession();
		} else {
			monster.sendMessage(ChatColor.RED + "You cannot spawn during doom!");
		}
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
	
	public static boolean enableEgg(String key) {
		SpawnEggMenuItem egg = eggMap.get(key);
		if (egg == null) return false;
		
		egg.enabled = true;
		return true;
	}
}
