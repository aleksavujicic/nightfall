package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.wrappers.ImpactUpgrades;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Map;

class SkeletonImpact extends AbstractToggleSkeleton<ImpactUpgrades> {
	
	private final int aoe;
	@Update @Display
	private final Cooldown reactionCD;

	private final int punch;
	private final boolean hasMeleeKB;
	private final int reaction;
	private final int aerodynamic;
	private final int forceInf;
	
	private final static String ARROW_METADATA_KEY = "active";

	SkeletonImpact(MonsterPlayer monster) {
		super(monster, MobType.SKELETON_IMPACT, ImpactUpgrades.class);
		
		Map<String,Integer> upgrades = null;
		
		int punch = upgrades.get("punch");
		int meleekb = upgrades.get("meleekb");
		int extraHealth = upgrades.get("extrahealth-impact");
		this.aerodynamic = upgrades.get("aerodynamic");
		this.forceInf = upgrades.get("force-inf");
		
		this.aoe = upgrades.get("aoe");
		this.punch = punch;
		this.hasMeleeKB = (meleekb > 0);
		this.reaction = upgrades.get("reaction");
		if (reaction > 0) {
			reactionCD = new ComplexCooldown(15 * 20);
		} else {
			reactionCD = new DudCooldown();
		}

		getArmour().addModifier(ItemModifierType.HEALTH, extraHealth, "Upgrade");
		getWeapon().addModifier(ItemModifierType.FAKE_PUNCH, punch, "Upgrade");
		getWeapon().addModifier(ItemModifierType.IMPACT_EXTRA, forceInf, "More Knockback");
		
		makeItemMutable("stick");
		getItem("stick").addModifier(ItemModifierType.KNOCKBACK, meleekb, "Upgrade");
	}
	
	@Override
	protected void setupItems() {
		super.setupItems();
		if (hasMeleeKB || reaction > 0) giveItem("stick");
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && isPlayerHoldingItem("stick") && reactionCD.isAvailable()) {
			reactionCD.reset();
            World world = monster.getLocation().getWorld();
            world.spawnParticle(Particle.EXPLOSION_LARGE, monster.getLocation(), 3, 1, 1, 1);
            double kb = 1 + 0.5 * reaction;
            for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
                Vector offset = dwarf.getEyeLocation().subtract(monster.getLocation()).toVector();

                double range = 4.5;
                double offlength = offset.length();
                if (offlength > range) continue;

                Vector knockback = offset.normalize().multiply(kb * (1 - offlength / range));
                knockback.setY(knockback.getY() / 2 + 0.1);

                DwarfDamage aoeDamage = dwarf.createDamage(this.monster, GameDamageType.IMPACT_AOE, 5 * reaction);
                aoeDamage.setKnockback(knockback);
                aoeDamage.fire();
            }
            Vector dir = monster.getEyeLocation().getDirection().multiply(-2.5 - 0.5*reaction).normalize();
            dir = dir.setY(dir.getY() + 0.5);
            monster.setVelocity(dir);
        }
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (!damage.hasArrow()) return;
		
		Dwarf dwarf = damage.getDwarf();
		Arrow arrow = damage.getArrow();
		if (ArrowMisc.getArrowForce(arrow) > 0.7 && hasAOE()) {
			Location centerLoc = dwarf.getEyeLocation();
			impactExplosion(centerLoc, dwarf, isActiveProjectile(arrow));
		}
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock, BlockFace hitFace) {
		if (hitBlock == null) return;

		Block explosionBlock = hitBlock.getRelative(hitFace);
		Location centerLoc = explosionBlock.getLocation();
		impactExplosion(centerLoc, null, isActiveProjectile(proj));
	}
	
	private void impactExplosion(Location centerLoc, Dwarf exempt, boolean affectSelf) {
		if (!hasAOE() || monster.getLocation().getY() - centerLoc.getY() > 20) {
			return; // prevents impact shooting down from too high up
		}
		World world = monster.getLocation().getWorld();
		world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 3, 1, 1, 1);
		double kb = 0.7 + aoe * 0.3 + forceInf * 0.2;
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (dwarf == exempt) {
				continue;
			}
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();

			double range = 4.5;
			double offlength = offset.length();
			if (offlength > range) continue;

			Vector knockback = offset.normalize().multiply(kb * (1 - offlength / range));
			knockback.setY(knockback.getY() / 2 + 0.1);
			
			DwarfDamage aoeDamage = dwarf.createDamage(this.monster, GameDamageType.IMPACT_AOE, 5 * aoe);
			aoeDamage.setKnockback(knockback);
			aoeDamage.fire();
			
		}
		
		if (affectSelf) {
			Vector offset = monster.getEyeLocation().subtract(centerLoc).toVector();
			if (offset.length() < 6.5) {
				Vector knockback = offset.normalize().multiply(kb * 2 / Math.sqrt(Math.max(2, offset.length())));
				knockback.setY(knockback.getY() / 2 + 0.1);
				monster.setVelocity(knockback);
			}
		}
	}
	
//	@Override
//	protected int getPower() {
//		return super.getPower() + 3 * aoe;
//	}
	
	@Override
	protected boolean canToggle() {
		return (aerodynamic > 0) && hasArrows(2);
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		Projectile proj = super.onBowFire(arrow, force);
		if (ArrowMisc.getArrowForce(arrow) < 0.5) {
			return null;
		}
		
		if (isToggled()) {
			proj.setMetadata(ARROW_METADATA_KEY, new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
			removeArrows(2);
		}
		
		if (proj instanceof Arrow) {
			((Arrow) proj).setKnockbackStrength(punch);
		}
		
		checkToggle();
		return proj;
	}
	
	protected boolean isActiveProjectile(Projectile proj) {
		return proj.hasMetadata(ARROW_METADATA_KEY);
	}
	
	private boolean hasAOE() {
		return aoe > 0;
	}
}
