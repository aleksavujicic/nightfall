package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;

import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class Loadout {
	private final String title;
	private final boolean forceTitle;
	private final Object hat;
	
	private final SwordType swordType;
	private final BowType bowType;
	private final AleType aleType;
	private final Map<ConsumableType, Integer> consumables;
	
	private final ArmourType armour;
	private final Set<Passive> passives;
	
	
	public String getTitle() {
		return title;
	}
	public boolean forceTitle() { return forceTitle; }
	public Object getHat() {
		return hat;
	}
	
	public SwordType getSwordType() {
		return swordType;
	}
	public BowType getBowType() {
		return bowType;
	}
	public AleType getAleType() {
		return aleType;
	}
	public Map<ConsumableType, Integer> getConsumables() {
		return consumables;
	}
	
	public ArmourType getArmour() {
		return armour;
	}
	public Set<Passive> getPassives() {
		return passives;
	}
	
	public Loadout(String title, boolean forceTitle, Object hat, SwordType swordType, BowType bowType, AleType aleType, Map<ConsumableType, Integer> consumables, ArmourType armour, Set<Passive> passives) {
		this.title = title;
		this.forceTitle = forceTitle;
		this.hat = hat;
		this.swordType = swordType;
		this.bowType = bowType;
		this.aleType = aleType;
		this.consumables = consumables;
		this.armour = armour;
		this.passives = passives;
	}
}
