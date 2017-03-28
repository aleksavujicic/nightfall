package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.Hat;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfData {
	private String title = null;
	private boolean forceTitle = false;
	private Hat hat = null;
	
	private Set<KitElementType> elements = new HashSet<>();
	private Map<ConsumableType, Integer> consumables = new HashMap<>();
	
	
	public String getTitle() {
		return title;
	}
	public boolean getForceTitle() { return forceTitle; }
	public Hat getHat() {
		return hat;
	}
	
	public Map<ConsumableType, Integer> getConsumables() {
		return consumables;
	}
	public Set<KitElementType> getElements() {
		return elements;
	}
	
	public DwarfData() {
		addDefaults();
	}
	
	public DwarfData(String title, boolean forceTitle, Hat hat, Set<KitElementType> elements, Map<ConsumableType, Integer> consumables) {
		this.title = title;
		this.forceTitle = forceTitle;
		this.hat = hat;
		this.elements = (elements == null ? new HashSet<>() : elements);
		this.consumables = (consumables == null ? new HashMap<>() : consumables);
		
		addDefaults();
	}
	
	private void addDefaults() {
		addElement(KitElementType.DWARF_AXE);
		addElement(KitElementType.DWARF_PICK);
		addElement(KitElementType.DWARF_SHOVEL);
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
	
	public void addElement(KitElementType type) {
		if (type == KitElementType.TOMBMAKER)
			elements.remove(KitElementType.DWARF_SHOVEL);
		
		elements.add(type);
	}
	
	public void incrementConsumable(ConsumableType consumable, int amt) {
		int current = consumables.computeIfAbsent(consumable, k -> 0);
		consumables.put(consumable, current + amt);
	}
	
	
	public static DwarfData getData(Player player) {
		return Loadout.getLoadout(player).constructProperties();
	}
	
}