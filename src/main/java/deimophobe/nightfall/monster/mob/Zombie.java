package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class Zombie extends AbstractMob {

	protected double rebirthChance;
	protected Map<String, Integer> upgrades;

	protected final Location rebirthLoc;
	
	protected Zombie(MonsterPlayer mons) {
		this(mons, null);
	}

	public Zombie(MonsterPlayer mons, Location rebirth) {
		this(mons, rebirth, MobType.ZOMBIE);
	}

	protected Zombie(MonsterPlayer mons, Location rebirth, MobType zombieType) {
		super(mons, zombieType);
		this.rebirthChance = 0;
		this.rebirthLoc = rebirth;
		
		upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		int attack = upgrades.get("attack") + upgrades.get("attack-inf");
		int health = (upgrades.get("health") + upgrades.get("health-inf"))*2;
		getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
	}
	
	private boolean didRebirth() {
		return rebirthLoc != null;
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		if (didRebirth())
			giveSpawnProtection(12);
	}
	
	@Override
	public void tpToSpawn() {
		if (didRebirth())
			monster.teleportTo(rebirthLoc);
		else
			super.tpToSpawn();
	}

	@Override
	public void onDeath() {
		boolean setRebirth = (Math.random() < rebirthChance - (monster.getRebirthCount() * 0.3));
		if (setRebirth) {
			monster.setRebirthSpot(monster.getLocation());
			monster.incrementRebirthCount();
		}
		else {
			monster.removeRebirth();
			monster.resetRebirthCount();
		}
	}

}
