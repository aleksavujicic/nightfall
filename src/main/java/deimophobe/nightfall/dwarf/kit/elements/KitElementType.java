package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitElement;
import deimophobe.nightfall.dwarf.kit.elements.accessory.*;
import deimophobe.nightfall.dwarf.kit.elements.armour.*;
import deimophobe.nightfall.dwarf.kit.elements.healing.*;
import deimophobe.nightfall.dwarf.kit.elements.hero.*;
import deimophobe.nightfall.dwarf.kit.elements.melee.*;
import deimophobe.nightfall.dwarf.kit.elements.ranged.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 24/03/17.
 */
public enum KitElementType {
	// Swords
	DRB,
	GRB,
	AXE,
	HAMMER,
	DAGGER,
	RAPIER,
	GLAIVE,
	SCEPTER,
	HUNTING_CLAWS,
	GREATSWORD,
	SHADOW_BLADE,

	// Bows
	SHORTBOW,
	LONGBOW,
	DRAGONSKIN,
	EBOW,
	LIGHTBOW,
	WARPWEAVER,
	CROSSBOW,
	VOLCANIC,
	TURRET,
	
	// Ales
	HEALING_ALE,
	HEARTY_ALE,
	JIMMY_JUICE,
	TRINKET,
	REGROWTH_STAR,
	CHUGGING_ALE,
	STRONG_ALE,
	HEALINGGLOW,
	
	// Armours
	RUNEBLESSED,
	QUIVER,
	STUDDED,
	COIL,
	STURDY,
	NATURE_SUIT,
	BERSERKER,
	
	// Accessories
	COMPASS,
	CHESTO,
	SLOWFALL,
	DARKVISION,
	AVENGE,
	TOMBMAKER,
	RESURRECTION,
	BRICKLAYER,
	
	// Tools
	DWARF_PICK,
	DWARF_SHOVEL,
	DWARF_AXE,
	
	
	// ---- HERO STUFF ----
	// Base
	HERO_SLOWFALL,
	HERO_ALE,
	
	// Tui
	TUI_HAMMER,
	WILDFIRE,
	
	// Nosovin
	TINDERFLAME,
	WAND,
	ROCKET_BOOTS,
	
	// Arthea
	HEALER_TOTEM,
	CADUCEUS,
	ELYSTRIA,
	LUMINOUS,
	
	// Velvetine
	HORN,
	VELSWORD,
	VELBOW,

	// Oxysis
	DRUCRIST,
	WILDE_STAFF,

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
			// Swords
			case DRB: return new DwarvenRuneblade(dwarf);
			case GRB: return new GreaterRuneblade(dwarf);
			case AXE: return new AxeOfMalice(dwarf);
			case HAMMER: return new Hammer(dwarf);
			case DAGGER: return new Dagger(dwarf);
			case RAPIER: return new Rapier(dwarf);
			case GLAIVE: return new Glaive(dwarf);
			case SCEPTER: return new Scepter(dwarf);
			case HUNTING_CLAWS: return new HuntingClaws(dwarf);
			case GREATSWORD: return new Greatsword(dwarf);
			case SHADOW_BLADE: return new ShadowBlade(dwarf);
			
			// Bows
			case SHORTBOW: return new Shortbow(dwarf);
			case LONGBOW: return new Longbow(dwarf);
			case DRAGONSKIN: return new Dragonskin(dwarf);
			case EBOW: return new Ebow(dwarf);
			case LIGHTBOW: return new Lightbow(dwarf);
			case WARPWEAVER: return new Warpweaver(dwarf);
			case CROSSBOW: return new Crossbow(dwarf);
			case VOLCANIC: return new VolcanicGauntlet(dwarf);
			case TURRET: return new TurretBow(dwarf);
			
			// Ales
			case HEALING_ALE: return new HealingAle(dwarf);
			case HEARTY_ALE: return new HeartyAle(dwarf);
			case JIMMY_JUICE: return new JimmyJuice(dwarf);
			case TRINKET: return new Trinket(dwarf);
			case REGROWTH_STAR: return new Regrowth(dwarf);
			case CHUGGING_ALE: return new ChuggingAle(dwarf);
			case STRONG_ALE: return new StrongAle(dwarf);
			case HEALINGGLOW: return new HealingGlow(dwarf);
			
			// Armours
			case RUNEBLESSED: return new Runeblessed(dwarf);
			case QUIVER: return new Quiver(dwarf);
			case STUDDED: return new StuddedArmour(dwarf);
			case COIL: return new MagicCoil(dwarf);
			case STURDY: return new Sturdy(dwarf);
			case NATURE_SUIT: return new NatureSuit(dwarf);
			case BERSERKER: return new BerserkArmour(dwarf);
			
			// Accessory
			case COMPASS: return new DwarfCompass(dwarf);
			case CHESTO: return new Chestomatic(dwarf);
			case SLOWFALL: return new Slowfall(dwarf, true);
			case DARKVISION: return new DarkVision(dwarf);
			case AVENGE: return new Avenge(dwarf);
			case TOMBMAKER: return new Tombmaker(dwarf);
			case RESURRECTION: return new Resurrection(dwarf);
			case BRICKLAYER: return new Bricklayer(dwarf);
			
			// Tools
			case DWARF_AXE: return new DwarfAxe(dwarf);
			case DWARF_PICK: return new DwarfPickaxe(dwarf);
			case DWARF_SHOVEL: return new DwarfShovel(dwarf);
			
			// ---- HERO STUFF -----
			// Base
			case HERO_SLOWFALL: return new Slowfall(dwarf, false);
			case HERO_ALE: return new HeroAle(dwarf);
			
			// Tui
			case TUI_HAMMER: return new TuiHammer(dwarf);
			case WILDFIRE: return new Wildfire(dwarf);
			
			// 'Nosovin'
			case TINDERFLAME: return new Tinderflame(dwarf);
			case WAND: return new Wand(dwarf);
			case ROCKET_BOOTS: return new RocketBoots(dwarf);
			
			// Arthea
			case HEALER_TOTEM: return new HealerTotem(dwarf);
			case CADUCEUS: return new Caduceus(dwarf);
			case ELYSTRIA: return new Elystria(dwarf);
			case LUMINOUS: return new Luminous(dwarf);
			
			// Velvetine
			case HORN: return new Horn(dwarf);
			case VELSWORD: return new VelSword(dwarf);
			case VELBOW: return new VelBow(dwarf);

			// Oxysis
			case DRUCRIST: return new Drucrist(dwarf);
			case WILDE_STAFF: return new WildeStaff(dwarf);
		}
		throw new UnsupportedOperationException("Unimplemented element type: " + this);
	}
}
