package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.loadout.LoadoutConstructable;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.Kit;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfData implements LoadoutConstructable {
	private final SortedSet<KitPieceType> pieces;
	private final SortedMap<ConsumableType, Integer> consumables;
	
	
	public DwarfData() {
		this(null, null);
	}
	
	public DwarfData(Set<KitPieceType> pieces, Map<ConsumableType, Integer> consumables) {
		this.pieces = (pieces != null ? new TreeSet<>(pieces) : new TreeSet<>());
		this.consumables = (consumables != null ? new TreeMap<>(consumables) : new TreeMap<>());
		
		addDefaults();
		tombmakerCheck();
	}
	
	
	private void addDefaults() {
		addPiece(KitPieceType.DWARF_AXE);
		addPiece(KitPieceType.DWARF_PICK);
		addPiece(KitPieceType.DWARF_SHOVEL);
	}
	
	private void tombmakerCheck() {
		if (pieces.contains(KitPieceType.TOMBMAKER))
			pieces.remove(KitPieceType.DWARF_SHOVEL);
	}
	
	@Override
	public void addPiece(String type) throws UnknownEnumElementException {
		addPiece(KitPieceType.fromString(type));
	}
	
	@Override
	public void incrementConsumable(String consumable, int amt) throws UnknownEnumElementException {
		incrementConsumable(ConsumableType.fromString(consumable), amt);
	}
	
	public void addPiece(KitPieceType type) {
		pieces.add(type);
		tombmakerCheck();
	}
	
	public void incrementConsumable(ConsumableType consumable, int amt) {
		int current = consumables.computeIfAbsent(consumable, k -> 0);
		consumables.put(consumable, current + amt);
	}
	
	public Kit createKitAndApplyToDwarf(Dwarf dwarf) {
		Kit kit = new Kit(dwarf, pieces);
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