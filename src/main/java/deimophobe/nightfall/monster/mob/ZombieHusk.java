package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Husk;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * Created by TKiwisi on 9/22/17.
 */
public class ZombieHusk extends ZombieMob {
	
	private final int procRes;
	private final double arrowRes;
	private final int armourShred;
	private final int leapLvl;
	private final int regen;
	private final Cooldown leapCD;
	private final Cooldown smashCD;
	private boolean smashing;
	
	private final boolean stagger;
	
	private static final int STAGGER_DURATION = 70;
	
	private static final Integer[] shredValues = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20};
	private static final Integer[] arrowResValues = {0, 10, 20, 30, 40, 50}; // added by 25 later
	private static final Integer[] rebirthValues = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	
	public ZombieHusk(MonsterPlayer mons) {
		super(mons, MobData.getMobData("zombie.husk"));
		
		Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		this.armourShred = shredValues[upgrades.get("shred-husk")];
		this.procRes = upgrades.get("procresist");
		int arrowRes = arrowResValues[upgrades.get("arrow-husk")];
		int rebirthChance = rebirthValues[upgrades.get("rebirth-husk")];
		this.leapLvl = upgrades.get("groundsmash");
		this.regen = upgrades.get("regen");
		
		if (leapLvl != 0) {
			leapCD = new SimpleCooldown(400);
			smashCD = new ComplexCooldown(10);
			smashCD.reset();
		} else {
			leapCD = new DudCooldown();
			smashCD = new DudCooldown();
		}
		this.smashing = false;
		
		this.arrowRes = (double) (arrowRes + 25) / 100; // 25 is base for husks
		this.rebirthChance = (double) rebirthChance / 100;
		
		this.stagger = upgrades.get("stagger") >= 1;
		
		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, 25, "Husk Zombie");
		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
		getArmour().addModifier(ItemModifierType.SPEED, -25, "Husk Zombie");
		getArmour().addModifier(ItemModifierType.HEALTH, 10, "Husk Zombie"); // total of 30 hearts
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ATTACK, 5, "Husk Zombie");
		getArmour().addModifier(ItemModifierType.KB_RESIST, 10*procRes, "Upgrade");
		
		if (procRes == 10) {
			getArmour().addModifier(ItemModifierType.UNPROCCABLE, 1);
		} else if (procRes != 0) {
			getArmour().addModifier(ItemModifierType.PROC_RESIST, procRes * 10, "Upgrade");
		}
	}
	
	@Override
	public void update() {
		super.update();
		leapCD.update();
		if (smashing) {
			smashCD.update();
			World world = monster.getPlayer().getWorld();
			if (smashCD.isAvailable()) {
				smashCD.reset();
				Vector currentVelocity = monster.getPlayer().getVelocity();
				monster.getPlayer().setVelocity(new Vector(currentVelocity.getX() * 2.5, -1.5, currentVelocity.getZ() * 2.5));
			}
			if (monster.getPlayer().isOnGround()) {
				monster.removePotionEffect(PotionEffectType.LUCK);
				world.spawnParticle(Particle.EXPLOSION_NORMAL, monster.getLocation(), 60, 2, 0, 2, 0.05);
				monster.playSound("drum", 1f, 0.5f, true);
				monster.playSound("entity.generic.explode", 0.5f, 0.5f, true);
				for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
					Vector offset = dwarf.getEyeLocation().subtract(monster.getLocation()).toVector();
					if (offset.length() > 5) continue;
					
					Vector distance = offset.setY(0).normalize();
					Vector knockback = distance.multiply((0.3 + 0.2 * leapLvl) / Math.sqrt(Math.max(1, offset.length())));
					knockback.setY(knockback.getY() / 2 + 0.5);
					
					DwarfDamage aoeDamage = dwarf.createDamage(this.monster, GameDamageType.HUSK_STOMP, 6 * leapLvl);
					aoeDamage.addKnockback(knockback);
					aoeDamage.fire();
					if (stagger) {
						if (dwarf.isHero()) {
						    dwarf.setStunned(15);
                        } else {
                            dwarf.givePotionEffect(PotionEffectType.BLINDNESS, STAGGER_DURATION, 1, true, false, true);
							dwarf.givePotionEffect(PotionEffectType.WEAKNESS, STAGGER_DURATION, 1, true, false, true);
                            dwarf.givePotionEffect(PotionEffectType.CONFUSION, STAGGER_DURATION + 40, 1, true, false, true);
                            dwarf.setStunned(STAGGER_DURATION);
                        }
					}
				}
				smashCD.reset();
				smashing = false;
			}
		}
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		monster.givePermanentPotionEffect(PotionEffectType.REGENERATION, regen);
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowRes().addBoost(arrowRes);
		if (Math.random() < procRes * 0.1) {
			damage.setProc(false);
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.addArmourShred(armourShred);
	}
	
	@Override
	public void onUse(ClickType click, Block block, BlockFace face) {
		super.onUse(click, block, face);
		if (click.isRightClick() && isPlayerHoldingWeapon()) {
			if (leapCD.isAvailable()) {
				leapCD.reset();
				
				double yaw = monster.getPlayer().getLocation().getYaw();
				double radYaw = yaw * Math.PI / 180;
				
				double hVel = (double) leapLvl / 15 + 0.4;
				double vVel = (double) leapLvl / 50 + 0.5;
				monster.getPlayer().setVelocity(new Vector(-hVel * Math.sin(radYaw), vVel, hVel * Math.cos(radYaw)));
				giveSpawnProtection(50);
				smashing = true;
				monster.getPlayer().getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, monster.getLocation(), 10, 0.5, 0, 0.5, 0.05);
			}
		}
	}
	
	@Override
	public float getCooldown() {
		return leapCD.getCooldown();
	}
	
	@Override
	protected DeadEntitySpawner<Husk> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(Husk.class, husk -> {
			husk.setBaby(false);
		});
	}
}
