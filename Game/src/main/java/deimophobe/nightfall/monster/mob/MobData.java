package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.skin.Skin;
import deimophobe.nightfall.util.ArmourSlot;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 27/01/17.
 */
public class MobData {
	//private final Class<? extends Mob> mobClass; TODO
	
	final String fullName;
	
	final String title;
	final boolean forceTitle;
	
	final DisguiseType disguiseType;
	final String skinName;
	
	final boolean ranged;
	
	private final int attack;
	private final int health;
	private final int speed;
	
	final ArmourSlot armourSlot;
	private final CustomItem armour;
	private final CustomItem weapon;
	private final Map<String, CustomItem> items;
	
	final int immuneTime;
	final boolean proccable;
	final double damageRes;
	final double arrowRes;
	final int armourShred;
	final int torchXP;
	final int charmTime;
	final double shrineWeight;
	final double shrineProtDamage;
	final int shrineXP;
	final boolean canRun;
	final int depthStriderLevel;
	
	private final Map<String, MobSound> sounds;
	
	
	private static final MobData DEFAULT_DATA = new MobData();
	private MobData() {
		fullName = "default";
		
		title = null;
		forceTitle = false;
		
		disguiseType = null;
		skinName = null;
		
		ranged = false;
		
		attack = 5;
		health = 10;
		speed = 0;
		armourShred = 5;
		
		proccable = true;
		damageRes = 0.6;
		arrowRes = 0;
		torchXP = 50;
		charmTime = 160;
		shrineWeight = 1;
		shrineProtDamage = -1; // -1 = insta kill
		shrineXP = 2;
		immuneTime = 8;
		canRun = true;
		depthStriderLevel = 3;
		
		armourSlot = ArmourSlot.CHEST;
		armour = null;
		weapon = null;
		items = new LinkedHashMap<>();
		
		sounds = new HashMap<>();
		
		// Add compass
		CustomItem compass = CustomItem.getItem(NightfallPlugin.getInternalFileConfig("misc-items.yml").getConfigurationSection("mob-compass"), LoreTemplate.MOB);
		items.put("compass", compass);
	}
	
	private MobData(String fullKey, ConfigurationSection section) {
		fullName = fullKey;
		
		String parentString = section.getString("parent", null);
		MobData parent;
		if (parentString == null)
			parent = DEFAULT_DATA;
		else
			parent = getMobDataWithContext(parentString, section.getRoot(), fullKey.split("\\.")[0]);
		
		title = section.getString("title", parent.title);
		forceTitle = section.getBoolean("forcetitle", parent.forceTitle);
		
		if (section.contains("disguisetype")) {
			String disguiseName = section.getString("disguisetype");
			try {
				disguiseType = DisguiseType.valueOf(disguiseName.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid disguise type '" + disguiseName + "' for mob " + fullName, e);
			}
		} else {
			disguiseType = parent.disguiseType;
		}
		skinName = section.getString("skin", parent.skinName);
		
		ranged = section.getBoolean("ranged", parent.ranged);
		
		attack = section.getInt("attack", parent.attack);
		health = section.getInt("health", parent.health);
		speed = section.getInt("speed", parent.speed);
		armourShred = section.getInt("shred", parent.armourShred);
		
		proccable = section.getBoolean("proccable", parent.proccable);
		damageRes = section.getDouble("resistance", parent.damageRes);
		arrowRes = section.getDouble("arrowres", parent.arrowRes);
		torchXP = section.getInt("torchxp", parent.torchXP);
		charmTime = section.getInt("charmtime", parent.charmTime);
		shrineWeight = section.getDouble("shrineweight", parent.shrineWeight);
		shrineProtDamage = section.getDouble("shrine-prot-damage", parent.shrineProtDamage);
		shrineXP = section.getInt("shrine-xp", parent.shrineXP);
		immuneTime = section.getInt("immunetime", parent.immuneTime);
		canRun = section.getBoolean("canrun", parent.canRun);
		depthStriderLevel = section.getInt("depthstrider", parent.depthStriderLevel);
		
		if (section.contains("armourslot")) armourSlot = ArmourSlot.fromString(section.getString("armourslot"));
		else armourSlot = parent.armourSlot;
		if (section.contains("armour")) armour = CustomItem.getItem(section.getConfigurationSection("armour"), LoreTemplate.MOB);
		else armour = CustomItem.tryClone(parent.armour);
		if (section.contains("weapon")) weapon = CustomItem.getItem(section.getConfigurationSection("weapon"), LoreTemplate.MOB);
		else weapon = CustomItem.tryClone(parent.weapon);
		
		items = new LinkedHashMap<>(parent.items);
		ConfigurationSection itemSection = section.getConfigurationSection("items");
		if (itemSection != null) {
			for (String item : itemSection.getKeys(false)) {
				items.put(item, CustomItem.getItem(itemSection.getConfigurationSection(item), LoreTemplate.MOB));
			}
		}
		
		sounds = new HashMap<>(parent.sounds);
		if (section.contains("sounds")) {
			ConfigurationSection soundSec = section.getConfigurationSection("sounds");
			for (String soundName : soundSec.getKeys(false)) {
				sounds.put(soundName, new MobSound(soundSec.getConfigurationSection(soundName)));
			}
		}
	}
	
	// ===== ITEMS =====
	
	private void compileItems() {
		// Add stats to weapon
		if (weapon != null) {
			if (!ranged) {
				weapon.addModifier(ItemModifierType.ATTACK, attack);
			} else {
				weapon.addModifier(ItemModifierType.POWER, attack);
			}
			weapon.addModifier(ItemModifierType.ARMOUR_SHRED, armourShred);
		}
		
		// Make stats to armour
		if (armour != null) {
			int healthGain = (health - 10);
			armour.addModifier(ItemModifierType.HEALTH, healthGain);
			armour.addModifier(ItemModifierType.SPEED, speed);
			armour.addModifier(ItemModifierType.RESISTANCE, (int) (damageRes*100));
			armour.addModifier(ItemModifierType.ARROW_RESISTANCE, (int) (arrowRes*100));
			if (depthStriderLevel != 0)
				armour.addModifier(ItemModifierType.DEPTH_STRIDER, depthStriderLevel);
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
		if (weapon != null)
			newItems.put("weapon", weapon.clone());
		if (armour != null)
			newItems.put("armour", armour.clone());
		
		return newItems;
	}
	
	CustomItem getAsWeapon(String name) {
		if (!items.containsKey(name)) throw new IllegalArgumentException("No item called '" + name + "' for mob " + this.fullName);
		
		CustomItem item = items.get(name).clone();
		item.addModifier(ItemModifierType.ATTACK, attack);
		item.addModifier(ItemModifierType.ARMOUR_SHRED, armourShred);
		return item;
	}
	
	boolean hasWeapon() {
		return weapon != null;
	}
	boolean hasArmour() {
		return armour != null;
	}
	
	
	// ====== REFLECTION =====
	
	private void getFields() {
		//TODO: Need to initiated MobData with class object somehow?
		
	}
	
	// ====== VERIFICATION =====
	
	/** Verifies that the values of this MobData
	 * are valid (so no nulls etc.). This is only done
	 * on those which are to be used as mobs. (So that base
	 * types such as 'ghostblade-base' can be in an invalid state).
	 */
	private void verify() {
		// This should be practically impossible - but checking just in case
		if (fullName == null)
			throw new IllegalStateException("Mobdata name is missing?!");
		
		if (title == null)
			throw new IllegalStateException("Title for mob " + fullName + " is not defined.");
		
		if (disguiseType == DisguiseType.PLAYER) {
			if (skinName == null)
				throw new IllegalStateException("Mob " + fullName + " has player disguise but no skin name.");
			if (!Skin.skinExists(skinName))
				throw new IllegalStateException("Mob " + fullName + " has player disguise with skin '" + skinName + "' but skin does not exist.");
		}
		
		if (health == 0)
			throw new IllegalStateException("Mob " + fullName + " has zero health.");
	}
	
	
	// ====== DATA FETCHER =====
	
	static MobData getMobData(String fullKey) {
		return getMobData(fullKey, true);
	}
	
	private static MobData getMobData(String fullKey, boolean verify) {
		String[] keySplit = fullKey.split("\\.");
		if (keySplit.length > 2) throw new IllegalArgumentException("MobData key '" + fullKey + "' is invalid. Keys can contain at most two levels.");
		
		String base = keySplit[0];
		String sub = (keySplit.length == 2 ? keySplit[1] : "base");
		
		ConfigurationSection file = NightfallPlugin.getInternalFileConfig("mobs/" + base + ".yml");
		MobData data;
		if (sub.equals("base")) {
			if (file.contains(sub)) data = new MobData(fullKey, file.getConfigurationSection(sub));
			else data = new MobData(fullKey, file);
		} else {
			if (!file.contains(sub)) throw new IllegalArgumentException("MobData key '" + fullKey + "' is invalid. Key not found.");
			data = new MobData(fullKey, file.getConfigurationSection(sub));
		}
		
		if (verify) {
			data.compileItems();
			data.verify();
		}
		
		return data;
	}
	
	private static MobData getMobDataWithContext(String name, ConfigurationSection context, String oldKeyBase) {
		if (context.contains(name)) {
			return new MobData(oldKeyBase + "." + name, context.getConfigurationSection(name));
		} else {
			return getMobData(name, false);
		}
	}
	
	
	
	// ====== SOUND =====
	
	void playSound(String sound, MonsterPlayer monster) {
		sounds.putIfAbsent(sound, new MobSound(sound));
		MobSound mobSound = sounds.get(sound);
		mobSound.play(monster);
	}
	
	private class MobSound {
		private final String soundPath;
		private final float pitch;
		private final float volume;
		private final double chance;
		
		private MobSound(String name) {
			this.soundPath =  "mob."+MobData.this.fullName +"."+name;
			this.pitch = 1;
			this.chance = 1;
			this.volume = 1;
		}
		
		private MobSound(ConfigurationSection section) {
			this.soundPath = section.getString("path", "mob."+MobData.this.fullName +"."+section.getName());
			this.pitch = (float) section.getDouble("pitch", 1);
			this.volume = (float) section.getDouble("volume", 1);
			this.chance = section.getDouble("chance", 1);
		}
		
		private void play(MonsterPlayer monster) {
			if (Math.random() <= chance)
				monster.playSound(soundPath, volume, pitch, true);
		}
	}
}
