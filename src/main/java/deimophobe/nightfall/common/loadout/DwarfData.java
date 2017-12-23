package deimophobe.nightfall.common.loadout;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfData {
	private SortedSet<String> elements = new TreeSet<>();
	private SortedMap<String, Integer> consumables = new TreeMap<>();
	
	
	public Map<String, Integer> getConsumables() {
		return consumables;
	}
	public Set<String> getElements() {
		return elements;
	}
	
	public DwarfData() {
		addDefaults();
	}
	
	public DwarfData(Set<String> elements, Map<String, Integer> consumables) {
		if (elements != null)
			this.elements = new TreeSet<>(elements);
		if (consumables != null)
			this.consumables = new TreeMap<>(consumables);
		
		addDefaults();
		
		if (this.elements.contains(KitElementName.TOMBMAKER))
			this.elements.remove(KitElementName.DWARF_SHOVEL);
	}
	
	private void addDefaults() {
		addElement(KitElementName.DWARF_AXE);
		addElement(KitElementName.DWARF_PICKAXE);
		addElement(KitElementName.DWARF_SHOVEL);
	}
	
	public void addElement(String type) {
		elements.add(type);
		
		if (Objects.equals(type, KitElementName.TOMBMAKER))
			elements.remove(KitElementName.DWARF_SHOVEL);
	}
	
	public void addConsumables(Map<String, Integer> consumables) {
		for (String type : consumables.keySet()) {
			incrementConsumable(type, consumables.get(type));
		}
	}
	
	public void incrementConsumable(String consumable, int amt) {
		int current = consumables.computeIfAbsent(consumable, k -> 0);
		consumables.put(consumable, current + amt);
	}
	
	public static DwarfData getData(Player player) {
		return LoadoutManager.getManager().getLoadout(player).constructProperties();
	}
}