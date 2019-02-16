package deimophobe.nightfall.map.region;

/**
 * Created by Deimophobe on 21/01/17.
 */
public class NullRegion implements Region {
	@Override
	public boolean containsPosition(double x, double y, double z) {
		return false;
	}
}
