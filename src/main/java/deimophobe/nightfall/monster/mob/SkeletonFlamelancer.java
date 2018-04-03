package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.ai.AIType;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 20/01/17.
 */
class SkeletonFlamelancer extends Skeleton {

	private int flame;
	private int volley;
	private int speed;
	private int arrowRes;
	private int firePath;
	private int fireAI;
	private double realArrowRes = 0;

	private static final int ARROWS_FIRED = 20;
	private double flameBlock;
	private double chargeBonus = 0;

	private static Integer[] arrowResValues = {0, 10, 20, 30, 40, 50};

	SkeletonFlamelancer(MonsterPlayer monster) {
		super(monster, MobData.getMobData("skeleton.flamelancer"));
		this.flame = upgrades.get("flame");
		this.volley = upgrades.get("volley");
		this.speed = upgrades.get("speed");
		this.arrowRes = arrowResValues[upgrades.get("arrowres-flamelancer")];
		this.firePath = upgrades.get("firepath");
		this.fireAI = upgrades.get("fireai");
		realArrowRes = arrowRes * 0.01;

		flameBlock = 0.05 + flame * 0.02;

		getArmour().addModifier(ItemModifierType.SPEED, 10, "Flamelancer");
		getArmour().addModifier(ItemModifierType.SPEED, speed * 10, "Upgrade");
		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		changeDisguiseWatcher(flagWatcher -> flagWatcher.setBurning(true));
	}

	@Override
	public void update() {
		super.update();
        Block block = monster.getLocation().getBlock();
        if (everyNthTick(10)) {
			if (BlockType.IGNORABLE.matchesBlock(block) && Math.random() < firePath * 0.1) {
				block.setType(Material.FIRE);
			} else {
				monster.getPlayer().setFireTicks(0);
			}
			chargeBonus = Math.min(chargeBonus+0.01, 0.2);
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		super.onBowFire(arrow, force);
		
		arrow.setFireTicks(10000);
		arrow.setCritical(false);
		final int arrowsToFire = (int) (ARROWS_FIRED*(force*force));
		if (Math.random() < (volley * 0.06 + 0.1 + chargeBonus)) {
			for (int i=0; i<arrowsToFire; i++) {
				Arrow newArrow = ArrowMisc.summonArrow(monster, getPower(), force*2, force, 30f);
				newArrow.setCritical(false);
				newArrow.setFireTicks(10000);
			}
			chargeBonus = 0;
		}
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock, Entity hitEntity) {
		if (hitBlock == null) return;
		
		BlockFace face = Misc.getBlockFaceProjectileHit(proj, hitBlock);
		Block block = hitBlock.getRelative(face);
		
		if (Math.random() < flameBlock) {
			if (BlockType.IGNORABLE.matchesBlock(hitBlock)) {
				hitBlock.setType(Material.FIRE);
				if (fireAI == 1 && Math.random() < 0.5) {
					AIManager.getManager().spawnAI(AIType.FIRE_SKELLY, hitBlock.getLocation());
				}
			} else if (BlockType.IGNORABLE.matchesBlock(block)) {
				block.setType(Material.FIRE);
				if (fireAI == 1 && Math.random() < 0.5) {
					AIManager.getManager().spawnAI(AIType.FIRE_SKELLY, block.getLocation());
				}
			}
		}
	}

	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.setFireTicks(60 + flame * 20);
	}

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowRes().addBoost(realArrowRes);
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(org.bukkit.entity.Skeleton.class, skeleton -> {
			skeleton.setFireTicks(10000);
		});
	}
}
