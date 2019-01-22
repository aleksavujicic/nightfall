package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.VampirismCooldown;
import deimophobe.nightfall.monster.upgrades.wrappers.FuryUpgrades;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

/**
 * Created by TKiwisi on 10/06/17.
 */
public class ZombieFury extends RebirthableMob<FuryUpgrades> {
	
	private final double arrowResistance;
	private final int armourShred;
	
	private final Runnable pursuitApplier;
	private boolean isLeaping;
	
	@Update
	private final VampirismCooldown vampirismCD;
	
	@Update @Display @Interact(click = ClickType.RIGHT)
	private final Cooldown leapCD;
	
	ZombieFury(MonsterPlayer mons) {
		super(mons, MobType.ZOMBIE_FURY, FuryUpgrades.class);
		
		FuryUpgrades upgrades = getUpgrades();
		
		this.armourShred = upgrades.getArmourShred();
		this.pursuitApplier = upgrades.getPursuitApplier();
		this.leapCD = upgrades.createLeapCooldown(this::onLeap);
		isLeaping = false;
		
		this.arrowResistance = upgrades.getArrowResistance();
		this.vampirismCD = upgrades.createVampirismCooldown();
	}
	
	@Override
	public void update() {
		super.update();
		if (isLeaping && monster.getPlayer().isOnGround()) {
			isLeaping = false;
			removeSpawnProtection();
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getType() != GameDamageType.MELEE) return;
		
		damage.addArmourShred(armourShred);
		
		vampirismCD.tryUse(damage);
		damage.addPostDamageHandler(pursuitApplier);
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowResistance().addBoost(arrowResistance);
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(Zombie.class, zombie -> {
			zombie.setBaby(false);
		});
	}
	
	private void onLeap() {
		isLeaping = true;
		giveSpawnProtection(50, false, false);
	}
}
