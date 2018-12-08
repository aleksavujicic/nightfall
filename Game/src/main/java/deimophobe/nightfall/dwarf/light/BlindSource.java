package deimophobe.nightfall.dwarf.light;

import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 8/12/18.
 */
public class BlindSource extends LifetimeExpireable implements LightSource {
	public BlindSource(int lifetime) {
		super(lifetime);
	}
	
	@Override
	public void apply(Dwarf dwarf, Vision vision) {
		vision.forceBlind(getLifetime());
	}
}
