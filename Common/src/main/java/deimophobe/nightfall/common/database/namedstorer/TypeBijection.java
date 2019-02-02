package deimophobe.nightfall.common.database.namedstorer;

/**
 * Created by Deimophobe on 31/01/19.
 */
public interface TypeBijection<T, U> {
	U mapForward(T input);
	T mapBackward(U output) throws NoInverseException;
	
	TypeBijection<Boolean, Boolean> BOOLEAN = new SimpleBijection<>();
	TypeBijection<String, String> STRING = new SimpleBijection<>();
}
