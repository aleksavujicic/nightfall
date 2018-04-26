package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.entity.LivingEntity;

/**
 * Created by Deimophobe on 8/07/17.
 */
final class Hellhound extends AbstractWolf {
	
	Hellhound(MonsterPlayer monster) {
		super(monster, MobType.HELLHOUND);
		getWeapon().addModifier(ItemModifierType.BURNING, 2);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		changeDisguiseWatcher(WolfWatcher.class, (ww) -> ww.setAngry(true));
	}
	
	@Override
	protected float leapPitch() {
		return 0.85f;
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(org.bukkit.entity.Wolf.class, wolf -> {
			wolf.setSitting(monster.isSneaking());
			wolf.setTamed(false);
			wolf.setAngry(true);
			wolf.setTarget(monster.getPlayer());
		});
	}
}
