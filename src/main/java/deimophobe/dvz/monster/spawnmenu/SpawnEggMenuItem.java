package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.items.ItemCreator;
import deimophobe.dvz.Misc;
import deimophobe.dvz.Phase;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.menu.MenuSession;
import deimophobe.dvz.menu.SimpleItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.monster.upgrade.MobUpgrades;
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
public class SpawnEggMenuItem extends SimpleItem<MonsterPlayer> {
	
	private final MobType mobType;
	
	private int quantity;
	private final int maxQuantity;
	private final double spawnChance;
	
	private SpawnEggMenuItem(ConfigurationSection section) {
		super(ItemCreator.createItem(section.getConfigurationSection("egg"), Slot.HEAD));
		
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
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		if (quantity == 0) return false;
		
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
}
