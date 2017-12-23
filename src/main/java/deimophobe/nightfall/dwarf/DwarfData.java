package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.loadout.LoadoutConstruct;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.Kit;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfData implements LoadoutConstruct {
	private SortedSet<KitElementType> elements = new TreeSet<>();
	private SortedMap<ConsumableType, Integer> consumables = new TreeMap<>();
	
	public Set<KitElementType> getElements() {
		return elements;
	}
	public Map<ConsumableType, Integer> getConsumables() {
		return consumables;
	}
	
	
	public DwarfData() {
		addDefaults();
	}
	
	public DwarfData(Set<KitElementType> elements, Map<ConsumableType, Integer> consumables) {
		if (elements != null)
			this.elements = new TreeSet<>(elements);
		if (consumables != null)
			this.consumables = new TreeMap<>(consumables);
		
		addDefaults();
		tombmakerCheck();
	}
	
	
	private void addDefaults() {
		addElement(KitElementType.DWARF_AXE);
		addElement(KitElementType.DWARF_PICK);
		addElement(KitElementType.DWARF_SHOVEL);
	}
	
	private void tombmakerCheck() {
		if (elements.contains(KitElementType.TOMBMAKER))
			elements.remove(KitElementType.DWARF_SHOVEL);
	}
	
	
	public void addElement(String type) {
		try {
			addElement(KitElementType.fromString(type));
		} catch (UnknownEnumElementException e) {
			Bukkit.getLogger().severe("Unknown KitElementType: " + type);
			e.printStackTrace();
		}
	}
	
	public void incrementConsumable(String consumable, int amt) {
		try {
			incrementConsumable(ConsumableType.fromString(consumable), amt);
		} catch (UnknownEnumElementException e) {
			Bukkit.getLogger().severe("Unknown ConsumableType: " + consumable);
			e.printStackTrace();
		}
	}
	
	public void addElement(KitElementType type) {
		elements.add(type);
		tombmakerCheck();
	}
	
	public void incrementConsumable(ConsumableType consumable, int amt) {
		int current = consumables.computeIfAbsent(consumable, k -> 0);
		consumables.put(consumable, current + amt);
	}
	
	public Kit createKitAndApplyToDwarf(Dwarf dwarf) {
		return new Kit(dwarf, this);
	}
	
	public static DwarfData getData(Player player) {
		DwarfData dd = new DwarfData();
		LoadoutManager.getManager().getLoadout(player).modifyLoadoutConstruct(dd);
		return dd;
	}
}