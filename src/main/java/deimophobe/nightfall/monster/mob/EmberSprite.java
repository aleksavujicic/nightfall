package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by TKiwisi on 10/16/17.
 */
public class EmberSprite extends AbstractMob implements FloatyMob {
	
	@Display @Update private ComplexCooldown launchCD = new ComplexCooldown(300, this::launch);
	@Update private ComplexCooldown fireCD = new ComplexCooldown(10, this::shootFireball);
	@Update private ComplexCooldown preloadCD = new ComplexCooldown(40);
	private ComplexCooldown reloadCD = new ComplexCooldown(30, this::giveAmmo);
	private final int MAX_AMMO = 4;
	
	private int currentAmmo = MAX_AMMO;

	public EmberSprite(MonsterPlayer mons) {
		super(mons, MobType.EMBER_SPRITE);
	}

	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		giveItem("blaze-ammo", MAX_AMMO);
		resetFloatiness();
	}

	@Override
	public void update() {
		super.update();
		if (preloadCD.isAvailable()) {
			reloadCD.update();
			reloadCD.tryUse();
		}
	}

	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && isPlayerHoldingItem("blaze-ammo")) {
			fireCD.tryUse();
		}
	}

	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getType() == GameDamageType.RANGED) {
			damage.cancel();
		}
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block block, Entity hitEntity) {
		super.onProjectileLand(proj, block, hitEntity);
		blazeExplosion(proj.getLocation());
	}

	@Override
	public void onShift(boolean sneak) {
		super.onShift(sneak);
		launchCD.tryUse();
	}
	
	@Override
	public void resetFloatiness() {
		monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -2);
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 5);
	}
	
	private void giveAmmo() {
		if (currentAmmo < MAX_AMMO) {
			currentAmmo++;
			giveItem("blaze-ammo", 1);
		}
	}
	
	private void shootFireball() {
		monster.useHeldItem();
		currentAmmo--;
		
		reloadCD.reset();
		preloadCD.reset();
		
		Location loc = monster.getEyeLocation();
		World world = loc.getWorld();
		
		SmallFireball fireball = world.spawn(loc, SmallFireball.class, sf -> {
			sf.setShooter(monster.getPlayer());
			sf.setVelocity(loc.getDirection().multiply(1.5f));
		});
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!fireball.isDead()) {
					blazeExplosion(fireball.getLocation());
					fireball.remove();
				}
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 24); // 1.2 seconds lifetime
		
		world.playSound(loc, "entity.blaze.shoot", 2, 1f);
	}
	
	private void launch() {
		Location loc = monster.getLocation();
		World world = loc.getWorld();
		blazeExplosion(loc);
		world.playSound(loc, "entity.firework.launch", 2, 0.8f);
		monster.setVelocity(0, 3, 0);
	}

	private void blazeExplosion(Location centerLoc) {
		World world = monster.getLocation().getWorld();

		double damage = 40;
		int armorShred = 35;
		double power = 4.5;
		double kb = 1;

		BlockConverter.convert(BlockConverter.Type.EXPLOSION, centerLoc, power);
		world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 3, 1, 1, 1);
		world.playSound(centerLoc, "entity.generic.explode", 2, 1);

		int radius = 1;

		for (int x = -radius; x <= radius; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -radius; z <= radius; z++) {
					Block block = centerLoc.clone().add(x, y, z).getBlock();
					Block blockBelow = centerLoc.clone().add(x,y-1, z).getBlock();

					if (BlockType.IGNORABLE.matchesBlock(block) && !BlockType.IGNORABLE.matchesBlock(blockBelow) && (Math.random() < 0.015)) {
						block.setType(Material.FIRE);
					}
				}
			}
		}

		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
			double distance = offset.subtract(new Vector(0,1,0)).length();
			if (distance > 4) continue;

			Vector knockback = offset.normalize().multiply(kb / Math.sqrt(Math.max(2, distance)));
			knockback.setY(knockback.getY() / 2 + 0.1);

			DwarfDamage aoeDamage = dwarf.createDamage(this.monster, GameDamageType.BLAZE_EXPLOSION, damage);
			aoeDamage.setKnockback(knockback);
			aoeDamage.setArmourShred(armorShred);
			aoeDamage.fire();
		}
	}
}
