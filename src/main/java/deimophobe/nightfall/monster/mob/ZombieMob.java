package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class ZombieMob extends AbstractMob {

	protected double rebirthChance;
	protected Map<String, Integer> upgrades;
	protected ZombieMob(MonsterPlayer mons) {
		this(mons, MobData.getMobData("zombie"));
	}

	protected ZombieMob(MonsterPlayer mons, MobData zombieData) {
		super(mons, MobType.ZOMBIE, zombieData);
		this.rebirthChance = 0;
		
		upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		int attack = upgrades.get("attack") + upgrades.get("attack-inf");
		int health = (upgrades.get("health") + upgrades.get("health-inf"))*2;
		getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, attack, "Attack Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		if (spawnMethod == SpawnMethod.REBIRTH) {
			giveSpawnProtection(30);
		}
	}

	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		
		Function<Integer, Double> chanceFunction = rebirthCount -> rebirthChance - 0.3*rebirthCount;
		monster.setRebirthSpot(monster.getLocation(), chanceFunction);
	}
	
	@Override
	protected void shrineProtectionDamage() {
		super.shrineProtectionDamage();
		monster.removeRebirth();
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(Zombie.class, zombie -> {
			zombie.setBaby(false);
		});
	}
}
