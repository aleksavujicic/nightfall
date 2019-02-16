package deimophobe.nightfall.map.region;

/**
 * Created by Deimophobe on 15/02/19.
 */
@FunctionalInterface
interface Conditional {
	boolean evaluate(double left, double right);
}
