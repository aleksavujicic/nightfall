package deimophobe.dvz;

import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.AttributeModifier;
import minecraft.spigot.community.michel_0.api.ItemAttributes;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 18/01/17.
 */
public class ItemCreator {
	private ItemCreator() {}
	
	public static ItemStack createItem(
			Material type, short damage, byte data,
			String name, List<String> lore, int quantity,
			int attackDamage, int healthBoost, int speed,
			boolean kbResist, boolean bound, boolean shiny, int depth, int knockback,
			Slot slot) {
		
		ItemStack item = new ItemStack(type, quantity, damage, data);
		item.setDurability(damage);
		
		
		ItemMeta meta = item.getItemMeta();
		
		name = name.replace('&', ChatColor.COLOR_CHAR);
		for (int i=0; i < lore.size() ; i++) {
			String line = lore.get(i);
			String newline = line.replace('&',ChatColor.COLOR_CHAR);
			lore.set(i,newline);
		}
		lore.add("");
		meta.setDisplayName(name);
		meta.setLore(lore);
		
		meta.setUnbreakable(true);
		if (shiny)
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
		if (bound)
			meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
		if (depth != 0)
			meta.addEnchant(Enchantment.DEPTH_STRIDER, depth, true);
		if (knockback != 0)
			meta.addEnchant(Enchantment.KNOCKBACK, knockback, true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS);
		
		item.setItemMeta(meta);
		
		
		ItemAttributes attributes = new ItemAttributes();
		if (attackDamage != -1) {
			attributes.addModifier(new AttributeModifier(Attribute.ATTACK_DAMAGE, "Damage", slot, 0, attackDamage, UUID.randomUUID()));
			lore.add(ChatColor.BLUE + "Attack: " + attackDamage);
		}
		
		if (healthBoost != 0) {
			attributes.addModifier(new AttributeModifier(Attribute.MAX_HEALTH, "HealthBoost", slot, 0, healthBoost, UUID.randomUUID()));
			lore.add(ChatColor.BLUE + "Health: " + (healthBoost/2 + 10));
		}
		
		if (speed != 0) {
			attributes.addModifier(new AttributeModifier(Attribute.MOVEMENT_SPEED, "Speed", slot, 1, (double)speed/100, UUID.randomUUID()));
			if (speed > 0)
				lore.add(ChatColor.BLUE + "Speed: +" + speed + "%");
			else
				lore.add(ChatColor.RED + "Speed: " + speed + "%");
		}
		
		if (kbResist) {
			attributes.addModifier(new AttributeModifier(Attribute.KNOCKBACK_RESISTANCE, "KBResist", slot, 0, 1, UUID.randomUUID()));
			lore.add(ChatColor.BLUE + "Knockback Resistance");
		}
		
		item = attributes.apply(item);
		
		return item;
	}
	
	public static ItemStack createItem(ConfigurationSection section, Slot slot) {
		if (section == null) return null;
		Bukkit.getLogger().info("Creating material: " + section.getString("material"));
		Material type = Material.valueOf(section.getString("material").toUpperCase());
		short damage = (short) section.getInt("damage", 0);
		byte data = (byte) section.getInt("data", 0);
		
		String name = section.getString("name");
		List<String> lore = section.getStringList("lore");
		
		int quantity = section.getInt("quantity", 1);
		int attackDamage = section.getInt("attack", -1);
		int healthBoost = section.getInt("health", 0);
		int speed = section.getInt("speed", 0);
		
		boolean kbResist = section.getBoolean("kbresist", false);
		boolean bound = section.getBoolean("bound", false);
		boolean shiny = section.getBoolean("shiny", false);
		int depth = section.getInt("depth", 0);
		int knockback = section.getInt("knockback", 0);
		
		return createItem(type, damage, data, name, lore, quantity, attackDamage, healthBoost, speed, kbResist, bound, shiny, depth, knockback, slot);
	}
	
	public static List<ItemStack> createItems(ConfigurationSection section, Slot slot) {
		if (section == null) return null;
		List<ItemStack> items = new ArrayList<>();
		for (String key : section.getKeys(false)) {
			items.add(createItem(section.getConfigurationSection(key), slot));
		}
		return items;
	}
	
	
	// Item modifications
	public static ItemStack setAttribute(ItemStack item, Attribute attribute, int value, Slot slot) {
		if (value == 0) return item;
		
		item = item.clone();
		
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		
		switch (attribute) {
			case MAX_HEALTH:
				lore.add(ChatColor.BLUE + "Health: " + value);
				value = value*2 - 20;
				break;
			case MOVEMENT_SPEED:
				if (value > 0)
					lore.add(ChatColor.BLUE + "Speed: +" + value + "%");
				else
					lore.add(ChatColor.RED + "Speed: " + value + "%");
				break;
			case ATTACK_DAMAGE:
				lore.add(ChatColor.BLUE + "Attack: " + value);
				break;
			default:
				throw new IllegalArgumentException("Upgrading attribute '"+attribute+"' is not supported.");
		}
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		
		ItemAttributes attributes = new ItemAttributes();
		attributes.getFromStack(item);
		if (attribute == Attribute.MOVEMENT_SPEED)
			attributes.addModifier(new AttributeModifier(attribute, "SpeedUpgrade", slot, 2, (double)value/100, UUID.randomUUID()));
		else
			attributes.addModifier(new AttributeModifier(attribute, "Upgrade", slot, 0, value, UUID.randomUUID()));
		return attributes.apply(item);
	}
}
