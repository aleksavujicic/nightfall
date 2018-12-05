package deimophobe.nightfall.cooldown;

import com.google.common.base.Preconditions;
import deimophobe.nightfall.NightfallPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 3/12/18.
 *
 */
public interface Interactable {
	void interact();
	
	static Interactable wrapMethod(Method method, Object instance) throws IllegalArgumentException {
		checkNotNull(method, "Method must not be null");
		checkNotNull(instance, "Instance must not be null");
		
		// Check method has no arguments
		Type[] parameters = method.getParameterTypes();
		checkArgument(parameters.length == 0, "Method '%s' must have no arguments.", method.getName());
		
		// Check object can invoke method (i.e. check that it inherits from the methods declared class)
		Class<?> owner = method.getDeclaringClass();
		checkArgument(owner.isInstance(instance),
				"Method '%s', has declaring class '%s', but instance does not inherit from this class.",
				method.getName(),
				owner.getName()
		);
		
		// Ignore java access keywords
		method.setAccessible(true);
		
		return () -> {
			try {
				method.invoke(instance);
			} catch (IllegalAccessException e) {
				NightfallPlugin.logger().severe("Tried to invoke interactable method '" + method.getName() + "', but access was denied.");
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				NightfallPlugin.logger().severe("Failed to execute interactable method '" + method.getName() + "'.");
				e.printStackTrace();
			}
		};
	}
}
