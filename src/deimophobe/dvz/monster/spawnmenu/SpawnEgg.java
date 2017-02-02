package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophbe on 19/01/17.
 */
class SpawnEgg extends MobMenuItem {
	private final MobType mobType;
	
	private int quantity;
	private final int maxQuantity;
	private final double spawnChance;
	
	SpawnEgg(MobType type, ItemStack egg, int maxQuantity, double spawnChance) {
		super(egg, 0);
		
		this.mobType = type;
		
		this.quantity = 0;
		this.maxQuantity = maxQuantity;
		this.spawnChance = spawnChance;
	}
	
	private SpawnEgg(ConfigurationSection section) {
		super(ItemCreator.createItem(section.getConfigurationSection("egg"), Slot.HEAD), 0);
		
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
	public boolean isAvailable() {
		return (quantity != 0);
	}
	
	@Override
	public boolean onSelect(MonsterPlayer monster) {
		monster.spawnAs(mobType);
		quantity -= 1;
		return true;
	}
	
	private static final Map<String, SpawnEgg> eggMap = new HashMap<>();
	static {
		Configuration spawnConfig = YamlConfiguration.loadConfiguration(Game.getGame().getPlugin().getResource("spawn-eggs.yml"));
		for (String key : spawnConfig.getKeys(false)) {
			SpawnEgg egg = new SpawnEgg(spawnConfig.getConfigurationSection(key));
			eggMap.put(key.toLowerCase(), egg);
		}
	}
	public static SpawnEgg getEgg(String key) {
		return eggMap.get(key);
	}
}
