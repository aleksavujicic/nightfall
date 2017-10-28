package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 24/03/17.
 */
public enum KitElementType {
	DRB,
	GRB,
	GREATSWORD,
	AXE,
	HAMMER,
	DAGGER,
	TOMBMAKER,
	TIGERFIST,
	GLAIVE,
	HUNTER_WEP,
	RAPIER,

	SHORTBOW,
	LONGBOW,
	DRAGONSKIN,
	EBOW,
	LIGHTBOW,
	WARPWEAVER,
	CROSSBOW,
	VOLCANIC,
	TURRET,
	MINIGUN,
	
	HEALING_ALE,
	HOLY_ALE,
	JIMMY_JUICE,
	TRINKET,
	REGROWTH_STAR,
	CHUGGING_ALE,
	STRONG_ALE,
	
	RUNEBLESSED,
	QUIVER,
	STUDDED,
	COIL,
	STURDY,
	NATURE_SUIT,
	
	COMPASS,
	CHESTO,
	SLOWFALL,
	DARKVISION,
	AVENGE,
	
	
	DWARF_PICK,
	DWARF_SHOVEL,
	DWARF_AXE,
	
	
	HERO_SLOWFALL,
	HERO_ALE,
	
	TUI_HAMMER,
	WILDFIRE,
	
	TINDERFLAME,
	WAND,
	ROCKET_BOOTS,
	
	HEALER_TOTEM,
	CADUCEUS,
	ELYSTRIA,
	
	HORN,
	VELSWORD,
	VELBOW,

	SCEPTEROFMAGMA,
	HEALINGGLOW,
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
			case GREATSWORD: return new Greatsword(dwarf);
			case AXE: return new AxeOfMalice(dwarf);
			case DAGGER: return new Dagger(dwarf);
			case TOMBMAKER: return new Tombmaker(dwarf);
			case HAMMER: return new Hammer(dwarf);
			case TIGERFIST: return new TigerFist(dwarf);
			case GLAIVE: return new Glaive(dwarf);
			case HUNTER_WEP: return new HunterWep(dwarf);
			case RAPIER: return new Rapier(dwarf);
				
			case SHORTBOW: return new Shortbow(dwarf);
			case LONGBOW: return new Longbow(dwarf);
			case DRAGONSKIN: return new Dragonskin(dwarf);
			case EBOW: return new Ebow(dwarf);
			case LIGHTBOW: return new Lightbow(dwarf);
			case WARPWEAVER: return new Warpweaver(dwarf);
			case CROSSBOW: return new Crossbow(dwarf);
			case VOLCANIC: return new VolcanicGauntlet(dwarf);
			case TURRET: return new TurretBow(dwarf);
			case MINIGUN: return new Minigun(dwarf);
				
			case HEALING_ALE: return new HealingAle(dwarf);
			case HOLY_ALE: return new HolyAle(dwarf);
			case JIMMY_JUICE: return new JimmyJuice(dwarf);
			case TRINKET: return new Trinket(dwarf);
			case REGROWTH_STAR: return new Regrowth(dwarf);
			case CHUGGING_ALE: return new ChuggingAle(dwarf);
			case STRONG_ALE: return new StrongAle(dwarf);
				
			case RUNEBLESSED: return new Runeblessed(dwarf);
			case QUIVER: return new Quiver(dwarf);
			case STUDDED: return new StuddedArmour(dwarf);
			case COIL: return new MagicCoil(dwarf);
			case STURDY: return new Sturdy(dwarf);
			case NATURE_SUIT: return new NatureSuit(dwarf);
				
			case COMPASS: return new DwarfCompass(dwarf);
			case CHESTO: return new Chestomatic(dwarf);
			case SLOWFALL: return new Slowfall(dwarf, true);
			case DARKVISION: return new DarkVision(dwarf);
			case AVENGE: return new Avenge(dwarf);
			
			
			case DWARF_AXE: return new DwarfAxe(dwarf);
			case DWARF_PICK: return new DwarfPickaxe(dwarf);
			case DWARF_SHOVEL: return new DwarfShovel(dwarf);
			
			
			case HERO_SLOWFALL: return new Slowfall(dwarf, false);
			case HERO_ALE: return new HeroAle(dwarf);
			
			case TUI_HAMMER: return new TuiHammer(dwarf);
			case WILDFIRE: return new Wildfire(dwarf);
			
			case TINDERFLAME: return new Tinderflame(dwarf);
			case WAND: return new Wand(dwarf);
			case ROCKET_BOOTS: return new RocketBoots(dwarf);
			
			case HEALER_TOTEM: return new HealerTotem(dwarf);
			case CADUCEUS: return new Caduceus(dwarf);
			case ELYSTRIA: return new Elystria(dwarf);
			
			case HORN: return new Horn(dwarf);
			case VELSWORD: return new VelSword(dwarf);
			case VELBOW: return new VelBow(dwarf);

			case SCEPTEROFMAGMA: return new ScepterOfMagma(dwarf);
			case HEALINGGLOW: return new HealingGlow(dwarf);
		}
		throw new UnsupportedOperationException("Unimplemented element type: " + this);
	}
}
