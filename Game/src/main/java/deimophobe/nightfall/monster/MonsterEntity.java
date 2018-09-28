package deimophobe.nightfall.monster;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.game.entity.GameEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 29/08/17.
 */
public interface MonsterEntity<E extends LivingEntity> extends GameEntity<E> {
	void onDamageAttack(DwarfDamage damage);
	void onDamageReceive(MonsterDamage damage);
	
	boolean isAI();
	
	boolean isBowInstaKillable();
	
	
	@Override
	default MonsterDamage createDamage(GameEntity attacker, GameDamageType type, double damage) {
		return createDamage(attacker, type, damage, null);
	}
	
	@Override
	MonsterDamage createDamage(GameEntity attacker, GameDamageType type, double damage, Projectile projectile);
}
