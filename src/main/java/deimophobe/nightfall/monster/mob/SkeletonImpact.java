package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

class SkeletonImpact extends AbstractToggleSkeleton {
	
	private final int aoe;
	@Update @Display private final Cooldown warpCD;
	@Update @Display private final Cooldown reactionCD;

	private final int punch;
	private final boolean hasMeleeKB;
	private final int reaction;
	private final Set<Arrow> activeArrows = new HashSet<>();
	
	private final static String ARROW_METADATA_KEY = "active";

	SkeletonImpact(MonsterPlayer monster) {
		super(monster, MobData.getMobData("skeleton.impact"));
		int punch = upgrades.get("punch");
		int meleekb = upgrades.get("meleekb");
		int extraHealth = upgrades.get("extrahealth-impact");
		int warpweaver = upgrades.get("warpweaver");
		
		this.aoe = upgrades.get("aoe");
		this.punch = punch;
		this.hasMeleeKB = (meleekb > 0);
		this.reaction = upgrades.get("reaction");
		if (reaction > 0) {
			reactionCD = new ComplexCooldown(15 * 20);
		} else {
			reactionCD = new DudCooldown();
		}

		if (warpweaver > 0) {
			warpCD = new ComplexCooldown(40 * 20);
		} else {
			warpCD = new DudCooldown();
		}

		getArmour().addModifier(ItemModifierType.HEALTH, extraHealth * 3, "Upgrade");
		getWeapon().addModifier(ItemModifierType.FAKE_PUNCH, punch, "Upgrade");
		
		makeItemMutable("stick");
		getItem("stick").addModifier(ItemModifierType.KNOCKBACK, meleekb, "Upgrade");
	}
	
	@Override
	protected void giveItems() {
		if (hasMeleeKB || reaction > 0) giveItem("stick");
		super.giveItems();
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isLeftClick(action) && isPlayerHoldingWeapon()) {
			removeActiveArrows();
		}
		if (Misc.isRightClick(action) && isPlayerHoldingItem("stick") && reactionCD.isAvailable()) {
			reactionCD.reset();
            World world = monster.getLocation().getWorld();
            world.spawnParticle(Particle.EXPLOSION_LARGE, monster.getLocation(), 3, 1, 1, 1);
            double kb = 0.5 + 0.35 * reaction;
            for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
                Vector offset = dwarf.getEyeLocation().subtract(monster.getLocation()).toVector();
                if (offset.length() > 4.5) {
                    continue;
                }

                Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(2, offset.length())));
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
		if (damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7 && aoe > 0) {
			Location centerLoc = damage.getDwarf().getEyeLocation();
			impactExplosion(centerLoc, damage.getDwarf());
		}
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock, Entity hitEntity) {
		if (hitBlock == null) return;
		
		if (warpCD.isAvailable() && isToggled() && isActiveProjectile(proj)) {
			if (!GameMap.getCurrentMap().getCurrentShrineProtection().continsEntity(proj)) {
				forceBowToggle(false);
				
				Location newSpot = proj.getLocation().add(0, 0.25, 0);
				newSpot.add(proj.getLocation().getDirection().multiply(0.25));
				newSpot.setDirection(monster.getLocation().getDirection());
				teleportTo(newSpot);
				
				warpCD.reset();
				
				activeArrows.remove(proj);
			}
		} else {
			BlockFace face = Misc.getBlockFaceProjectileHit(proj, hitBlock);
			Block explosionBlock = hitBlock.getRelative(face);
			Location centerLoc = explosionBlock.getLocation();
			impactExplosion(centerLoc, null);
		}
	}
	
	private void impactExplosion(Location centerLoc, Dwarf exempt) {
		if (aoe == 0 || monster.getLocation().getY() - centerLoc.getY() > 30) {
			return; // prevents impact shooting down from too high up
		}
		World world = monster.getLocation().getWorld();
		world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 3, 1, 1, 1);
		double kb = 0.35 + aoe * 0.1;
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (dwarf == exempt) {
				continue;
			}
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
			if (offset.length() > 3.5) {
				continue;
			}
			
			
			Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(2, offset.length())));
			knockback.setY(knockback.getY() / 2 + 0.1);
			
			DwarfDamage aoeDamage = dwarf.createDamage(this.monster, GameDamageType.IMPACT_AOE, 5 * aoe);
			aoeDamage.setKnockback(knockback);
			aoeDamage.fire();
			
		}
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		removeActiveArrows();
	}
	
	@Override
	protected int getPower() {
		return super.getPower() + 3 * aoe;
	}
	
	@Override
	protected boolean canToggle() {
		return warpCD.isAvailable();
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		Projectile proj = super.onBowFire(arrow, force);
		if (ArrowMisc.getArrowForce(arrow) < 0.5) {
			return null;
		}
		
		if (isToggled()) {
			proj.setMetadata(ARROW_METADATA_KEY, new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
			ArrowMisc.setGlowColour(arrow, ChatColor.DARK_PURPLE);
			activeArrows.add(arrow);
		}
		
		if (proj instanceof Arrow) {
			((Arrow) proj).setKnockbackStrength(punch);
		}
		
		return proj;
	}
	
	protected boolean isActiveProjectile(Projectile proj) {
		return proj.hasMetadata(ARROW_METADATA_KEY);
	}
	
	
	private void removeActiveArrows() {
		for (Arrow arrow : activeArrows) {
			ArrowMisc.removeGlow(arrow);
			arrow.removeMetadata(ARROW_METADATA_KEY, NightfallPlugin.getPlugin());
		}
		activeArrows.clear();
	}
	
	private void teleportTo(Location location) {
		Location here = monster.getLocation();
		monster.getPlayer().setFallDistance(0);
		monster.teleportTo(location);
		
		World world = location.getWorld();
		world.spawnParticle(Particle.SPELL_WITCH, location, 20, 0.5, 0.5, 0.5);
		world.spawnParticle(Particle.SPELL_WITCH, here, 20, 0.5, 0.5, 0.5);
		world.playSound(location, "entity.illusion_illager.mirror_move", 0.6f, 0.95f);
		world.playSound(here, "entity.illusion_illager.mirror_move", 0.6f, 0.95f);
	}
}
