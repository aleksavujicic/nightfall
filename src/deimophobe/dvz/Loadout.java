package deimophobe.dvz;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.ArmourType;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.Ale;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.Bow;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
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
	
	private static final Map<Player, Loadout> loadouts = new HashMap<>();
	public static void setLoadout(Player player, Loadout loadout) {
		loadouts.put(player, loadout);
	}
	public static Loadout getLoadout(Player player) {
		Loadout loadout = loadouts.get(player);
		if (loadout != null) return loadout;
		
		Map<ConsumableType, Integer> consumables = new HashMap<>();
		consumables.put(ConsumableType.LAMP, 5);
		consumables.put(ConsumableType.SOS, 5);
		consumables.put(ConsumableType.WRENCH, 5);
		consumables.put(ConsumableType.MORTAR, 5);
		consumables.put(ConsumableType.WIZARD_MORTAR, 5);
		consumables.put(ConsumableType.HEAL_STATION, 5);
		consumables.put(ConsumableType.ARMOUR_ITEM, 5);
		
		Set<Passive> passives = new HashSet<>();
		passives.add(Passive.AVENGE);
		passives.add(Passive.QUICKFEET);
		passives.add(Passive.DARKVISION);
		passives.add(Passive.SAFEFALL);
		passives.add(Passive.NAMETHISSOMETHINGBETTERDEIMO);
		
		Loadout defaultLoadout = new Loadout("Ranger", false, null, SwordType.GRB, BowType.DRAGONSKIN, AleType.JIMMYJUICE, consumables, ArmourType.RUNEBLESSED, passives);
		loadouts.put(player, defaultLoadout);
		return defaultLoadout;
	}
}