package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.Hat;
import deimophobe.dvz.dwarf.kit.ArmourType;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfData {
	private String title = null;
	private boolean forceTitle = false;
	private Hat hat = null;
	
	private SwordType swordType = SwordType.DRB;
	private BowType bowType = BowType.SHORTBOW;
	private AleType aleType = AleType.HEALING;
	private ArmourType armour = null;
	
	private Map<ConsumableType, Integer> consumables = new HashMap<>();
	private Set<Passive> passives = new HashSet<>();
	
	
	public String getTitle() {
		return title;
	}
	public boolean forceTitle() { return forceTitle; }
	public Hat getHat() {
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
	public ArmourType getArmour() {
		return armour;
	}
	
	public Map<ConsumableType, Integer> getConsumables() {
		return consumables;
	}
	public Set<Passive> getPassives() {
		return passives;
	}
	
	public DwarfData() {}
	
	public DwarfData(String title, boolean forceTitle, Hat hat, SwordType swordType, BowType bowType, AleType aleType, ArmourType armour, Map<ConsumableType, Integer> consumables, Set<Passive> passives) {
		this.title = title;
		this.forceTitle = forceTitle;
		this.hat = hat;
		this.swordType = swordType;
		this.bowType = bowType;
		this.aleType = aleType;
		this.armour = armour;
		this.consumables = (consumables == null ? new HashMap<>() : consumables);
		this.passives = (passives == null ? new HashSet<>() : passives);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public void setForceTitle(boolean forceTitle) {
		this.forceTitle = forceTitle;
	}
	public void setHat(Hat hat) {
		this.hat = hat;
	}
	
	public void setSwordType(SwordType swordType) {
		this.swordType = swordType;
	}
	public void setBowType(BowType bowType) {
		this.bowType = bowType;
	}
	public void setAleType(AleType aleType) {
		this.aleType = aleType;
	}
	public void setArmour(ArmourType armour) {
		this.armour = armour;
	}
	
	public void addPassive(Passive passive) {
		passives.add(passive);
	}
	public void incrementConsumable(ConsumableType consumable, int amt) {
		int current = consumables.computeIfAbsent(consumable, k -> 0);
		consumables.put(consumable, current + amt);
	}
	
	public static DwarfData getData(Player player) {
		return Loadout.getLoadout(player).constructProperties();
	}
	
}