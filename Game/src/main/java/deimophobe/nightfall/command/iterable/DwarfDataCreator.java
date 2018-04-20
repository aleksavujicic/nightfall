package deimophobe.nightfall.command.iterable;

import deimophobe.nightfall.dwarf.DwarfData;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 22/03/18.
 */
public interface DwarfDataCreator {
	DwarfData createDwarfData(Player player);
}
