package deimophobe.nightfall.damage;

/**
 * Created by Deimophobe on 16/09/17.
 */
public class MultiPartValue {
	private double base;
	private double boost;
	private double multiplier;

	@Deprecated
	public void setBase(double base) {
		this.base = base;
	}

	@Deprecated
	public void setBoost(double boost) {
		this.boost = boost;
	}

	@Deprecated
	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}
	
	public void addBoost(double amt) {
		boost += amt;
	}
	public void timesMult(double amt) {
		multiplier *= amt;
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
		return (base + boost) * multiplier;
	}
	
	@Override
	public String toString() {
		return "Base: " + base + " Boost: " + boost + " Mult: " + multiplier ;
	}
}
