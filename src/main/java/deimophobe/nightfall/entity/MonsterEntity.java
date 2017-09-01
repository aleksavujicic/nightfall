package deimophobe.nightfall.entity;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import org.bukkit.entity.LivingEntity;

/**
 * Created by Deimophobe on 29/08/17.
 */
public interface MonsterEntity<E extends LivingEntity> extends GameEntity<E> {
	void onDamageAttack(DwarfDamage damage);
	void onDamageReceive(MonsterDamage damage);
}
