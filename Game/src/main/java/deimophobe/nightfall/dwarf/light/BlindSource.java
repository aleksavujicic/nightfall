package deimophobe.nightfall.dwarf.light;

import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.dwarf.Dwarf;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 8/12/18.
 */
public class BlindSource extends LifetimeExpireable implements LightSource {
	public BlindSource(int lifetime) {
		super(lifetime);
		checkArgument(lifetime >= 0, "Blindness duration must be positive (got %s)", lifetime);
	}
	
	@Override
	public void apply(Dwarf dwarf, Vision vision) {
		vision.forceBlind(getLifetime());
	}
}
