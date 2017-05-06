package deimophobe.dvz;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class Damage<A extends GameEntity, R extends GameEntity> {
	private final DamageType type;
	private final A attacker;
	private final R receiver;
	
	private double damage;
	private boolean cancelled;
	private boolean force;
	
	public Damage(DamageType type, A attacker, R receiver, double damage) {
		this.type = type;
		this.attacker = attacker;
		this.receiver = receiver;
		
		this.damage = damage;
		this.cancelled = false;
	}
	
	public DamageType getType() {
		return type;
	}
	
	public A getAttacker() {
		return attacker;
	}
	
	public R getReceiver() {
		return receiver;
	}
	
	public void cancel() {
		cancelled = true;
	}
	
	public void multiplyDamage(double multiplier) {
		damage *= multiplier;
	}
	
	public void applyDamage() {
		if (cancelled) return;
		
		if (force)
			receiver.getEntity().setNoDamageTicks(0);
		
		receiver.getEntity().damage(damage);
	}
}
