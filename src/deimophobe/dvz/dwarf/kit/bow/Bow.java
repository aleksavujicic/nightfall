package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Bow extends DwarvenItem {
	
	protected final int power;
	private final BowType type;
	
	Bow(Dwarf dwarf, BowType type) {
		super(dwarf, getItem(type));
		this.type = type;
		this.power = type.getPower();
	}
	
	public BowType getBowType() {
		return type;
	}
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {return power;}
	public Projectile onBowFire(Arrow arrow, float force) {
		return arrow;
	}
	public void onProjectileLand(Projectile arrow, Block hitBlock) {}
	
	public static Bow createBow(Dwarf dwarf, BowType bowType) {
		switch (bowType) {
			case SHORTBOW:
				return new Bow(dwarf, BowType.SHORTBOW);
			case DRAGONSKIN:
				return new Dragonskin(dwarf);
			case LONGBOW:
				return new Longbow(dwarf);
			case LIGHTBOW:
				return new Lightbow(dwarf);
			case CROSSBOW:
				return new Crossbow(dwarf);
			case WARPWEAVER:
				return new Warpweaver(dwarf);
			case EBOW:
				return new Ebow(dwarf);
		}
		return null;
	}
	
	private static final Map<BowType, ItemStack> bows = new HashMap<>();
	static {
		ConfigurationSection bowSection = DwarfManager.getManager().getConfig().getConfigurationSection("bow");
		
		for (BowType type : BowType.values()) {
			ItemStack bow = ItemCreator.createItem(bowSection.getConfigurationSection(type.getName()), Slot.MAIN_HAND);
			
			ItemMeta meta = bow.getItemMeta();
			List<String> lore = meta.getLore();
			lore.add(ChatColor.BLUE + "Power: " + type.getPower());
			meta.setLore(lore);
			bow.setItemMeta(meta);
			
			bows.put(type, bow);
		}
	}
	
	
	public static ItemStack getItem(BowType bowType) {
		return bows.get(bowType);
	}
}
