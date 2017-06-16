package deimophobe.dvz.monster.mob;

import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.Misc;
import deimophobe.dvz.items.lore.LoreTemplate;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * Created by Deimophobe on 27/01/17.
 */
public class MobData {
	final String name;
	
	final String title;
	final boolean forceTitle;
	
	final DisguiseType disguiseType;
	final String playerName;
	final String skinName;
	
	private final int attack;
	private final int health;
	private final int speed;
	
	final boolean armourOnChest;
	private final CustomItem armour;
	private final CustomItem weapon;
	private final Map<String, CustomItem> items;
	
	final int immuneTime;
	final boolean proccable;
	final double damageRes;
	final double arrowRes;
	final int armourShred;
	final int torchXP;
	final boolean shrineImmune;
	
	private MobData() {
		name = "default";
		
		title = null;
		forceTitle = false;
		
		disguiseType = null;
		playerName = null;
		skinName = null;
		
		attack = 5;
		health = 10;
		speed = 0;
		
		armourOnChest = true;
		armour = null;
		weapon = null;
		items = new LinkedHashMap<>();
		
		immuneTime = 8;
		
		proccable = true;
		damageRes = 0.5;
		arrowRes = 0;
		armourShred = 10;
		torchXP = 10;
		shrineImmune = false;
	}
	
	private MobData(ConfigurationSection section, MobData parent) {
		name = section.getName();
		
		title = section.getString("title", parent.title);
		forceTitle = section.getBoolean("forcetitle", parent.forceTitle);
		
		if (section.contains("disguisetype")) {
			disguiseType = DisguiseType.valueOf(section.getString("disguisetype").toUpperCase());
		} else {
			disguiseType = parent.disguiseType;
		}
		playerName = section.getString("playername", parent.playerName);
		skinName = section.getString("skin", parent.skinName);
		
		attack = section.getInt("attack", parent.attack);
		health = section.getInt("health", parent.health);
		speed = section.getInt("speed", parent.speed);
		
		armourOnChest = section.getBoolean("armouronchest", parent.armourOnChest);
		if (section.contains("armour")) armour = CustomItem.getItem(section.getConfigurationSection("armour"), LoreTemplate.MOB, (armourOnChest ? Slot.CHEST : Slot.HEAD));
		else armour = CustomItem.tryClone(parent.armour);
		if (section.contains("weapon")) weapon = CustomItem.getItem(section.getConfigurationSection("weapon"), LoreTemplate.MOB, Slot.MAIN_HAND);
		else weapon = CustomItem.tryClone(parent.weapon);
		
		items = new LinkedHashMap<>(parent.items);
		ConfigurationSection itemSection = section.getConfigurationSection("items");
		if (itemSection != null) {
			for (String item : itemSection.getKeys(false)) {
				items.put(item, CustomItem.getItem(itemSection.getConfigurationSection(item), LoreTemplate.MOB, Slot.MAIN_HAND));
			}
		}
		immuneTime = section.getInt("immunetime", parent.immuneTime);
		
		proccable = section.getBoolean("proccable", parent.proccable);
		damageRes = section.getDouble("resistance", parent.damageRes);
		arrowRes = section.getDouble("arrowres", parent.arrowRes);
		armourShred = section.getInt("shred", parent.armourShred);
		torchXP = section.getInt("torchxp", parent.torchXP);
		shrineImmune = section.getBoolean("shrineimmune", parent.shrineImmune);
	}
	
	private void compile() {
		// Add stats to weapon
		if (weapon != null) {
			weapon.addModifier(ItemModifierType.ATTACK, attack);
			weapon.addModifier(ItemModifierType.ARMOUR_SHRED, armourShred);
		}
		
		// Make stats to armour
		if (armour != null) {
			int healthGain = (health - 10);
			armour.addModifier(ItemModifierType.HEALTH, healthGain);
			armour.addModifier(ItemModifierType.SPEED, speed);
			armour.addModifier(ItemModifierType.RESISTANCE, (int) (damageRes*100));
			armour.addModifier(ItemModifierType.ARROW_RESISTANCE, (int) (arrowRes*100));
			if (!proccable) armour.addModifier(ItemModifierType.UNPROCCABLE, 1);
		}
		
		// Make items immutable
		Set<String> itemNames = items.keySet();
		for (String name : itemNames) {
			items.compute(name, (k,v) -> v.immutableCopy());
		}
	}
	
	Map<String, CustomItem> getItems() {
		Map<String, CustomItem> newItems = new HashMap<>(items);
		newItems.put("weapon", weapon.clone());
		newItems.put("armour", armour.clone());
		
		return newItems;
	}
	
	private static final Map<String, MobData> mobs = new HashMap<>();
	static {
		ConfigurationSection mobData = Misc.getInternalFileConfig("mobs.yml");
		mobs.put("default", new MobData());
		for (String key : mobData.getKeys(false)) {
			String parentKey = mobData.getConfigurationSection(key).getString("parent", "default");
			mobs.put(key.toLowerCase(), new MobData(mobData.getConfigurationSection(key), getMobData(parentKey)));
		}
		
		for (MobData data : mobs.values())
			data.compile();
	}
	public static MobData getMobData(String type) {
		return mobs.get(type);
	}
}
