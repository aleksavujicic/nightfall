package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.upgrades.wrappers.WitherUpgrades;
import deimophobe.nightfall.util.ArrowMisc;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 20/01/17.
 */
class SkeletonWither extends AbstractToggleSkeleton<WitherUpgrades> {

	private final int shredBonus;
	private final int sniperBonus;
	private final double siphon;
	private final double arrowResistance;
	private final boolean withering;
	
	@Update @Display(reverse = true)
	private final Cooldown sniperCooldown;


	SkeletonWither(MonsterPlayer monster) {
		super(monster, MobType.SKELETON_WITHER, WitherUpgrades.class);
		
		WitherUpgrades upgrades = getUpgrades();
		
		this.shredBonus = upgrades.getShredBonus();
		this.sniperBonus = upgrades.getSniperBonus();
		this.siphon = upgrades.getSiphonAmount();
		this.arrowResistance = upgrades.getArrowResistance();
		this.withering = upgrades.hasWithering();
		
		this.sniperCooldown = upgrades.createSniperCooldown();
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		if (withering) {
			forceBowToggle(true);
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if ((damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7) || (damage.getType() == GameDamageType.WITHER_SKULL)) {
			damage.addPostDamageHandler(() -> {
				sniperCooldown.reset();
				monster.heal(siphon);
				
				if (withering) damage.getDwarf().givePoison(PoisonType.WITHER_SKELETON, 50);
			});
		}
		
		if (damage.getType() == GameDamageType.WITHER_SKULL) {
			damage.setArmourShred(getArmourShred());
		}
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowResistance().addBoost(arrowResistance);
	}

	@Override
	public void onProjectileLand(Projectile proj, Block block, BlockFace hitFace) {
		super.onProjectileLand(proj, block, hitFace);
		if (proj.getType() == EntityType.WITHER_SKULL) {
			skullExplosion(proj.getLocation());
		}
	}

	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (isToggled()) {
			if (!hasArrows(2)) return null;
			if (force < 0.7) return null;
			
			removeArrows(2);

			Location loc = monster.getEyeLocation();
			World world = loc.getWorld();

			WitherSkull skull = world.spawn(loc, WitherSkull.class, s -> {
				s.setShooter(monster.getPlayer());
				s.setVelocity(loc.getDirection().multiply(force*force));
			});

			new BukkitRunnable() {
				@Override
				public void run() {
					if (!skull.isDead()) {
						skullExplosion(skull.getLocation());
						skull.remove();
					}
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), 30); // 1.5 second lifetime

			world.playSound(loc, "entity.wither.break_block", 0.1f, 0.8f);
			
			changeDisguiseWatcher(SkeletonWatcher.class, (sw) -> sw.setSwingArms(false));

			return null;
		} else {
			return super.onBowFire(arrow, force);
		}
	}
	
	@Override
	protected boolean canToggle() {
		return withering;
	}
	
	@Override
	protected double getPowerBonus() {
		if (isSniperActive()) return sniperBonus;
		
		return 0;
	}
	
	@Override
	protected int getArmourShred() {
		return super.getArmourShred() + shredBonus;
	}
	
	private boolean isSniperActive() {
		return !sniperCooldown.isAvailable();
	}
	
	private void skullExplosion(Location centerLoc) {
		World world = monster.getLocation().getWorld();
		
		double kb = 0.2;
		
		world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 1, 0, 0, 0);
		world.spawnParticle(Particle.SMOKE_NORMAL, centerLoc, 70, 0.5, 0.5, 0.5, 0.03);
		world.playSound(centerLoc, "entity.zombie.infect", 2, 0.75f);
		
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
			double distance = offset.subtract(new Vector(0,1,0)).length();
			if (distance > 3.5) continue;
			
			//todo hardcoded damage
			DwarfDamage aoeDamage = dwarf.createDamage(this.monster, GameDamageType.WITHER_SKULL, 25);
			Vector knockback = offset.normalize().multiply(kb / Math.sqrt(Math.max(2, distance)));
			aoeDamage.setKnockback(knockback);
			aoeDamage.setArmourShred(getArmourShred());
			aoeDamage.fire();
		}
	}
	
}
