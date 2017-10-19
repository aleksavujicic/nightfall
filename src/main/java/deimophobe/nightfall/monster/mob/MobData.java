package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ArmourSlot;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.Skin;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.lore.LoreTemplate;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
	
	final ArmourSlot slot;
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
	final boolean canRun;
	
	private final Map<String, MobSound> sounds = new HashMap<>();
	
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
		armourShred = 5;
		
		proccable = true;
		damageRes = 0.6;
		arrowRes = 0;
		torchXP = 10;
		shrineImmune = false;
		immuneTime = 8;
		canRun = true;
		
		slot = ArmourSlot.CHEST;
		armour = null;
		weapon = null;
		items = new LinkedHashMap<>();
	}
	
	private MobData(ConfigurationSection section, MobData parent) {
		name = section.getName();
		
		title = section.getString("title", parent.title);
		forceTitle = section.getBoolean("forcetitle", parent.forceTitle);
		
		if (section.contains("disguisetype")) {
			String disguiseName = section.getString("disguisetype");
			try {
				disguiseType = DisguiseType.valueOf(disguiseName.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid disguise type '" + disguiseName + "' for mob " + name, e);
			}
		} else {
			disguiseType = parent.disguiseType;
		}
		playerName = section.getString("playername", parent.playerName);
		skinName = section.getString("skin", parent.skinName);
		
		attack = section.getInt("attack", parent.attack);
		health = section.getInt("health", parent.health);
		speed = section.getInt("speed", parent.speed);
		armourShred = section.getInt("shred", parent.armourShred);
		
		proccable = section.getBoolean("proccable", parent.proccable);
		damageRes = section.getDouble("resistance", parent.damageRes);
		arrowRes = section.getDouble("arrowres", parent.arrowRes);
		torchXP = section.getInt("torchxp", parent.torchXP);
		shrineImmune = section.getBoolean("shrineimmune", parent.shrineImmune);
		immuneTime = section.getInt("immunetime", parent.immuneTime);
		canRun = section.getBoolean("canrun", parent.canRun);
		
		if (section.contains("armourslot")) slot = ArmourSlot.fromString(section.getString("armourslot"));
		else slot = parent.slot;
		if (section.contains("armour")) armour = CustomItem.getItem(section.getConfigurationSection("armour"), LoreTemplate.MOB, slot.getSlot());
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
		if (weapon != null)
			newItems.put("weapon", weapon.clone());
		if (armour != null)
			newItems.put("armour", armour.clone());
		
		return newItems;
	}
	
	boolean hasWeapon() {
		return weapon != null;
	}
	boolean hasArmour() {
		return armour != null;
	}
	
	/** Verifies that the values of this MobData
	 * are valid (so no nulls etc.). This is only done
	 * on those which are to be used as mobs. (So that base
	 * types such as 'ghostblade-base' can be in an invalid state).
	 */
	void verify() {
		// This should be practically impossible - but checking just in case
		if (name == null)
			throw new IllegalStateException("Mobdata name is missing?!");
		
		if (title == null)
			throw new IllegalStateException("Title for mob " + name + " is not defined.");
		
		if (disguiseType == DisguiseType.PLAYER) {
			if (playerName == null)
				throw new IllegalStateException("Mob " + name + " has player disguise but no player name.");
			if (skinName == null)
				throw new IllegalStateException("Mob " + name + " has player disguise but no skin name.");
			if (!Skin.skinExists(skinName))
				throw new IllegalStateException("Mob " + name + " has player disguise with skin '" + skinName + "' but skin does not exist.");
		}
		
		if (health == 0)
			throw new IllegalStateException("Mob " + name + " has zero health.");
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
	static MobData getMobData(String type) {
		if (!mobs.containsKey(type))
			throw new IllegalArgumentException("No mobdata with key " + type);
		return mobs.get(type);
	}
	
	
	
	void playSound(String sound, MonsterPlayer monster) {
		MobSound mobSound = sounds.putIfAbsent(sound, new MobSound(sound, 1));
		mobSound.play(monster);
	}
	
	private class MobSound {
		private final String soundName;
		private final float pitch;
		
		private MobSound(String name, float pitch) {
			this.soundName =  "mob."+MobData.this.name+"."+name;
			this.pitch = pitch;
		}
		
		private void play(MonsterPlayer monster) {
			Bukkit.broadcastMessage("Play " + soundName);
			monster.playSound(soundName, 1f, pitch, true);
		}
	}
}
