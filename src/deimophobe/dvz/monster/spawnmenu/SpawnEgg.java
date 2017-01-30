package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophbe on 19/01/17.
 */
class SpawnEgg {
	private final MobType mobTemplate;
	private final ItemStack egg;
	
	private int quantity;
	private final int maxQuantity;
	private final double spawnChance;
	
	private final int index;
	
	SpawnEgg(MobType type, ItemStack egg, int maxQuantity, double spawnChance, int index) {
		this.mobTemplate = type;
		this.egg = egg;
		
		this.quantity = 0;
		this.maxQuantity = maxQuantity;
		this.spawnChance = spawnChance;
		
		this.index = index;
	}
	
	static SpawnEgg createEgg(ConfigurationSection section) {
		MobType type = MobType.getMobType(section.getString("mobtype"));
		
		ItemStack egg = ItemCreator.createItem(section.getConfigurationSection("egg"), Slot.HEAD);
		
		int maxQuantity = section.getInt("quantity", 1);
		double chance = section.getDouble("chance", 0.5);
		int index = section.getInt("index", 0);
		
		return new SpawnEgg(type, egg, maxQuantity, chance, index);
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
	
	ItemStack getEgg() {
		return egg;
	}
	
	int getIndex() {
		return index;
	}
	
	boolean canSpawn() {
		return (quantity != 0);
	}
	
	void spawn(MonsterPlayer monster) {
		monster.spawnAs(mobTemplate);
		quantity -= 1;
	}
}
