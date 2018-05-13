package deimophobe.nightfall.util;

import com.google.common.collect.ForwardingSet;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class WeightedSet<T extends Weightable> extends ForwardingSet<T> {
	
	private final Set<T> delegate;
	
	public WeightedSet(Set<T> delegate) {
		this.delegate = delegate;
	}
	
	public WeightedSet() {
		this.delegate = new HashSet<>();
	}
	
	@SafeVarargs
	public WeightedSet(T... elements) {
		this.delegate = Stream.of(elements).collect(Collectors.toSet());
	}
	
	@Override
	protected Set<T> delegate() {
		return delegate;
	}
	
	public T getRandom() {
		if (size() == 0) throw new IllegalStateException("Cannot get random weighted element if size is zero.");
		
		double totalWeight = getCurrentTotalWeight();
		if (totalWeight == 0) throw new IllegalStateException("Cannot get random weighted element if total weight is zero.");
		
		double rand = Math.random() * totalWeight;
		for (T element : this) {
			rand -= element.getWeight();
			if (rand <= 0) return element;
		}
		
		// If nothing just get any old element
		return iterator().next();
	}
	
	public double getCurrentTotalWeight() {
		double weight = 0;
		
		for (T element : this) {
			weight += element.getWeight();
		}
		
		return weight;
	}
	
	public WeightedSet<T> filter(Predicate<T> filter) {
		WeightedSet<T> weightedSet = new WeightedSet<>();
		for (T element : this) {
			if (filter.test(element)) {
				weightedSet.add(element);
			}
		}
		return weightedSet;
	}
}
