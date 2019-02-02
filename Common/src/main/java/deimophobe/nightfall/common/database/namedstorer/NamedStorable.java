package deimophobe.nightfall.common.database.namedstorer;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 31/01/19.
 */
public abstract class NamedStorable<V, S> {
	@NotNull
	private final String key;
	@NotNull
	private final TypeBijection<V, S> resolver;
	
	private final V defaultValue;
	
	protected NamedStorable(@NotNull String key, @NotNull TypeBijection<V, S> resolver, V defaultValue) {
		checkNotNull(key, "Key must not be null");
		checkNotNull(resolver, "Resolver must not be null");
		this.key = key;
		this.resolver = resolver;
		this.defaultValue = defaultValue;
	}
	
	@NotNull
	public String getKey() {
		return key;
	}
	
	public V getDefault() {
		return defaultValue;
	}
	
	public S formatValueForStoring(V value) {
		return resolver.mapForward(value);
	}
	
	public V retrieveValueFromStorage(S stored) {
		try {
			return resolver.mapBackward(stored);
		} catch (NoInverseException e) {
			NightfallCommonPlugin.logger().warning("Failed to unstore " + stored + " using storer " + this);
			e.printStackTrace();
			
			return defaultValue;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj.getClass() != this.getClass()) return false;
		
		NamedStorable namedStorable = (NamedStorable) obj;
		return key.equals(namedStorable.key);
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
