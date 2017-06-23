package deimophobe.dvz.monster.mob;

import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.Misc;
import deimophobe.dvz.blocks.BlockConverter;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Spiderling extends AbstractTypedMob {
	@Override protected MobType getType() {return MobType.SPIDERLING;}
	
	Spiderling(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		dwarf.givePotionEffect(PotionEffectType.POISON, 50, 4, true, false, true);
		return damage;
	}
	
	
	private static final int SPIDER_SPIT_CD_MAX = 5;
	private int spiderSpitCd = 0;
	
	private static final double CORRODE_DISTANCE = 15;
	
	@Override
	public void spawn() {
		super.spawn();
		givePermanentPotionEffect(PotionEffectType.JUMP, 3);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (spiderSpitCd > 0)
			spiderSpitCd--;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
			if (spiderSpitCd == 0) {
				spiderSpitCd = SPIDER_SPIT_CD_MAX;
				
				Location loc = monster.getLocation();
				World world = loc.getWorld();
				
				Entity snow = world.spawnEntity(loc.add(0,0.25,0), EntityType.SNOWBALL);
				((Snowball) snow).setShooter(monster.getPlayer());
				snow.setVelocity(loc.getDirection().add(new Vector(0,0.25,0)));
				world.playSound(loc, "entity.spider.step", 0.3f, 1);
			}
		}
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block block) {
		if (proj.getLocation().distance(monster.getLocation()) <= CORRODE_DISTANCE)
			BlockConverter.convert(BlockConverter.Type.CORROSION, block.getLocation().add(0.5, 0.5, 0.5), 2);
	}
}
