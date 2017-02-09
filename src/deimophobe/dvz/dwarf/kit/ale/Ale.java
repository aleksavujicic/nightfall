package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.CooldownItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 16/01/17.
 */
public abstract class Ale extends CooldownItem {
	
	private final int manaCost;
	
	public Ale(Dwarf dwarf, AleType type, int manaCost) {
		super(dwarf, getItem(type), 10);
		this.manaCost = manaCost;
	}
	
	protected boolean useMana() {
		return dwarf.useMana(manaCost);
	}
	public void onGotHit(GameEntity hitter, DamageType type, double damage) {}
	
	@Override
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
		}
		return null;
	}
	
	private static final Map<AleType, ItemStack> ales = new HashMap<>();
	static {
		ConfigurationSection aleSection = DwarfManager.getManager().getConfig().getConfigurationSection("ale");
		
		ales.put(AleType.HEALING, ItemCreator.createItem(aleSection.getConfigurationSection("healing"), Slot.MAIN_HAND));
		ales.put(AleType.JIMMYJUICE, ItemCreator.createItem(aleSection.getConfigurationSection("jj"), Slot.MAIN_HAND));
		ales.put(AleType.HOLY, ItemCreator.createItem(aleSection.getConfigurationSection("holy"), Slot.MAIN_HAND));
		ales.put(AleType.TRINKET, ItemCreator.createItem(aleSection.getConfigurationSection("trinket"), Slot.MAIN_HAND));
		ales.put(AleType.REGROWTH, ItemCreator.createItem(aleSection.getConfigurationSection("regrowth"), Slot.MAIN_HAND));
	}
	public static ItemStack getItem(AleType aleType) {
		return ales.get(aleType);
	}
}
