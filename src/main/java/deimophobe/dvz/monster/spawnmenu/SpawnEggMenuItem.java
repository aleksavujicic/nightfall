package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.items.ItemCreator;
import deimophobe.dvz.Misc;
import deimophobe.dvz.Phase;
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
	private final ItemStack item;
	
	private final MobType mobType;
	
	private int quantity;
	private final int maxQuantity;
	private final double spawnChance;
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> player) {
		return item;
	}
	
	private SpawnEggMenuItem(MobType type, ItemStack egg, int maxQuantity, double spawnChance) {
		this.item = egg;
		
		this.mobType = type;
		
		this.quantity = 0;
		this.maxQuantity = maxQuantity;
		this.spawnChance = spawnChance;
	}
	
	private SpawnEggMenuItem(ConfigurationSection section) {
		this.item = ItemCreator.createItem(section.getConfigurationSection("egg"), Slot.HEAD);
		
		mobType = MobType.getMobType(section.getString("mobtype"));
		
		quantity = 0;
		maxQuantity = section.getInt("quantity", 1);
		spawnChance = section.getDouble("chance", 0.5);
	}
	
	boolean tryRespawn() {
		double rand = Math.random();
		if (true || rand <= spawnChance) {
			quantity = maxQuantity;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isAvailable(MonsterPlayer player) {
		return (quantity != 0);
	}
	
	@Override
	public boolean select(MonsterPlayer monster) {
		if (Game.getGame().getPhase() == Phase.GAME || Game.getGame().getPhase() == Phase.END) {
			if (!DoomManager.getManager().isDoom()) {
				monster.spawnAs(mobType);
				quantity -= 1;
			} else {
				monster.sendMessage(ChatColor.RED + "You cannot spawn during doom!");
			}
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
}
