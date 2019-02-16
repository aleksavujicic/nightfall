package deimophobe.nightfall.map.region;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 21/01/17.
 */
class SphericalRegion implements Region {
	private final Position center;
	private final double radiusSquared;
	
	static SphericalRegion fromParameterList(@NotNull List<Double> parameters) {
		checkArgument(parameters.size() == 4, "Number of parameters must be 4 (got %s)", parameters);
		
		double x = parameters.get(0);
		double y = parameters.get(1);
		double z = parameters.get(2);
		double radius = parameters.get(3);
		
		Position position = new Position(x, y, z);
		return new SphericalRegion(position, radius);
	}
	
	SphericalRegion(Position center, double radius) {
		this.center = center;
		this.radiusSquared = radius*radius;
	}
	
	SphericalRegion(double x, double y, double z, double radius) {
		this.center = new Position(x, y, z);
		this.radiusSquared = radius*radius;
	}
	
	@Override
	public boolean containsPosition(double x, double y, double z) {
		double dx = x - center.getX();
		double dy = y - center.getY();
		double dz = z - center.getZ();
		
		return dx*dx + dy*dy + dz*dz <= radiusSquared;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SphericalRegion that = (SphericalRegion) o;
		return Double.compare(that.radiusSquared, radiusSquared) == 0 &&
				Objects.equals(center, that.center);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(center, radiusSquared);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
