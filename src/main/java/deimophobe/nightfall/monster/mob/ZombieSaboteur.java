package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
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
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

/**
 * Created by TKiwisi on 10/06/17.
 */
public class ZombieSaboteur extends ZombieMob {
	

	private final int sabotage;
	private final int poison;
	private final int pick;
	private final int epinephrine;
	private final int healing;
	private final Cooldown sneakCD;
	private final int sneakLvl;
	private final boolean assa;
	private int healBuf;
	
	private final static Villager.Profession PROFESSION = Villager.Profession.HUSK;
	
	
	public ZombieSaboteur(MonsterPlayer mons) {
		super(mons, MobData.getMobData("zombie.saboteur"));
		
		Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		this.sabotage = upgrades.get("sabotage");
		this.healing = upgrades.get("healing");
		this.poison = upgrades.get("poison");
		this.pick = upgrades.get("pick");
		this.epinephrine = upgrades.get("epinephrine");
		int speed = epinephrine * 5;
		
		this.sneakLvl = upgrades.get("sneak");
		if (sneakLvl > 0 || healing > 0) {
			sneakCD = new SimpleCooldown((30 - sneakLvl * 4) * 20);
		}
		else {
			sneakCD = new DudCooldown();
		}
		
		this.assa = upgrades.get("assassination") >= 1;
		
		if (pick > 0) {
			setWeapon("wood-pickaxe");
			getWeapon().addModifier(ItemModifierType.EFFICIENCY, (pick - 1), "Pick Upgrade");
			
			// Reapply attack as it was lost with weapon override
			int attack = upgrades.get("attack") + upgrades.get("attack-inf");
			getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
			getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, attack, "Upgrade");
		}

		getArmour().addModifier(ItemModifierType.SPEED, 25, "Saboteur Zombie");
		getArmour().addModifier(ItemModifierType.SPEED, speed, "Epinephrine");
		int saboHealthMalus = (upgrades.get("health") + upgrades.get("health-inf")) * -1;
		getArmour().addModifier(ItemModifierType.HEALTH, saboHealthMalus, "Saboteur Zombie");
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		changeDisguiseWatcher(ZombieVillagerWatcher.class, zw -> {
			zw.setProfession(PROFESSION);
			zw.setBaby(true);
		});
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (isInvisible()) {
			if (sec) {
				Location loc = monster.getLocation();
				loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 7, 0.3, 0.3, 0.3, 0);
			}
		} else {
			sneakCD.update();
		}
		if (halfSec && healBuf > 0) {
			monster.heal(1);
			healBuf--;
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
	public void onUse(Action action, Block block, BlockFace face) {
		if (Misc.isRightClick(action) && isPlayerHoldingWeapon() && sneakCD.isAvailable()) {
			if (sneakLvl > 0) {
				monster.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
				monster.givePotionEffect(PotionEffectType.SPEED, 8 * sneakLvl, 3, true, true, true);
				Location loc = monster.getLocation();
				World world = loc.getWorld();
				world.spawnParticle(Particle.SMOKE_LARGE, loc, 160, 0.8, 0.8, 0.8, 0);
				world.playSound(loc, "entity.generic.burn", 1f, 0.7f);
			}
			if (healing > 0) {
				healBuf = healing * 2;
			}
			sneakCD.reset();
		}
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		didBreak = super.onBlockBreak(block, didBreak);
		if (didBreak) {
			monster.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
		return didBreak;
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		
		damage.multiplyKnockback(0.75);

		if (poison > 0) {
			damage.getDwarf().givePotionEffect(PotionEffectType.POISON, 40, poison+4, true, false, true);
		}
		if (sabotage > 0) {
			damage.getDwarf().givePotionEffect(PotionEffectType.UNLUCK, 100, poison, true, false, true);
		}
		if (assa && monster.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			monster.playSound("entity.wither.shoot", 1f, 2f, true);
			damage.getMultiPartDamage().addBoost(60);
		}
		monster.removePotionEffect(PotionEffectType.INVISIBILITY);
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
