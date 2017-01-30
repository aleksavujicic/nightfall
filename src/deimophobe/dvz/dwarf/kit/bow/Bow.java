package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Bow extends DwarvenItem {
	
	protected final int power;
	
	Bow(Dwarf dwarf, BowType type, int power) {
		super(dwarf, getItem(type));
		this.power = power;
	}
	
	@Override
	public void update() {}
	@Override
	public float fractionComplete() {return 0;}
	
	@Override
	protected boolean ability(Action type) {return false;}
	
	
	public double onHit(GameEntity monster) {return power;}
	public void onKill(GameEntity monster, boolean b) {}
	public Projectile onBowFire(Arrow arrow, float force) {
		return arrow;
	}
	public void onArrowLand(Arrow arrow, Block hitBlock) {}
	
	public static Bow createBow(Dwarf dwarf, BowType bowType) {
		switch (bowType) {
			case SHORTBOW:
				return new Bow(dwarf, BowType.SHORTBOW, 30);
			case DRAGONSKIN:
				return new Dragonskin(dwarf);
			case LONGBOW:
				return new Longbow(dwarf);
			case LIGHTBOW:
				return new Lightbow(dwarf);
			case CROSSBOW:
				return new Bow(dwarf, BowType.CROSSBOW, 30);
			case WARPWEAVER:
				return new Bow(dwarf, BowType.WARPWEAVER, 30);
			case EBOW:
				return new Ebow(dwarf);
		}
		return null;
	}
	
	private static final Map<BowType, ItemStack> bows = new HashMap<>();
	static {
		ConfigurationSection bowSection = DwarfManager.getManager().getConfig().getConfigurationSection("bow");
		
		bows.put(BowType.SHORTBOW, ItemCreator.createItem(bowSection.getConfigurationSection("shortbow"), Slot.MAIN_HAND));
		bows.put(BowType.DRAGONSKIN, ItemCreator.createItem(bowSection.getConfigurationSection("dragonskin"), Slot.MAIN_HAND));
		bows.put(BowType.LIGHTBOW, ItemCreator.createItem(bowSection.getConfigurationSection("lightbow"), Slot.MAIN_HAND));
		bows.put(BowType.LONGBOW, ItemCreator.createItem(bowSection.getConfigurationSection("longbow"), Slot.MAIN_HAND));
		bows.put(BowType.CROSSBOW, ItemCreator.createItem(bowSection.getConfigurationSection("xbow"), Slot.MAIN_HAND));
		bows.put(BowType.EBOW, ItemCreator.createItem(bowSection.getConfigurationSection("ebow"), Slot.MAIN_HAND));
		bows.put(BowType.WARPWEAVER, ItemCreator.createItem(bowSection.getConfigurationSection("warpweaver"), Slot.MAIN_HAND));
	}
	public static ItemStack getItem(BowType bowType) {
		return bows.get(bowType);
	}
}
