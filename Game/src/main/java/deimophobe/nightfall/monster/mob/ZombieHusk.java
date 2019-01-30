package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.upgrades.wrappers.HuskUpgrades;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Husk;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by TKiwisi on 9/22/17.
 */
public class ZombieHusk extends RebirthableMob<HuskUpgrades> {
	
	private static final int STAGGER_DURATION = 50;
	private static final int STAGGER_DURATION_HERO = 20;
	
	private final int armourShred;
	private final double arrowResistance;
	private final double procResistChance;
	private final int leapLevel;
	
	private final boolean stagger;
	private final double regenBonus;
	
	@Update @Display @Interact(click = ClickType.RIGHT)
	private final Cooldown leapCooldown;
	private final Cooldown downSmasher;
	private boolean smashing;
	
	ZombieHusk(MonsterPlayer mons) {
		super(mons, MobType.ZOMBIE_HUSK, HuskUpgrades.class);
		
		this.smashing = false;
		
		HuskUpgrades upgrades = getUpgrades();
		this.armourShred = upgrades.getArmourShred();
		this.arrowResistance = upgrades.getArrowResistance();
		this.procResistChance = upgrades.getProcResistChance();
		
		this.leapLevel = upgrades.getSmashLevel();
		this.regenBonus = upgrades.getHealthPerSecondBonus();
		
		if (leapLevel != 0) {
			leapCooldown = new UseCooldown(400, this::startLeap);
			downSmasher = new RepeaterCooldown(10, this::startSmashDown);
			downSmasher.reset();
		} else {
			leapCooldown = new DudCooldown();
			downSmasher = new DudCooldown();
		}
		
		this.stagger = upgrades.hasStagger();
	}
	
	@Override
	public void update() {
		super.update();
		if (smashing) {
			downSmasher.update();
			if (monster.getPlayer().isOnGround()) {
				endSmash();
			}
		}
		if (everyNthTick(20)) {
			monster.heal(regenBonus);
		}
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		getUpgrades().applyRegen();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowResistance().addBoost(arrowResistance);
		if (Math.random() < procResistChance) {
			damage.setProc(false);
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.addArmourShred(armourShred);
	}
	
	@Override
	protected DeadEntitySpawner<Husk> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(Husk.class, husk -> {
			husk.setBaby(false);
		});
	}
	
	private void startLeap() {
		leapCooldown.reset();
		
		double hVel = (double) leapLevel / 15 + 0.4;
		double vVel = (double) leapLevel / 50 + 0.5;
		monster.leap(hVel, vVel);
		
		giveSpawnProtection(50, false, false);
		smashing = true;
		monster.getPlayer().getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, monster.getLocation(), 10, 0.5, 0, 0.5, 0.05);
	}
	
	private void startSmashDown() {
		Vector currentVelocity = monster.getPlayer().getVelocity();
		monster.getPlayer().setVelocity(new Vector(currentVelocity.getX() * 2.5, -1.5, currentVelocity.getZ() * 2.5));
	}
	
	private void endSmash() {
		World world = monster.getWorld();
		
		removeSpawnProtection();
		world.spawnParticle(Particle.EXPLOSION_NORMAL, monster.getLocation(), 60, 2, 0, 2, 0.05);
		monster.playSound("drum", 1f, 0.5f, true);
		monster.playSound("entity.generic.explode", 0.5f, 0.5f, true);
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(monster.getLocation()).toVector();
			if (offset.length() > 5) continue;
			
			Vector distance = offset.setY(0).normalize();
			Vector knockback = distance.multiply((0.3 + 0.2 * leapLevel) / Math.sqrt(Math.max(1, offset.length())));
			knockback.setY(knockback.getY() / 2 + 0.5);
			
			DwarfDamage aoeDamage = dwarf.createDamage(this.monster, GameDamageType.HUSK_STOMP, 6 * leapLevel);
			aoeDamage.addKnockback(knockback);
			aoeDamage.fire();
			if (stagger) {
				if (dwarf.isHero()) {
					dwarf.setStunned(STAGGER_DURATION_HERO);
				} else {
					dwarf.giveBlindness(STAGGER_DURATION);
					dwarf.givePotionEffect(PotionEffectType.WEAKNESS, STAGGER_DURATION, 1, true, false, true);
					dwarf.givePotionEffect(PotionEffectType.CONFUSION, STAGGER_DURATION + 40, 1, true, false, true);
					dwarf.setStunned(STAGGER_DURATION);
				}
			}
		}
		downSmasher.reset();
		smashing = false;
	}
}
