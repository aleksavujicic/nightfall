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
enum LoadoutItem {
	GRB(new SwordChanger(SwordType.GRB), "grb"),
	MALICE(new SwordChanger(SwordType.AXE_OF_MALICE), "malice"),
	HAMMER(new SwordChanger(SwordType.HAMMER), "hammer"),
	DAGGER(new SwordChanger(SwordType.DAGGER), "dagger"),
	TOMBMAKER(new SwordChanger(SwordType.TOMBMAKER), "tombmaker"),
	
	AVENGE(new PassiveChanger(Passive.AVENGE), "avenge"),
	SAFEFALL(new PassiveChanger(Passive.SAFEFALL), "safefall"),
	DARKVISION(new PassiveChanger(Passive.DARKVISION), "darkvision"),
	
	ONE_SOS(new ConsumableChanger(ConsumableType.SOS, 1), "1sos"),
	TWO_SOS(new ConsumableChanger(ConsumableType.SOS, 2), "2sos")
	;
	
	private final LoadoutChanger changer;
	private final String id;
	
	LoadoutItem(LoadoutChanger changer, String id) {
		this.changer = changer;
		this.id = id.toLowerCase();
	}
	
	void addToLoadout(Loadout loadout) {
		changer.addToLoadout(loadout);
	}
	
	static LoadoutItem getItemFromID(String id) {
		id = id.toLowerCase();
		for (LoadoutItem item : values()) {
			if (item.id.equals(id))
				return item;
		}
		throw new IllegalArgumentException("Unknown ID for loadout item: '" + id + "'.");
	}
	
	
	private static abstract class LoadoutChanger {
		abstract void addToLoadout(Loadout loadout);
	}
	
	
	private static class SwordChanger extends LoadoutChanger {
		private final SwordType type;
		
		private SwordChanger(SwordType type) {
			this.type = type;
		}
		
		@Override
		void addToLoadout(Loadout loadout) {
			loadout.setSwordType(type);
		}
	}
	private static class BowChanger extends LoadoutChanger {
		private final BowType type;
		
		private BowChanger(BowType type) {
			this.type = type;
		}
		
		@Override
		void addToLoadout(Loadout loadout) {
			loadout.setBowType(type);
		}
	}
	private static class AleChanger extends LoadoutChanger {
		private final AleType type;
		
		private AleChanger(AleType type) {
			this.type = type;
		}
		
		@Override
		void addToLoadout(Loadout loadout) {
			loadout.setAleType(type);
		}
	}
	private static class ArmourChanger extends LoadoutChanger {
		private final ArmourType type;
		
		private ArmourChanger(ArmourType type) {
			this.type = type;
		}
		
		@Override
		void addToLoadout(Loadout loadout) {
			loadout.setArmour(type);
		}
	}
	
	private static class ConsumableChanger extends LoadoutChanger {
		private final ConsumableType type;
		private final int quantity;
		
		private ConsumableChanger(ConsumableType type, int quantity) {
			this.type = type;
			this.quantity = quantity;
		}
		
		@Override
		void addToLoadout(Loadout loadout) {
			int amt = loadout.getConsumables().get(type);
			amt += quantity;
			loadout.getConsumables().put(type, amt);
		}
	}
	private static class PassiveChanger extends LoadoutChanger {
		private final Passive type;
		
		private PassiveChanger(Passive type) {
			this.type = type;
		}
		
		@Override
		void addToLoadout(Loadout loadout) {
			loadout.getPassives().add(type);
		}
	}
}
