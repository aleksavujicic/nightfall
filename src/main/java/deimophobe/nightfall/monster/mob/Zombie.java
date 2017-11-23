package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;

import java.util.Map;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class Zombie extends AbstractMob {

	protected double rebirthChance;
	protected Map<String, Integer> upgrades;

	protected final Location rebirthLoc;
	private boolean disabledRebirth = false;

	protected Zombie(MonsterPlayer mons) {
		this(mons, null);
	}

	public Zombie(MonsterPlayer mons, Location rebirth) {
		this(mons, rebirth, MobType.ZOMBIE.getMobData());
	}

	protected Zombie(MonsterPlayer mons, Location rebirth, MobData zombieData) {
		super(mons, MobType.ZOMBIE, zombieData);
		this.rebirthChance = 0;
		this.rebirthLoc = rebirth;
		
		upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		int attack = upgrades.get("attack") + upgrades.get("attack-inf");
		int health = (upgrades.get("health") + upgrades.get("health-inf"))*2;
		getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
	}
	
	protected boolean didRebirth() {
		return rebirthLoc != null;
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		if (didRebirth()) {
			giveSpawnProtection(30);
		}
	}
	
	@Override
	public void tpToSpawn() {
		if (didRebirth()) {
			monster.teleportTo(rebirthLoc);
		}

		else
			super.tpToSpawn();
	}
	
	public void disableRebirth() {
		disabledRebirth = true;
	}

	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		boolean setRebirth = (Math.random() < rebirthChance - (monster.getRebirthCount() * 0.3));
		if (setRebirth && !disabledRebirth) {
			monster.setRebirthSpot(monster.getLocation());
			monster.incrementRebirthCount();
		}
		else {
			monster.removeRebirth();
			monster.resetRebirthCount();
		}
	}
}
