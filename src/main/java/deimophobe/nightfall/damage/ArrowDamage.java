package deimophobe.nightfall.damage;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.GameEntity;
import org.bukkit.entity.Arrow;

/**
 * Created by Deimophobe on 17/07/17.
 */
public class ArrowDamage<A extends GameEntity, R extends GameEntity> extends Damage<A, R> {
	private final Arrow arrow;
	public Arrow getArrow() { return arrow; }
	
	public ArrowDamage(DamageType type, A attacker, R receiver, double damage, Arrow arrow) {
		this(type, attacker, receiver, damage, false, arrow);
	}
	
	public ArrowDamage(DamageType type, A attacker, R receiver, double damage, boolean force, Arrow arrow) {
		super(type, attacker, receiver, damage, force);
		this.arrow = arrow;
	}
}
