package deimophobe.nightfall.map.region;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 18/03/17.
 */
class CylindricalRegion implements Region {
	private final double x;
	private final double z;
	private final double radiusSquared;
	
	static CylindricalRegion fromParameterList(@NotNull List<Double> parameters) {
		checkArgument(parameters.size() == 3, "Number of parameters must be 3 (got %s)", parameters);
		
		double x = parameters.get(0);
		double z = parameters.get(1);
		double radius = parameters.get(2);
		
		return new CylindricalRegion(x, z, radius);
	}
	
	CylindricalRegion(double x, double z, double radius) {
		this.x = x;
		this.z = z;
		this.radiusSquared = radius*radius;
	}
	
	@Override
	public boolean containsPosition(double x, double y, double z) {
		double dx = this.x - x;
		double dz = this.z - z;
		
		return (dx*dx + dz*dz <= radiusSquared);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CylindricalRegion that = (CylindricalRegion) o;
		return Double.compare(that.x, x) == 0 &&
				Double.compare(that.z, z) == 0 &&
				Double.compare(that.radiusSquared, radiusSquared) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, z, radiusSquared);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
