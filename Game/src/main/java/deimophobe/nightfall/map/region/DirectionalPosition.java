package deimophobe.nightfall.map.region;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 12/02/19.
 */
class DirectionalPosition extends Position {
	private final float yaw;
	private final float pitch;
	
	static DirectionalPosition fromDoubleList(List<Double> list) {
		int size = list.size();
		checkArgument(3 <= size && size <= 5, "List must have a size between 3 and 5 inclusive (got %s)", size);
		
		double x = list.get(0);
		double y = list.get(1);
		double z = list.get(2);
		
		float yaw = 0f;
		float pitch = 0f;
		
		switch (size) {
			case 3: break;
			case 4: {
				yaw = list.get(3).floatValue();
				break;
			}
			case 5: {
				yaw = list.get(3).floatValue();
				pitch = list.get(4).floatValue();
				break;
			}
		}
		
		return new DirectionalPosition(x, y, z, yaw, pitch);
	}
	
	DirectionalPosition(double x, double y, double z, float yaw, float pitch) {
		super(x, y, z);
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	@Override
	public Location toLocation(World world) {
		Location location = super.toLocation(world);
		location.setYaw(yaw);
		location.setPitch(pitch);
		return location;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		DirectionalPosition that = (DirectionalPosition) o;
		return Float.compare(that.yaw, yaw) == 0 &&
				Float.compare(that.pitch, pitch) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), yaw, pitch);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
