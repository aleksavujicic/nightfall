package deimophobe.nightfall.common.database.namedstorer;

/**
 * Created by Deimophobe on 1/02/19.
 */
public class SimpleBijection<T> implements TypeBijection<T, T> {
	@Override
	public T mapForward(T input) {
		return input;
	}
	
	@Override
	public T mapBackward(T output) {
		return output;
	}
}
