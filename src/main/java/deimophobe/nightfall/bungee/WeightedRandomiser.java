package deimophobe.nightfall.bungee;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class WeightedRandomiser<T> {
	private final Set<WeightedElement> elements = new HashSet<>();
	private double totalWeight = 0;
	
	public WeightedRandomiser(WeightedElement... elements) {
		for (WeightedElement element : elements) {
			addWeightedElement(element);
		}
	}
	
	public void addWeightedElement(WeightedElement element) {
		elements.add(element);
		totalWeight += element.weight;
	}
	
	public void addWeightedElement(T element, double weight) {
		addWeightedElement(new WeightedElement(element, weight));
	}
	
	public T getRandom() {
		if (elements.size() == 0) throw new IllegalStateException("Cannot get random weighted element if elements size is zero.");
		
		double rand = Math.random() * totalWeight;
		for (WeightedElement element : elements) {
			rand -= element.weight;
			
			if (rand <= 0) return element.element;
		}
		
		return elements.iterator().next().element;
	}
	
	public class WeightedElement {
		private final T element;
		private final double weight;
		
		public WeightedElement(T element, double weight) {
			this.element = element;
			this.weight = weight;
		}
	}
}
