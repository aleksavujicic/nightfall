package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.KitElement;

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
	
	TUI_HAMMER,
	WILDFIRE,
	
	TINDERFLAME,
	WAND,
	ROCKET_BOOTS,
	
	
	HERO_ALE;
	
	public static KitElementType get(String name) {
		for (KitElementType type : values()) {
			if (type.name().equalsIgnoreCase(name))
				return type;
		}
		throw new IllegalArgumentException("Unknown element type: " + name);
	}
	
	public KitElement createElement(Dwarf dwarf) {
		switch (this) {
			case DRB: return new GreaterRuneblade(dwarf);
			case GRB: return new GreaterRuneblade(dwarf);
			case AXE: return new AxeOfMalice(dwarf);
			case DAGGER: return new Dagger(dwarf);
			case TOMBMAKER: return new Tombmaker(dwarf);
			case HAMMER: return new Hammer(dwarf);
				
			case SHORTBOW:
				break;
			case LONGBOW:
				break;
			case DRAGONSKIN:
				break;
			case EBOW:
				break;
			case LIGHTBOW:
				break;
			case WARPWEAVER:
				break;
			case CROSSBOW:
				break;
				
			case HEALING_ALE: return new HealingAle(dwarf);
			case HOLY_ALE: return new HolyAle(dwarf);
			case JIMMY_JUICE: return new JimmyJuice(dwarf);
			case TRINKET: return new Trinket(dwarf);
			case REGROWTH_STAR: return new Regrowth(dwarf);
				
			case RUNEBLESSED:
				break;
			case QUIVER:
				break;
			case STUDDED: return new StuddedArmour(dwarf);
			case COIL: return new MagicCoil(dwarf);
				
			case COMPASS:
				break;
			case SAFEFALL: return new JumpySafefall(dwarf);
			case DARKVISION: return new DarkVision(dwarf);
			case AVENGE: return new Avenge(dwarf);
			
			
			case HERO_SAFEFALL: return new Safefall(dwarf);
			case HERO_ALE: return new HeroAle(dwarf);
			
			case TUI_HAMMER: return new TuiHammer(dwarf);
			
			case TINDERFLAME: return new Tinderflame(dwarf);
			case ROCKET_BOOTS: return new RocketBoots(dwarf);
		}
		throw new UnsupportedOperationException("Unimplemented element type: " + this);
	}
}
