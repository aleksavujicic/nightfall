package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.loadout.LoadoutConstructable;
import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.Kit;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfData implements LoadoutConstructable {
	private final Set<KitPieceType> pieces;
	private final Map<ConsumableType, Integer> consumables;
	
	
	public DwarfData() {
		this(null, null);
	}
	
	public DwarfData(Set<KitPieceType> pieces, Map<ConsumableType, Integer> consumables) {
		this.pieces = (pieces != null
				? EnumSet.copyOf(pieces)
				: EnumSet.noneOf(KitPieceType.class)
		);
		this.consumables = (consumables != null
				? new EnumMap<>(consumables)
				: new EnumMap<>(ConsumableType.class)
		);
		
		addDefaults();
		duplicateCheck();
	}
	
	
	private void addDefaults() {
		addPiece(KitPieceType.DWARF_AXE);
		addPiece(KitPieceType.DWARF_PICK);
		addPiece(KitPieceType.DWARF_SHOVEL);
	}
	
	private void duplicateCheck() {
		if (pieces.contains(KitPieceType.TOMBMAKER)) {
			pieces.remove(KitPieceType.DWARF_SHOVEL);
		}
		if (pieces.contains(KitPieceType.SPEEDY_BRICKLAYER)) {
			pieces.remove(KitPieceType.BRICKLAYER);
		}
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
		duplicateCheck();
	}
	
	public void incrementConsumable(ConsumableType consumable, int amt) {
		int current = consumables.computeIfAbsent(consumable, k -> 0);
		consumables.put(consumable, current + amt);
	}
	
	public Kit createKitAndApplyToDwarf(Dwarf dwarf) {
		Kit kit = new Kit(dwarf, pieces);
		kit.giveItems(PickupType.START);
		
		// Add consumables
		for (ConsumableType type : consumables.keySet()) {
			dwarf.giveConsumable(type, consumables.get(type));
		}
		
		return kit;
	}
	
	public static DwarfData getData(Player player) {
		DwarfData dd = new DwarfData();
		Loadout loadout = PlayerManager.getManager().getLoadout(player);
		loadout.modifyLoadoutConstruct(dd);
		return dd;
	}
}