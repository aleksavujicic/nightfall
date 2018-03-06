package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 13/04/17.
 */
public interface Mob {
	
	MobType getType();
	
	String getDeathMessageName();
	int getCharmTime();
	double getShrineWeight();
	Disguise getDisguise();
	default boolean hasDisguise() {return getDisguise() != null;}
	
	void onSpawn();
	
	void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec);
	void onShift(boolean sneaking);
	boolean onBlockBreak(Block block, boolean didBreak);
	void onUse(Action action, Block clickedBlock, BlockFace blockFace);
	void onDamageAttack(DwarfDamage damage);
	void onDamageReceive(MonsterDamage damage);
	Projectile onBowFire(Arrow arrow, float force);
	void onProjectileLand(Projectile proj, Block hitBlock, Entity hitEntity);
	float getCooldown();
	void onDeath(boolean silent);
}
