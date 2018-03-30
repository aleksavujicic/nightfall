package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.accessory.*;
import deimophobe.nightfall.dwarf.kit.armour.*;
import deimophobe.nightfall.dwarf.kit.healing.*;
import deimophobe.nightfall.dwarf.kit.hero.*;
import deimophobe.nightfall.dwarf.kit.hero.lyra.FeralInstinct;
import deimophobe.nightfall.dwarf.kit.hero.lyra.SilvetrisClaws;
import deimophobe.nightfall.dwarf.kit.melee.*;
import deimophobe.nightfall.dwarf.kit.ranged.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Deimophobe on 24/03/17.
 */
public enum KitPieceType {
	// Swords
	DRB(DwarvenRuneblade::new),
	RUNESWORD(Runesword::new),
	BLOOD_AXE(BloodiedWaraxe::new),
	HAMMER(Hammer::new),
	DAGGER(Dagger::new),
	RAPIER(Rapier::new),
	GLAIVE(Glaive::new),
	SCEPTER(Scepter::new),
	HUNTING_CLAWS(HuntingClaws::new),
	GREATSWORD(Greatsword::new),
	SOUL_BLADE(Soulblade::new),

	// Bows
	SHORTBOW(Shortbow::new),
	LONGBOW(Longbow::new),
	PHOENIX_BOW(PhoenixBow::new),
	JADE_BOW(JadeBow::new),
	LIGHTBOW(Lightbow::new),
	WARPWEAVER(Warpweaver::new),
	CROSSBOW(Crossbow::new),
	VOLCANIC(VolcanicGauntlet::new),
	TURRET(TurretBow::new),
	
	// Ales
	HEALING_ALE(HealingAle::new),
	HEARTY_ALE(HeartyAle::new),
	SAFETY_JUICE(SafetyJuice::new),
	PENDANT(RubyPendant::new),
	REGROWTH_STAR(Regrowth::new),
	CHUGGING_ALE(ChuggingAle::new),
	STRONG_ALE(StrongAle::new),
	
	// Armours
	STURDY(Sturdy::new),
	QUIVER(Quiver::new),
	STUDDED(StuddedArmour::new),
	FAIRY_BAND(FairyBand::new),
	BERSERKER(BerserkArmour::new),
	NATURE_SUIT(NatureSuit::new),
	
	// Accessories
	COMPASS(DwarfCompass::new),
	CHESTO(Chestomatic::new),
	SLOWFALL(Slowfall::new),
	DARKVISION(DarkVision::new),
	AVENGE(Avenge::new),
	TOMBMAKER(Tombmaker::new),
	RESURRECTION(Resurrection::new),
	BRICKLAYER(Bricklayer::new),
	CHISEL(Chisel::new),
	CLOCK(Clock::new),
	
	UNTIMELY_DEMISE(UntimelyDemise::new),
	
	// Tools
	DWARF_PICK(DwarfPickaxe::new),
	DWARF_SHOVEL(DwarfShovel::new),
	DWARF_AXE(DwarfAxe::new),
	
	
	// Stupid joke shit
	PENY_STICK(PenyStick::new),
	
	
	// ---- HERO STUFF ----
	// Base
	HERO_BASE(HeroBase::new),
	
	// Tui
	TUI_HAMMER(TuiHammer::new),
	WILDFIRE(Wildfire::new),
	
	// Nosovin
	TINDERFLAME(Tinderflame::new),
	WAND(Wand::new),
	ROCKET_BOOTS(RocketBoots::new),
	
	// Arthea
	HEALER_TOTEM(HealerTotem::new),
	CADUCEUS(Caduceus::new),
	ELYSTRIA(Elystria::new),
	LUMINOUS(Luminous::new),
	
	// Velvetine
	HORN(Horn::new),
	VELSWORD(VelSword::new),
	VELBOW(VelBow::new),
	
	// Herana
	MERMAID_TAIL(MermaidTail::new),
	TRIDENT(Trident::new),
	BUBBLE_BEAM(BubbleBeam::new),
	ICE_BOW(IceBow::new),
	
	// Lyra
	SILVETRIS_CLAWS(SilvetrisClaws::new),
	FERAL_INSTINCT(FeralInstinct::new),

	// Oxysis
	DRUCRIST(Drucrist::new),
	WILDE_STAFF(WildeStaff::new),

	;
	
	private final Function<Dwarf, KitPiece> kitPieceCreator;
	KitPieceType(Function<Dwarf, KitPiece> kitPieceCreator) {
		this.kitPieceCreator = kitPieceCreator;
	}
	
	public static KitPieceType fromString(String name) throws UnknownEnumElementException {
		return Misc.getEnumMemberFromString(name, values(), "KitPieceType");
	}
	
	public static boolean isValidKitPiece(String name) {
		try {
			fromString(name);
			return true;
		} catch (UnknownEnumElementException e) {
			return false;
		}
	}
	
	public static Set<String> getPieceNames() {
		Set<String> names = new HashSet<>();
		for (KitPieceType type : values()) {
			names.add(type.toString().toLowerCase());
		}
		return names;
	}
	
	public KitPiece createPiece(Dwarf dwarf) {
		return kitPieceCreator.apply(dwarf);
	}
}
