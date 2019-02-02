package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.VampirismCooldown;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 2/02/17.
 */
class ZombieBasic extends AbstractMob {
	
	@Update
	private final VampirismCooldown vampirismCD;
	
	@Update @Display
	@Interact(click = ClickType.RIGHT)
	private final Cooldown leapCD = new UseCooldown(25*20, this::leap);


	ZombieBasic(MonsterPlayer mons) {
		super(mons, MobType.ZOMBIE_BASE);
		
		vampirismCD = new VampirismCooldown(12, monster, 0, 1);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getType() != GameDamageType.MELEE) return;
		
		vampirismCD.tryUse(damage);
		damage.addPostDamageHandler(() -> {
			monster.givePotionEffect(PotionEffectType.SPEED, 100, 1, true, false, true);
		});
	}
	
	private void leap() {
		monster.leap(1, 0.5);
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(Zombie.class, zombie -> {
			zombie.setBaby(false);
		});
	}
}
