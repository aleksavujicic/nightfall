package deimophobe.nightfall.map.region;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 7/02/19.
 */
class Position {
	private final double x;
	private final double y;
	private final double z;
	
	static Position fromDoubleList(List<Double> list) {
		int size = list.size();
		checkArgument(size == 3, "List must have a size of 3 (got %s)", size);
		
		double x = list.get(0);
		double y = list.get(1);
		double z = list.get(2);
		
		return new Position(x, y, z);
	}
	
	Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public final double getX() {
		return x;
	}
	
	public final double getY() {
		return y;
	}
	
	public final double getZ() {
		return z;
	}
	
	public Location toLocation(World world) {
		return new Location(world, x, y, z);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position that = (Position) o;
		return Double.compare(that.x, x) == 0 &&
				Double.compare(that.y, y) == 0 &&
				Double.compare(that.z, z) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
