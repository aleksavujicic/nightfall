package deimophobe.nightfall.map.region;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 16/02/19.
 */
class OvalRegion implements Region {
	private final Position center;
	private final double xRadius;
	private final double yRadius;
	private final double zRadius;
	
	
	static OvalRegion fromParameterList(@NotNull List<Double> parameters) {
		checkArgument(parameters.size() == 6, "Number of parameters must be 6 (got %s)", parameters);
		
		double x = parameters.get(0);
		double y = parameters.get(1);
		double z = parameters.get(2);
		double xr = parameters.get(3);
		double yr = parameters.get(4);
		double zr = parameters.get(5);
		
		Position center = new Position(x,y,z);
		
		return new OvalRegion(center, xr, yr, zr);
	}
	
	public OvalRegion(Position center, double xRadius, double yRadius, double zRadius) {
		this.center = center;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		this.zRadius = zRadius;
	}
	
	@Override
	public boolean containsPosition(double x, double y, double z) {
		double xNorm = (x - center.getX())/xRadius;
		double yNorm = (y - center.getY())/yRadius;
		double zNorm = (z - center.getZ())/zRadius;
		
		return xNorm*xNorm + yNorm*yNorm + zNorm*zNorm <= 1;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
