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
	private final Class<V> valueType;
	
	protected NamedStorable(@NotNull String key, @NotNull TypeBijection<V, S> resolver, @NotNull Class<V> valueType) {
		checkNotNull(key, "Key must not be null");
		checkNotNull(resolver, "Resolver must not be null");
		checkNotNull(valueType, "Value type must not be null");
		
		this.key = key;
		this.resolver = resolver;
		this.defaultValue = null;
		this.valueType = valueType;
	}
	
	protected NamedStorable(@NotNull String key, @NotNull TypeBijection<V, S> resolver, @NotNull V defaultValue) {
		checkNotNull(key, "Key must not be null");
		checkNotNull(resolver, "Resolver must not be null");
		checkNotNull(defaultValue, "Use other constructor for a null default value");
		
		this.key = key;
		this.resolver = resolver;
		this.defaultValue = defaultValue;
		this.valueType = (Class<V>) defaultValue.getClass();
	}
	
	@NotNull
	public String getKey() {
		return key;
	}
	
	public Class<V> getValueType() {
		return valueType;
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
