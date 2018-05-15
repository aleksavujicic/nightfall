package deimophobe.nightfall.common.database.data;

/**
 * Represents an object that can be created from and saved to a {@link Data} object.
 * No direct use at the moment but will be useful to keep things organised (and if it is ever directly used in the future).
 * Created by Deimophobe on 15/05/18.
 */
public interface Datable<T extends Data> {
	T toData();
}
