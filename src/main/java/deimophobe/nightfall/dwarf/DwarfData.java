package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.loadout.LoadoutConstructable;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.Kit;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfData implements LoadoutConstructable {
	private final SortedSet<KitPieceType> elements;
	private final SortedMap<ConsumableType, Integer> consumables;
	
	
	public DwarfData() {
		this(null, null);
	}
	
	public DwarfData(Set<KitPieceType> elements, Map<ConsumableType, Integer> consumables) {
		this.elements = (elements != null ? new TreeSet<>(elements) : new TreeSet<>());
		this.consumables = (consumables != null ? new TreeMap<>(consumables) : new TreeMap<>());
		
		addDefaults();
		tombmakerCheck();
	}
	
	
	private void addDefaults() {
		addElement(KitPieceType.DWARF_AXE);
		addElement(KitPieceType.DWARF_PICK);
		addElement(KitPieceType.DWARF_SHOVEL);
	}
	
	private void tombmakerCheck() {
		if (elements.contains(KitPieceType.TOMBMAKER))
			elements.remove(KitPieceType.DWARF_SHOVEL);
	}
	
	@Override
	public void addElement(String type) {
		try {
			addElement(KitPieceType.fromString(type));
		} catch (UnknownEnumElementException e) {
			Bukkit.getLogger().severe("Unknown KitPieceType: " + type);
			e.printStackTrace();
		}
	}
	
	@Override
	public void incrementConsumable(String consumable, int amt) {
		try {
			incrementConsumable(ConsumableType.fromString(consumable), amt);
		} catch (UnknownEnumElementException e) {
			Bukkit.getLogger().severe("Unknown ConsumableType: " + consumable);
			e.printStackTrace();
		}
	}
	
	public void addElement(KitPieceType type) {
		elements.add(type);
		tombmakerCheck();
	}
	
	public void incrementConsumable(ConsumableType consumable, int amt) {
		int current = consumables.computeIfAbsent(consumable, k -> 0);
		consumables.put(consumable, current + amt);
	}
	
	public Kit createKitAndApplyToDwarf(Dwarf dwarf) {
		Kit kit = new Kit(dwarf, elements);
		kit.giveItems(KitGiveType.START);
		
		// Add consumables
		for (ConsumableType type : consumables.keySet()) {
			dwarf.giveConsumable(type, consumables.get(type));
		}
		
		return kit;
	}
	
	public static DwarfData getData(Player player) {
		DwarfData dd = new DwarfData();
		LoadoutManager.getManager().getLoadout(player).modifyLoadoutConstruct(dd);
		return dd;
	}
}