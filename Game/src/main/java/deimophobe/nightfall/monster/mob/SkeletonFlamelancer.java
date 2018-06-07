package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.ai.AIType;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class SkeletonFlamelancer extends Skeleton {
	private static final int DEFAULT_VOLLEY = 20;
	private static final int MAX_VOLLEY = 50;

	private final int flameDuration;
	private final double conflagRange;
	private final double blockIgniteChance;
	
	private final double volley;
	private double volleyBonus = 0;
	private final int numArrows;
	
	private final double arrowRes;
	private final boolean fireAI;
	
	@Update @Display
	private final ComplexCooldown blazeOfGlory = new ComplexCooldown(30*20, this::blaze);
	private final int blazeDuration;

	private static final Integer[] arrowResValues = {0, 10, 20, 30, 40, 50};

	SkeletonFlamelancer(MonsterPlayer monster) {
		super(monster, MobData.getMobData("skeleton.flamelancer"));
		
		int flame = upgrades.get("flame");
		this.blockIgniteChance = 0.025 + flame * 0.03;
		this.flameDuration = 60 + 20*flame;
		this.conflagRange = flame*0.8;
		
		int upgradeArrowRes = arrowResValues[upgrades.get("arrowres-flamelancer")];
		this.arrowRes = upgradeArrowRes*0.01;
		
		this.volley = upgrades.get("volley") * 0.14 + 0.1;
		this.numArrows = Math.min(DEFAULT_VOLLEY + upgrades.get("volley-inf"), MAX_VOLLEY);
		
		int speed = upgrades.get("speed");
		this.fireAI = upgrades.get("fireai") > 0;
		
		this.blazeDuration = upgrades.get("blaze") * 20;

		getArmour().addModifier(ItemModifierType.SPEED, 10, "Flamelancer");
		getArmour().addModifier(ItemModifierType.SPEED, speed * 10, "Upgrade");
		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, upgradeArrowRes, "Upgrade");
		getWeapon().addModifier(ItemModifierType.VOLLEY, DEFAULT_VOLLEY, "Flamelancer");
		getWeapon().addModifier(ItemModifierType.VOLLEY, numArrows - DEFAULT_VOLLEY, "More Volley");
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		changeDisguiseWatcher(flagWatcher -> flagWatcher.setBurning(true));
	}
	
	@Override
	protected void giveItems() {
		super.giveItems();
		if (blazeDuration != 0) giveItem("blaze");
	}
	
	@Override
	public void update() {
		super.update();
		monster.getPlayer().setFireTicks(0);
        if (blazeOfGlory.wasUsedWithin(blazeDuration)) {
	        Block block = monster.getLocation().getBlock();
			if (BlockType.IGNORABLE.matchesBlock(block)) {
				block.setType(Material.FIRE);
				if (everyNthTick(3)) monster.playSound("entity.ghast.shoot", 0.3f, 1.5f, true);
			}
		}
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (isPlayerHoldingItem("blaze") && blazeDuration != 0) {
			blazeOfGlory.tryUse();
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		super.onBowFire(arrow, force);
		
		arrow.setFireTicks(10000);
		arrow.setCritical(false);
		if (Math.random() < volley + volleyBonus) {
			final int arrowsToFire = (int) (numArrows*force*force);
			for (int i=0; i<arrowsToFire; i++) {
				Arrow newArrow = ArrowMisc.summonArrow(monster, getPower(), force*2, force, 20f);
				newArrow.setCritical(false);
				newArrow.setFireTicks(10000);
			}
			volleyBonus = 0;
		} else {
			volleyBonus = Math.min(volleyBonus + 0.02*force, 0.2);
		}
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock, Entity hitEntity) {
		if (hitBlock == null) return;
		
		BlockFace face = Misc.getBlockFaceProjectileHit(proj, hitBlock);
		Block block = hitBlock.getRelative(face);
		
		if (Math.random() < blockIgniteChance) {
			boolean success = tryIgnite(hitBlock);
			if (!success) tryIgnite(block);
		}
		
		conflagration(proj.getLocation());
	}

	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.setFireTicks(flameDuration);
		
		if (damage.hasArrow()) {
			Arrow arrow = damage.getArrow();
			conflagration(arrow.getLocation());
		}
	}

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowRes().addBoost(arrowRes);
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(org.bukkit.entity.Skeleton.class, skeleton -> {
			skeleton.setFireTicks(10000);
		});
	}
	
	private void conflagration(Location center) {
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (dwarf.distanceTo(center) > conflagRange) continue;
			
			Player player = dwarf.getPlayer();
			int currentTicks = player.getFireTicks();
			int fireTicks = Math.max(currentTicks, flameDuration/20);
			player.setFireTicks(fireTicks);
			
			if (everyNthTick(3)) monster.playSound("entity.generic.burn", 0.3f, 1.5f, false);
		}
	}
	
	private void blaze() {
		monster.playSound("entity.ghast.shoot", 1f, 0.5f, true);
		monster.givePotionEffect(PotionEffectType.FIRE_RESISTANCE, blazeDuration, 1, true, false, true);
	}
	
	private boolean tryIgnite(Block block) {
		if (BlockType.IGNORABLE.matchesBlock(block)) {
			block.setType(Material.FIRE);
			if (fireAI && Math.random() < 0.5) {
				AIManager.getManager().spawnAI(AIType.FIRE_SKELLY, block.getLocation());
			}
			return true;
		}
		return false;
	}
}
