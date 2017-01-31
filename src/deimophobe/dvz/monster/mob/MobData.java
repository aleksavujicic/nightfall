package deimophobe.dvz.monster.mob;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.monster.MobManager;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
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
	final String playerDisguise;
	final ItemStack helmet;
	final ItemStack chest;
	final List<ItemStack> items;
	final Collection<PotionEffect> effects;
	
	final boolean proccable;
	final double arrowRes;
	final int armourShred;
	final int torchXP;
	final boolean shrineImmune;
	
	private static final int POTION_LENGTH = 27*60*20;
	
	protected MobData(ConfigurationSection section) {
		MobData parent = null;
		if (section.contains("parent")) {
			parent = getMobData(section.getString("parent"));
		}
		
		if (parent == null) {
			title = section.getString("title");
			forceTitle = section.getBoolean("forcetitle", false);
		}  else {
			title = section.getString("title", parent.title);
			forceTitle = section.getBoolean("forcetitle", parent.forceTitle);
		}
		
		if (section.contains("disguisetype")) {
			disguiseType = DisguiseType.valueOf(section.getString("disguisetype").toUpperCase());
		} else {
			if (parent == null) {
				disguiseType = null;
			} else {
				disguiseType = parent.disguiseType;
			}
		}
		if (parent == null)
			playerDisguise = section.getString("playername");
		else
			playerDisguise = section.getString("playername", parent.playerDisguise);
		
		
		if (section.contains("helmet") || parent == null)
			helmet = ItemCreator.createItem(section.getConfigurationSection("helmet"), Slot.HEAD);
		else
			helmet = parent.helmet;
		
		if (section.contains("chest") || parent == null)
			chest = ItemCreator.createItem(section.getConfigurationSection("chest"), Slot.CHEST);
		else
			chest = parent.chest;
		
		if (section.contains("items") || parent == null)
			items = ItemCreator.createItems(section.getConfigurationSection("items"), Slot.MAIN_HAND);
		else
			items = parent.items;
		
		
		int resLevel = section.getInt("res", 3);
		int jumpLevel = section.getInt("jump", 0);
		boolean slowDig = section.getBoolean("slowdig", false);
		int immuneTime = section.getInt("immunetime", 8);
		
		effects = new HashSet<>();
		if (resLevel != 0) {
			effects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, POTION_LENGTH, resLevel - 1, true, false));
			effects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, POTION_LENGTH, 0, true, false));
		}
		if (jumpLevel != 0) {
			effects.add(new PotionEffect(PotionEffectType.JUMP, POTION_LENGTH, jumpLevel - 1, true, false));
		}
		if (slowDig) {
			effects.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, POTION_LENGTH, 3, true, false));
		}
		if (immuneTime != 0) {
			effects.add(new PotionEffect(PotionEffectType.LUCK, immuneTime*20, 0));
		}
		
		if (parent == null) {
			proccable = section.getBoolean("proccable", true);
			arrowRes = section.getDouble("arrowres", 0);
			armourShred = section.getInt("shred", 10);
			torchXP = section.getInt("torchxp", 5);
			shrineImmune = section.getBoolean("shrineimmune", false);
		} else  {
			proccable = section.getBoolean("proccable", parent.proccable);
			arrowRes = section.getDouble("arrowres", parent.arrowRes);
			armourShred = section.getInt("shred", parent.armourShred);
			torchXP = section.getInt("torchxp", parent.torchXP);
			shrineImmune = section.getBoolean("shrineimmune", parent.shrineImmune);
		}
	}
	
	private static final Map<String, MobData> mobs = new HashMap<>();
	static {
		ConfigurationSection mobData = MobManager.getManager().getMobConfig();
		for (String key : mobData.getKeys(false)) {
			mobs.put(key.toLowerCase(), new MobData(mobData.getConfigurationSection(key)));
		}
	}
	public static MobData getMobData(String type) {
		return mobs.get(type);
	}
	public static MobData getMobData(MobType type) {
		return mobs.get(type.getName());
	}
}
