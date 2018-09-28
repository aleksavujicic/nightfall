package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.game.entity.GameEntity;
import org.bukkit.entity.LivingEntity;

/**
 * Created by Deimophobe on 29/08/17.
 */
public interface DwarfEntity<E extends LivingEntity> extends GameEntity<E> {
	void onDamageAttack(MonsterDamage damage);
	void onDamageReceive(DwarfDamage damage);
}
