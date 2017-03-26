package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 16/01/17.
 */
public abstract class Ale {
	
	protected Dwarf dwarf;
	public Ale(Dwarf dwarf, AleType type) {
		//super(dwarf, getItem(type), 10);
	}
	
	public float fractionComplete() {
		return -1;
	}
	
	public static Ale createAle(Dwarf dwarf, AleType aleType) {
		switch (aleType) {
			case HEALING:
				return new HealingAle(dwarf);
			case HOLY:
				return new HolyAle(dwarf);
			case JIMMYJUICE:
				return new JimmyJuice(dwarf);
			case REGROWTH:
				return new Regrowth(dwarf);
			case TRINKET:
				return new Trinket(dwarf);
			case HERO:
				return new HeroAle(dwarf);
		}
		throw new IllegalArgumentException("Unknown ale type: " + aleType);
	}
	
	private static final Map<AleType, ItemStack> ales = new HashMap<>();
	static {
		ConfigurationSection aleSection = DwarfManager.getManager().getConfig().getConfigurationSection("ale");
		
		ales.put(AleType.HEALING, ItemCreator.createItem(aleSection.getConfigurationSection("healing"), Slot.MAIN_HAND));
		ales.put(AleType.JIMMYJUICE, ItemCreator.createItem(aleSection.getConfigurationSection("jj"), Slot.MAIN_HAND));
		ales.put(AleType.HOLY, ItemCreator.createItem(aleSection.getConfigurationSection("holy"), Slot.MAIN_HAND));
		ales.put(AleType.TRINKET, ItemCreator.createItem(aleSection.getConfigurationSection("trinket"), Slot.MAIN_HAND));
		ales.put(AleType.REGROWTH, ItemCreator.createItem(aleSection.getConfigurationSection("regrowth"), Slot.MAIN_HAND));
		ales.put(AleType.HERO, null);
	}
	public static ItemStack getItem(AleType aleType) {
		return ales.get(aleType);
	}
}
