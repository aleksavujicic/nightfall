package deimophobe.nightfall.common.command;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Created by Deimophobe on 9/03/18.
 */
@FunctionalInterface
public interface MessageResolver<T> {
	BaseComponent getMessage(T t);
	
	default BaseComponent getUncheckedMessage(Object object) {
		return getMessage((T) object);
	}
}
