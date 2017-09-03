package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 13/04/17.
 */
public interface Mob {
	boolean isShrineImmune();
	Disguise getDisguise();
	
	void onSpawn();
	
	void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec);
	void onShift(boolean sneaking);
	void onBlockBreak(Block block);
	void onUse(Action action, Block clickedBlock, BlockFace blockFace);
	void onDamageAttack(DwarfDamage damage);
	void onDamageReceive(MonsterDamage<? extends Dwarf> damage);
	Projectile onBowFire(Arrow arrow, float force);
	void onProjectileLand(Projectile proj, Block hitBlock);
	float getCooldown();
	void onDeath();
	
	MobType getType();
}
