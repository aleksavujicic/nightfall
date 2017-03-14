package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.Hat;
import deimophobe.dvz.dwarf.kit.ArmourType;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
enum  LoadoutItem {
	GRB(new SwordModifier(SwordType.GRB), 24, Category.SWORD),
	MALICE(new SwordModifier(SwordType.AXE_OF_MALICE), 24, Category.SWORD),
	HAMMER(new SwordModifier(SwordType.HAMMER), 16, Category.SWORD),
	DAGGER(new SwordModifier(SwordType.DAGGER), 16, Category.SWORD),
	TOMBMAKER(new SwordModifier(SwordType.TOMBMAKER), 4, Category.SWORD),
	
	DRAGONSKIN(new BowModifier(BowType.DRAGONSKIN), 16, Category.BOW),
	LONGBOW(new BowModifier(BowType.LONGBOW), 20, Category.BOW),
	WARPWEAVER(new BowModifier(BowType.WARPWEAVER), 12, Category.BOW),
	LIGHTBOW(new BowModifier(BowType.LIGHTBOW), 12, Category.BOW),
	EBOW(new BowModifier(BowType.EBOW), 12, Category.BOW),
	CROSSBOW(new BowModifier(BowType.CROSSBOW), 12, Category.BOW),
	
	HOLY(new AleModifier(AleType.HOLY), 8, Category.ALE),
	JJ(new AleModifier(AleType.JIMMYJUICE), 8, Category.ALE),
	TRINKET(new AleModifier(AleType.TRINKET), 8, Category.ALE),
	REGROWTH(new AleModifier(AleType.REGROWTH), 8, Category.ALE),
	
	RUNEBLESSED(new ArmourModifier(ArmourType.RUNEBLESSED), 8, Category.ARMOUR),
	QUIVER(new ArmourModifier(ArmourType.QUIVER), 8, Category.ARMOUR),
	STUDDED(new ArmourModifier(ArmourType.STUDDED), 8, Category.ARMOUR),
	COIL(new ArmourModifier(ArmourType.COIL), 8, Category.ARMOUR),
	
	AVENGE(new PassiveModifier(Passive.AVENGE), 8),
	SAFEFALL(new PassiveModifier(Passive.SAFEFALL), 4),
	DARKVISION(new PassiveModifier(Passive.DARKVISION), 4),
	
	SOS_ONE(new ConsumableModifier(ConsumableType.SOS, 1), 4),
	SOS_TWO(new ConsumableModifier(ConsumableType.SOS, 2), 8),
	MORTAR_ONE(new ConsumableModifier(ConsumableType.MORTAR, 64), 4),
	MORTAR_TWO(new ConsumableModifier(ConsumableType.MORTAR, 128), 8),
	WIZARD_ONE(new ConsumableModifier(ConsumableType.WIZARD_MORTAR, 16), 4),
	WIZARD_TWO(new ConsumableModifier(ConsumableType.WIZARD_MORTAR, 32), 8),
	LAMPS_ONE(new ConsumableModifier(ConsumableType.LAMP, 12), 4),
	LAMPS_TWO(new ConsumableModifier(ConsumableType.LAMP, 24), 8),
	WRENCH_ONE(new ConsumableModifier(ConsumableType.WRENCH, 2), 4),
	WRENCH_TWO(new ConsumableModifier(ConsumableType.WRENCH, 4), 8),
	HEALING_ONE(new ConsumableModifier(ConsumableType.HEAL_STATION, 4), 4),
	HEALING_TWO(new ConsumableModifier(ConsumableType.HEAL_STATION, 8), 8),
	
	CROWN_HAT(new HatModifier(Hat.CROWN), 0, Category.HAT),
	FLOWER_HAT(new HatModifier(Hat.FLOWER), 0, Category.HAT),
	WITCH_HAT(new HatModifier(Hat.WITCH), 0, Category.HAT),
	DV_GOGGLES(new HatModifier(Hat.DVGOGGLES), 0, Category.HAT),
	JIMMY_CAP(new HatModifier(Hat.JIMMYCAP), 0, Category.HAT),
	
	RANGER(new TitleModifier("Ranger"), 0, Category.TITLE),
	PALADIN(new TitleModifier("Paladin"), 0, Category.TITLE),
	GRAVEDIGGER(new TitleModifier("Gravedigger"), 0, Category.TITLE),
	ANCIENT(new TitleModifier("Ancient"), 0, Category.TITLE),
	FIRELORD(new TitleModifier("Firelord"), 0, Category.TITLE),
	HUNTER(new TitleModifier("Hunter"), 0, Category.TITLE),
	CAPTAIN(new TitleModifier("Captain"), 0, Category.TITLE),
	ENGINEER(new TitleModifier("Engineer"), 0, Category.TITLE),
	PHARAOH(new TitleModifier("Pharaoh"), 0, Category.TITLE),
	PEASANT(new TitleModifier("Peasant"), 0, Category.TITLE),
	JIMMY(new TitleModifier("Jimmy"), 0, Category.TITLE),
	SIR(new TitleModifier("Sir"), 0, Category.TITLE),
	LADY(new TitleModifier("Lady"), 0, Category.TITLE),
	BANANA(new TitleModifier("Banana"), 0, Category.TITLE),
	POTATO(new TitleModifier("Potato"), 0, Category.TITLE),
	GNOMISH(new TitleModifier("Gnomish"), 0, Category.TITLE),
	ELVEN(new TitleModifier("Elven"), 0, Category.TITLE),
	CLERIC(new TitleModifier("Cleric"), 0, Category.TITLE),
	WARRIOR(new TitleModifier("Warrior"), 0, Category.TITLE),
	SQUIRE(new TitleModifier("Squire"), 0, Category.TITLE),
	;
	
	private final PropertyModifier modifier;
	private final int cost;
	private final Category category;
	
	LoadoutItem(PropertyModifier modifier, int cost) {
		this(modifier, cost, null);
	}
	
	LoadoutItem(PropertyModifier modifier, int cost, Category category) {
		this.modifier = modifier;
		this.cost = cost;
		this.category = category;
	}
	
	int getCost() {
		return cost;
	}
	
	void modify(DwarfData dwarfData) {
		modifier.modify(dwarfData);
	}
	
	static LoadoutItem getItem(String id) {
		id = id.toLowerCase();
		for (LoadoutItem item : values()) {
			if (item.toString().toLowerCase().equals(id))
				return item;
		}
		throw new IllegalArgumentException("Unknown ID for loadout item: '" + id + "'.");
	}
	
	Set<LoadoutItem> getItemsInCategory() {
		if (category == null) return new HashSet<>();
		
		Set<LoadoutItem> items = new HashSet<>();
		for (LoadoutItem item : values()) {
			if (category == item.category)
				items.add(item);
		}
		return items;
	}
	
	private enum Category {
		SWORD,
		BOW,
		ALE,
		ARMOUR,
		HAT,
		TITLE
	}
	
	private static abstract class PropertyModifier {
		abstract void modify(DwarfData dwarfData);
	}
	
	
	private static class SwordModifier extends PropertyModifier {
		private final SwordType type;
		
		private SwordModifier(SwordType type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.setSwordType(type);
		}
	}
	private static class BowModifier extends PropertyModifier {
		private final BowType type;
		
		private BowModifier(BowType type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.setBowType(type);
		}
	}
	private static class AleModifier extends PropertyModifier {
		private final AleType type;
		
		private AleModifier(AleType type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.setAleType(type);
		}
	}
	private static class ArmourModifier extends PropertyModifier {
		private final ArmourType type;
		
		private ArmourModifier(ArmourType type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.setArmour(type);
		}
	}
	
	private static class ConsumableModifier extends PropertyModifier {
		private final ConsumableType type;
		private final int quantity;
		
		private ConsumableModifier(ConsumableType type, int quantity) {
			this.type = type;
			this.quantity = quantity;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.incrementConsumable(type, quantity);
		}
	}
	private static class PassiveModifier extends PropertyModifier {
		private final Passive type;
		
		private PassiveModifier(Passive type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.addPassive(type);
		}
	}
	
	private static class HatModifier extends PropertyModifier {
		private final Hat hat;
		
		private HatModifier(Hat hat) {
			this.hat = hat;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.setHat(hat);
		}
	}
	private static class TitleModifier extends PropertyModifier {
		private final String title;
		
		private TitleModifier(String title) {
			this.title = title;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.setTitle(title);
		}
	}
}
