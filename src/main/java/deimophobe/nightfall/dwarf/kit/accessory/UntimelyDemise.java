package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class UntimelyDemise extends AbstractPiece {
	public UntimelyDemise(Dwarf dwarf) {
		super(dwarf);
		dwarf.forcePlague();
	}
}
