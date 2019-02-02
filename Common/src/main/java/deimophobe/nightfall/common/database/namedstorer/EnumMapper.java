package deimophobe.nightfall.common.database.namedstorer;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Deimophobe on 31/01/19.
 */
public class EnumMapper<E extends Enum<E>> implements TypeBijection<E, String> {
	private final String enumName;
	private final E[] values;
	
	public EnumMapper(Class<E> enumClass) {
		this(enumClass, enumClass.getSimpleName());
	}
	
	public EnumMapper(Class<E> enumClass, String enumName) {
		this.enumName = enumName;
		try {
			values = (E[]) enumClass.getMethod("values").invoke(null);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String mapForward(E input) {
		return Misc.formatEnumElementName(input);
	}
	
	@Override
	public E mapBackward(String value) throws NoInverseException{
		try {
			return Misc.getEnumMemberFromString(value, values, enumName);
		} catch (UnknownEnumElementException e) {
			throw new NoInverseException(e);
		}
	}
}
