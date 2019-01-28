package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.NFBlocks;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.ai.AIType;
import deimophobe.nightfall.monster.upgrades.wrappers.FlamelancerUpgrades;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class SkeletonFlamelancer extends RangedMob<FlamelancerUpgrades> {
	private static final String PRIMARY_ARROW_TAG = "flamelancer-primary";

	private final int flameDuration;
	private final double conflagRange;
	private final double blockIgniteChance;
	
	private final int numArrows;
	
	private final double arrowResistance;
	private final boolean fireAI;
	
	@Update @Display @Interact(item = "blaze", click = ClickType.RIGHT)
	private final Cooldown blazeRunner = new UseCooldown(40*20, this::blaze);
	private final int blazeDuration;
	private boolean blazeCancelled;

	SkeletonFlamelancer(MonsterPlayer monster) {
		super(monster, MobType.SKELETON_FLAME, FlamelancerUpgrades.class);
		
		FlamelancerUpgrades upgrades = getUpgrades();
		
		this.blockIgniteChance = upgrades.getIgniteChance();
		this.flameDuration = upgrades.getFlameDuration();
		this.conflagRange = upgrades.getConflagRadius();
		
		this.arrowResistance = upgrades.getArrowResistance();
		this.numArrows = upgrades.getVolleyCount();
		this.fireAI = upgrades.spawnAIs();
		
		this.blazeDuration = upgrades.getBlazeRunnerDuration();
		this.blazeCancelled = false;
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		changeDisguiseWatcher(flagWatcher -> flagWatcher.setBurning(true));
	}
	
	@Override
	protected void setupItems() {
		super.setupItems();
		if (hasBlazerunner()) giveItem("blaze");
	}
	
	@Override
	public void update() {
		super.update();
		monster.getPlayer().setFireTicks(0);
        if (!blazeCancelled && blazeRunner.wasUsedWithin(blazeDuration)) {
	        if (everyNthTick(3)) {
		        Block block = monster.getLocation().getBlock();
	        	monster.playSound("entity.ghast.shoot", 0.3f, 1.5f, true);
	        	tryIgnite(block, true);
	        }
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		super.onBowFire(arrow, force);
		
		arrow.setFireTicks(10000);
		arrow.setCritical(false);
		arrow.setMetadata(PRIMARY_ARROW_TAG, new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
		
		final int arrowsToFire = (int) (numArrows*force*force);
		double damage = getBowPower()/3;
		for (int i=0; i<arrowsToFire; i++) {
			Arrow newArrow = ArrowMisc.summonArrow(monster, damage, force*2, force, 20f);
			newArrow.setCritical(false);
			newArrow.setFireTicks(10000);
		}
		
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock, BlockFace hitFace) {
		if (hitBlock == null) return;
		Block block = hitBlock.getRelative(hitFace);
		
		if (Math.random() < blockIgniteChance) {
			boolean success = tryIgnite(hitBlock, false);
			if (!success) tryIgnite(block, false);
		}
		
		if (proj.hasMetadata(PRIMARY_ARROW_TAG)) {
			applyConflag(proj.getLocation());
		}
	}

	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.setFireTicks(flameDuration);
		
		if (damage.hasArrow()) {
			Arrow arrow = damage.getArrow();
			applyConflag(arrow.getLocation());
			damage.removeKnockback();
		}
	}

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowResistance().addBoost(arrowResistance);
		damage.addPostDamageHandler(this::cancelBlazerunner);
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(org.bukkit.entity.Skeleton.class, skeleton -> {
			skeleton.setFireTicks(10000);
		});
	}
	
	private void applyConflag(Location center) {
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (Math.random() > 0.75) continue;
			if (dwarf.distanceTo(center) > conflagRange) continue;
			
			dwarf.increaseFireTicks(flameDuration);
			monster.playSound("entity.generic.burn", 0.3f, 1.5f, false);
		}
	}
	
	private void blaze() {
		if (!hasBlazerunner()) return;
		
		monster.playSound("entity.ghast.shoot", 1f, 0.5f, true);
		monster.givePotionEffect(PotionEffectType.FIRE_RESISTANCE, blazeDuration, 1, true, false, true);
		monster.givePotionEffect(PotionEffectType.SPEED, blazeDuration, 3, true, false, true);
		blazeCancelled = false;
	}
	
	private void cancelBlazerunner() {
		blazeCancelled = true;
		monster.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
		monster.removePotionEffect(PotionEffectType.SPEED);
	}
	
	private boolean hasBlazerunner() {
		return blazeDuration != 0;
	}
	
	private boolean tryIgnite(Block block, boolean reducedChance) {
		if (!NFBlocks.IGNORABLE.matchesBlock(block)) return false;
		if (block.getType() != Material.FIRE) {
			block.setType(Material.FIRE);
		}
		
		if (!fireAI) return true;
		double spawnChance = AIManager.getManager().getBaseSpawnChance()*3;
		if (reducedChance) spawnChance /= 2;
		if (Math.random() < spawnChance) {
			AIManager.getManager().spawnAI(AIType.FIRE_SKELLY, block.getLocation());
		}
		return true;
	}
}
