package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.HealerTotem;
import deimophobe.dvz.dwarf.kit.KitElement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 24/03/17.
 */
public enum KitElementType {
	DRB,
	GRB,
	AXE,
	HAMMER,
	DAGGER,
	TOMBMAKER,
	TIGERFIST,
	
	SHORTBOW,
	LONGBOW,
	DRAGONSKIN,
	EBOW,
	LIGHTBOW,
	WARPWEAVER,
	CROSSBOW,
	
	HEALING_ALE,
	HOLY_ALE,
	JIMMY_JUICE,
	TRINKET,
	REGROWTH_STAR,
	
	RUNEBLESSED,
	QUIVER,
	STUDDED,
	COIL,
	
	COMPASS,
	SAFEFALL,
	DARKVISION,
	AVENGE,
	
	
	DWARF_PICK,
	DWARF_SHOVEL,
	DWARF_AXE,
	
	
	HERO_SAFEFALL,
	HERO_ALE,
	
	TUI_HAMMER,
	WILDFIRE,
	
	TINDERFLAME,
	WAND,
	ROCKET_BOOTS,
	
	HEALER_TOTEM,
	KAD_POLE
	
	;
	
	public static boolean isElement(String name) {
		for (KitElementType type : values()) {
			if (type.name().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	public static KitElementType get(String name) {
		for (KitElementType type : values()) {
			if (type.name().equalsIgnoreCase(name))
				return type;
		}
		throw new IllegalArgumentException("Unknown element type: " + name);
	}
	
	public static Set<String> getElementNames() {
		Set<String> names = new HashSet<>();
		for (KitElementType type : values()) {
			names.add(type.toString().toLowerCase());
		}
		return names;
	}
	
	public KitElement createElement(Dwarf dwarf) {
		switch (this) {
			case DRB: return new DwarvenRuneblade(dwarf);
			case GRB: return new GreaterRuneblade(dwarf);
			case AXE: return new AxeOfMalice(dwarf);
			case DAGGER: return new Dagger(dwarf);
			case TOMBMAKER: return new Tombmaker(dwarf);
			case HAMMER: return new Hammer(dwarf);
			case TIGERFIST: return new TigerFist(dwarf);
				
			case SHORTBOW: return new Shortbow(dwarf);
			case LONGBOW: return new Longbow(dwarf);
			case DRAGONSKIN: return new Dragonskin(dwarf);
			case EBOW: return new Ebow(dwarf);
			case LIGHTBOW: return new Lightbow(dwarf);
			case WARPWEAVER: return new Warpweaver(dwarf);
			case CROSSBOW: return new Crossbow(dwarf);
				
			case HEALING_ALE: return new HealingAle(dwarf);
			case HOLY_ALE: return new HolyAle(dwarf);
			case JIMMY_JUICE: return new JimmyJuice(dwarf);
			case TRINKET: return new Trinket(dwarf);
			case REGROWTH_STAR: return new Regrowth(dwarf);
				
			case RUNEBLESSED: return new Runeblessed(dwarf);
			case QUIVER: return new Quiver(dwarf);
			case STUDDED: return new StuddedArmour(dwarf);
			case COIL: return new MagicCoil(dwarf);
				
			case COMPASS: return new DwarfCompass(dwarf);
			case SAFEFALL: return new JumpySafefall(dwarf);
			case DARKVISION: return new DarkVision(dwarf);
			case AVENGE: return new Avenge(dwarf);
			
			
			case DWARF_AXE: return new DwarfAxe(dwarf);
			case DWARF_PICK: return new DwarfPickaxe(dwarf);
			case DWARF_SHOVEL: return new DwarfShovel(dwarf);
			
			
			case HERO_SAFEFALL: return new Safefall(dwarf);
			case HERO_ALE: return new HeroAle(dwarf);
			
			case TUI_HAMMER: return new TuiHammer(dwarf);
			case WILDFIRE: return new Wildfire(dwarf);
			
			case TINDERFLAME: return new Tinderflame(dwarf);
			case WAND: return new Wand(dwarf);
			case ROCKET_BOOTS: return new RocketBoots(dwarf);
			
			case HEALER_TOTEM: return new HealerTotem(dwarf);
			case KAD_POLE: return new KadPole(dwarf);
		}
		throw new UnsupportedOperationException("Unimplemented element type: " + this);
	}
}
