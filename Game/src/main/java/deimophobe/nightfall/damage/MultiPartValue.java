package deimophobe.nightfall.damage;

import java.text.DecimalFormat;

/**
 * Created by Deimophobe on 16/09/17.
 */
public class MultiPartValue {
	private double base;
	private double boost;
	private double multiplier;
	private double postBoost;

	@Deprecated
	public void setBase(double base) {
		this.base = base;
	}
	
	public void addBoost(double amt) {
		boost += amt;
	}
	public void timesMult(double amt) {
		multiplier *= amt;
	}
	public void addPostBoost(double amt) {
		postBoost += amt;
	}
	
	public MultiPartValue(double base) {
		this.base = base;
		this.boost = 0;
		this.multiplier = 1;
	}
	
	public MultiPartValue(double base, double boost, double multiplier) {
		this.base = base;
		this.boost = boost;
		this.multiplier = multiplier;
	}
	
	public double getValue() {
		return (base + boost) * multiplier + postBoost;
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.####");
		
		return "Base: " + df.format(base)
				+ " Boost: " + df.format(boost)
				+ " Mult: " + df.format(multiplier)
				+ " Postboost: " + df.format(postBoost)
				+ " (Total: " + df.format(getValue()) + ")";
	}
}
