package deimophobe.nightfall.damage;

import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class MonsterDamage<A extends Dwarf, R extends GameEntity> extends GameDamage<A,R> {
	
	private boolean proc;
	
	public MonsterDamage(EntityDamageEvent event, GameDamageType type, A attacker, R receiver, double damage, boolean force, Projectile arrow) {
		super(event, type, attacker, receiver, damage, force, arrow);
	}
}
