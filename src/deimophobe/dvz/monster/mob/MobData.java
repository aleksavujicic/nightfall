package deimophobe.dvz.monster.mob;

import deimophobe.dvz.Game;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.Misc;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.xml.datatype.DatatypeConfigurationException;
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
	
	final int attack;
	final int health;
	final int speed;
	
	final boolean armourOnChest;
	final ItemStack armour;
	final ItemStack weapon;
	final List<ItemStack> items;
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
		items = new ArrayList<>();
		
		immuneTime = 8;
		
		proccable = true;
		damageRes = 0.5;
		arrowRes = 0;
		armourShred = 10;
		torchXP = 5;
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
		armour = (section.contains("armour") ? ItemCreator.createItem(section.getConfigurationSection("armour"), (armourOnChest ? Slot.CHEST : Slot.HEAD)) : parent.armour);
		weapon = (section.contains("weapon") ? ItemCreator.createItem(section.getConfigurationSection("weapon"), Slot.MAIN_HAND) : parent.weapon);
		
		items = new ArrayList<>(parent.items);
		ConfigurationSection itemSection = section.getConfigurationSection("items");
		if (itemSection != null) {
			for (String item : itemSection.getKeys(false)) {
				items.add(ItemCreator.createItem(itemSection.getConfigurationSection(item), Slot.MAIN_HAND));
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
	
	private static final Map<String, MobData> mobs = new HashMap<>();
	static {
		ConfigurationSection mobData = Misc.getInternalFileConfig("mobs.yml");
		mobs.put("default", new MobData());
		for (String key : mobData.getKeys(false)) {
			String parentKey = mobData.getConfigurationSection(key).getString("parent", "default");
			mobs.put(key.toLowerCase(), new MobData(mobData.getConfigurationSection(key), getMobData(parentKey)));
		}
	}
	public static MobData getMobData(String type) {
		return mobs.get(type);
	}
	public static MobData getMobData(MobType type) {
		return mobs.get(type.getName());
	}
}
