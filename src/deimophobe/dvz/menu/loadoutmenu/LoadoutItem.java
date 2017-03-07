package deimophobe.dvz.menu.loadoutmenu;

import deimophobe.dvz.dwarf.kit.ArmourType;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;

/**
 * Created by Deimophobe on 7/03/17.
 */
enum  LoadoutItem {
	GRB(new SwordModifier(SwordType.GRB), "grb", 16),
	MALICE(new SwordModifier(SwordType.AXE_OF_MALICE), "malice", 24),
	HAMMER(new SwordModifier(SwordType.HAMMER), "hammer", 16),
	DAGGER(new SwordModifier(SwordType.DAGGER), "dagger", 16),
	TOMBMAKER(new SwordModifier(SwordType.TOMBMAKER), "tombmaker", 4),
	
	HOLY(new AleModifier(AleType.HOLY), "holy", 8),
	JJ(new AleModifier(AleType.JIMMYJUICE), "jj", 8),
	TRINKET(new AleModifier(AleType.TRINKET), "trinket", 8),
	REGROWTH(new AleModifier(AleType.REGROWTH), "regrowth", 8),
	
	AVENGE(new PassiveModifier(Passive.AVENGE), "avenge", 8),
	SAFEFALL(new PassiveModifier(Passive.SAFEFALL), "safefall", 4),
	DARKVISION(new PassiveModifier(Passive.DARKVISION), "darkvision", 4),
	
	ONE_SOS(new ConsumableModifier(ConsumableType.SOS, 1), "1sos", 4),
	TWO_SOS(new ConsumableModifier(ConsumableType.SOS, 2), "2sos", 8),
	;
	
	private final PropertyModifier modifier;
	private final String id;
	private final int cost;
	
	LoadoutItem(PropertyModifier modifier, String id, int cost) {
		this.modifier = modifier;
		this.id = id.toLowerCase();
		this.cost = cost;
	}
	
	void modify(DwarfProperties dwarfProperties) {
		modifier.modify(dwarfProperties);
	}
	
	public static LoadoutItem getItem(String id) {
		id = id.toLowerCase();
		for (LoadoutItem item : values()) {
			if (item.id.equals(id))
				return item;
		}
		throw new IllegalArgumentException("Unknown ID for loadout item: '" + id + "'.");
	}
	
	
	
	private static abstract class PropertyModifier {
		abstract void modify(DwarfProperties dwarfProperties);
	}
	
	
	private static class SwordModifier extends PropertyModifier {
		private final SwordType type;
		
		private SwordModifier(SwordType type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfProperties dwarfProperties) {
			dwarfProperties.setSwordType(type);
		}
	}
	private static class BowModifier extends PropertyModifier {
		private final BowType type;
		
		private BowModifier(BowType type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfProperties dwarfProperties) {
			dwarfProperties.setBowType(type);
		}
	}
	private static class AleModifier extends PropertyModifier {
		private final AleType type;
		
		private AleModifier(AleType type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfProperties dwarfProperties) {
			dwarfProperties.setAleType(type);
		}
	}
	private static class ArmourModifier extends PropertyModifier {
		private final ArmourType type;
		
		private ArmourModifier(ArmourType type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfProperties dwarfProperties) {
			dwarfProperties.setArmour(type);
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
		void modify(DwarfProperties dwarfProperties) {
			int amt = dwarfProperties.getConsumables().get(type);
			amt += quantity;
			dwarfProperties.getConsumables().put(type, amt);
		}
	}
	private static class PassiveModifier extends PropertyModifier {
		private final Passive type;
		
		private PassiveModifier(Passive type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfProperties dwarfProperties) {
			dwarfProperties.getPassives().add(type);
		}
	}
}
