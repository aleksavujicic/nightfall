package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieVillagerWatcher;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

/**
 * Created by TKiwisi on 10/06/17.
 */
public class ZombieSaboteur extends ZombieMob {
	

	private final int sabotage;
	private final PoisonType poison;
	private final int pick;
	private final int epinephrine;
	private final int healing;
	private final Cooldown sneakCD;
	private final int sneakLvl;
	private final int speedInf;
	private final boolean assa;
	
	private final static Villager.Profession PROFESSION = Villager.Profession.HUSK;
	
	// The first null entry represents no poison if unupgraded
	private final PoisonType[] POISONS = new PoisonType[]{null, PoisonType.SAB1, PoisonType.SAB2, PoisonType.SAB3, PoisonType.SAB4, PoisonType.SAB5};
	
	
	public ZombieSaboteur(MonsterPlayer mons) {
		super(mons, MobData.getMobData("zombie.saboteur"));
		
		Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		this.sabotage = upgrades.get("sabotage");
		this.healing = upgrades.get("healing");
		this.pick = upgrades.get("pick");
		this.epinephrine = upgrades.get("epinephrine");
		this.speedInf = upgrades.get("speed-inf");
		int speed = epinephrine * 5;
		int morespeed = speedInf * 3;
		
		this.sneakLvl = upgrades.get("sneak");
		sneakCD = new SimpleCooldown((30 - sneakLvl * 5) * 20);
		
		this.assa = upgrades.get("assassination") >= 1;
		
		int poisonLvl = upgrades.get("poison");
		poison = POISONS[poisonLvl];
		
		if (pick > 0) {
			setWeapon("wood-pickaxe");
			getWeapon().addModifier(ItemModifierType.EFFICIENCY, (pick - 1), "Pick Upgrade");
			
			// Reapply attack as it was lost with weapon override
			int attack = upgrades.get("attack") + upgrades.get("attack-inf");
			getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
			getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, attack, "Upgrade");
		}

		getArmour().addModifier(ItemModifierType.SPEED, 25, "Saboteur Zombie");
		getWeapon().addModifier(ItemModifierType.SPEED, speed, "Epinephrine");
		getWeapon().addModifier(ItemModifierType.SPEED, morespeed, "More Speed");
		int saboHealthMalus = (upgrades.get("health") + upgrades.get("health-inf")) * -1;
		getArmour().addModifier(ItemModifierType.HEALTH, saboHealthMalus, "Saboteur Zombie");
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		changeDisguiseWatcher(ZombieVillagerWatcher.class, zw -> {
			zw.setProfession(PROFESSION);
			zw.setBaby(true);
			zw.setSprinting(false);
		});
	}
	
	@Override
	public void update() {
		super.update();
		if (isInvisible()) {
			if (everyNthTick(20)) {
				Location loc = monster.getLocation();
				loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 7, 0.3, 0.3, 0.3, 0);
			}
		} else {
			sneakCD.update();
		}
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() == GameDamageType.MELEE) {
			monster.givePotionEffect(PotionEffectType.SLOW, 30, 2, true, true, true);
		}
		monster.removePotionEffect(PotionEffectType.INVISIBILITY);
		monster.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
	}
	
	@Override
	public void onUse(ClickType click, Block block, BlockFace face) {
		if (click.isRightClick() && isPlayerHoldingWeapon() && sneakCD.isAvailable()) {
            monster.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
            monster.givePotionEffect(PotionEffectType.SPEED, 8 * sneakLvl, 3, true, false, true);
            Location loc = monster.getLocation();
            World world = loc.getWorld();
            world.spawnParticle(Particle.SMOKE_LARGE, loc, 160, 0.8, 0.8, 0.8, 0);
            world.playSound(loc, "entity.generic.burn", 1f, 0.7f);
			if (healing > 0) {
				monster.givePotionEffect(PotionEffectType.REGENERATION, 12 * 2 * healing, 3, true, false, true);
			}
			if (assa) {
			    monster.givePermanentPotionEffect(PotionEffectType.INCREASE_DAMAGE, 1);
            }
			sneakCD.reset();
		}
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		didBreak = super.onBlockBreak(block, didBreak);
		if (didBreak) {
			monster.removePotionEffect(PotionEffectType.INVISIBILITY);
            monster.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		}
		return didBreak;
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (assa && isInvisible()) {
			monster.playSound("entity.wither.shoot", 1f, 2f, true);
			damage.getMultiPartDamage().addBoost(57);
		}
		damage.multiplyKnockback(0.75);
		damage.addPostDamageHandler(() -> {
			if (poison != null) {
				damage.getDwarf().givePoison(poison, 50);
			}
			if (sabotage > 0 && isInvisible()) {
				damage.getDwarf().givePotionEffect(PotionEffectType.UNLUCK, 100, sabotage, true, false, true);
			}
			monster.removePotionEffect(PotionEffectType.INVISIBILITY);
			monster.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			sneakCD.reset();
		});
	}
	
	@Override
	public float getCooldown() {
		return sneakCD.getCooldown();
	}
	
	@Override
	public double getShrineWeight() {
		if (isInvisible()) return 0;
		else return super.getShrineWeight();
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(ZombieVillager.class, zombie -> {
			zombie.setBaby(true);
			zombie.setVillagerProfession(PROFESSION);
		});
	}
	
	private boolean isInvisible() {
		return monster.hasPotionEffect(PotionEffectType.INVISIBILITY);
	}
}
