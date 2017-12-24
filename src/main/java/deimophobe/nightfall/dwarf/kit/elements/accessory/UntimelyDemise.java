package deimophobe.nightfall.dwarf.kit.elements.accessory;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class UntimelyDemise extends AbstractElement {
	public UntimelyDemise(Dwarf dwarf) {
		super(dwarf);
		dwarf.forcePlague();
	}
}
