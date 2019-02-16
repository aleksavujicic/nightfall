package deimophobe.nightfall.map.region;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 16/02/19.
 */
public class BoxRegion implements Region {
	private final Position bottomCorner;
	private final Position topCorner;
	
	static BoxRegion fromParameterList(@NotNull List<Double> parameters) {
		checkArgument(parameters.size() == 6, "Number of parameters must be 6 (got %s)", parameters);
		
		double x1 = parameters.get(0);
		double y1 = parameters.get(1);
		double z1 = parameters.get(2);
		double x2 = parameters.get(3);
		double y2 = parameters.get(4);
		double z2 = parameters.get(5);
		
		Position bottom = new Position(
				Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2)
		);
		Position top = new Position(
				Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)
		);
		
		return new BoxRegion(bottom, top);
	}
	
	BoxRegion(Position bottomCorner, Position topCorner) {
		this.bottomCorner = bottomCorner;
		this.topCorner = topCorner;
	}
	
	@Override
	public boolean containsPosition(double x, double y, double z) {
		return bottomCorner.getX() <= x && x <= topCorner.getX()
				&& bottomCorner.getY() <= y && y <= topCorner.getY()
				&& bottomCorner.getZ() <= z && z <= topCorner.getZ();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BoxRegion boxRegion = (BoxRegion) o;
		return Objects.equals(bottomCorner, boxRegion.bottomCorner) &&
				Objects.equals(topCorner, boxRegion.topCorner);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(bottomCorner, topCorner);
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
