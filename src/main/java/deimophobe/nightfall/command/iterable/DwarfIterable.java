package deimophobe.nightfall.command.iterable;

import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 21/03/18.
 */
public class DwarfIterable extends IterableWrapper<Dwarf> {
	public DwarfIterable(Iterable<Dwarf> iterable) {
		super(iterable);
	}
}
