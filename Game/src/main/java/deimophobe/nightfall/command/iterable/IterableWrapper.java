package deimophobe.nightfall.command.iterable;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Created by Deimophobe on 21/03/18.
 */
public abstract class IterableWrapper<T> implements Iterable<T> {
	private final Iterable<T> iterable;
	
	public IterableWrapper(Iterable<T> iterable) {
		this.iterable = iterable;
	}
	
	@NotNull
	@Override
	public Iterator<T> iterator() {
		return iterable.iterator();
	}
}
