package deimophobe.dvz.monster.mob;

import deimophobe.dvz.Game;
import deimophobe.dvz.ItemCreator;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by Deimophobe on 27/01/17.
 */
public class MobData {
	final String title;
	final boolean forceTitle;
	
	final DisguiseType disguiseType;
	final String playerName;
	final String skinName;
	
	final int attack;
	final int health;
	final int speed;
	
	final boolean armourOnChest;
	final ItemStack armour;
	final ItemStack weapon;
	final List<ItemStack> items;
	
	final int resLevel;
	final int jumpLevel;
	final boolean slowDig;
	final boolean invisible;
	final int immuneTime;
	
	final boolean proccable;
	final double arrowRes;
	final int armourShred;
	final int torchXP;
	final boolean shrineImmune;
	
	private MobData(ConfigurationSection section) {
		title = section.getString("title");
		forceTitle = section.getBoolean("forcetitle", false);
		
		disguiseType = null;
		playerName = section.getString("playername", "fake player name");
		skinName = section.getString("skin", "capmergor");
		
		attack = section.getInt("attack", 5);
		health = section.getInt("health", 10);
		speed = section.getInt("speed", 0);
		
		armourOnChest = section.getBoolean("armouronchest", true);
		armour = ItemCreator.createItem(section.getConfigurationSection("armour"), (armourOnChest ? Slot.CHEST : Slot.HEAD));
		weapon = ItemCreator.createItem(section.getConfigurationSection("weapon"), Slot.MAIN_HAND);
		items = new ArrayList<>();
		
		resLevel = section.getInt("res", 3);
		jumpLevel = section.getInt("jump", 0);
		slowDig = section.getBoolean("slowdig", false);
		invisible = section.getBoolean("invisible", false);
		immuneTime = section.getInt("immunetime", 8);
		
		proccable = section.getBoolean("proccable", true);
		arrowRes = section.getDouble("arrowres", 0);
		armourShred = section.getInt("shred", 10);
		torchXP = section.getInt("torchxp", 5);
		shrineImmune = section.getBoolean("shrineimmune", false);
	}
	
	private MobData(ConfigurationSection section, MobData parent) {
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
		armour = (section.contains("armour") ? ItemCreator.createItem(section.getConfigurationSection("armour"), (armourOnChest ? Slot.CHEST : Slot.HEAD)) : parent.armour);
		weapon = (section.contains("weapon") ? ItemCreator.createItem(section.getConfigurationSection("weapon"), Slot.MAIN_HAND) : parent.weapon);
		
		items = new ArrayList<>(parent.items);
		ConfigurationSection itemSection = section.getConfigurationSection("items");
		if (itemSection != null) {
			for (String item : itemSection.getKeys(false)) {
				items.add(ItemCreator.createItem(itemSection.getConfigurationSection(item), Slot.MAIN_HAND));
			}
		}
		
		resLevel = section.getInt("res", parent.resLevel);
		jumpLevel = section.getInt("jump", parent.jumpLevel);
		slowDig = section.getBoolean("slowdig", parent.slowDig);
		invisible = section.getBoolean("invisible", parent.invisible);
		immuneTime = section.getInt("immunetime", parent.immuneTime);
		
		proccable = section.getBoolean("proccable", parent.proccable);
		arrowRes = section.getDouble("arrowres", parent.arrowRes);
		armourShred = section.getInt("shred", parent.armourShred);
		torchXP = section.getInt("torchxp", parent.torchXP);
		shrineImmune = section.getBoolean("shrineimmune", parent.shrineImmune);
	}
	
	private static final Map<String, MobData> mobs = new HashMap<>();
	static {
		ConfigurationSection mobData = YamlConfiguration.loadConfiguration(Game.getGame().getPlugin().getResource("mobs.yml"));
		for (String key : mobData.getKeys(false)) {
			if (key.equals("default")) {
				mobs.put(key.toLowerCase(), new MobData(mobData.getConfigurationSection(key)));
			} else {
				String parentKey = mobData.getConfigurationSection(key).getString("parent", "default");
				mobs.put(key.toLowerCase(), new MobData(mobData.getConfigurationSection(key), getMobData(parentKey)));
			}
		}
	}
	public static MobData getMobData(String type) {
		return mobs.get(type);
	}
	public static MobData getMobData(MobType type) {
		return mobs.get(type.getName());
	}
}
