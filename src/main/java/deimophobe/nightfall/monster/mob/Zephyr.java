package deimophobe.nightfall.monster.mob;

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
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 27/03/18.
 */
class Zephyr extends AbstractMob implements FloatyMob {
	protected Zephyr(MonsterPlayer monster) { super(monster, MobType.ZEPHYR); }
	
	@Display @Update
	private final ComplexCooldown ability = new ComplexCooldown(20, this::implosion);
	
	private static final int SLIME_SIZE = 2;
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		setFloatiness();
		
		changeDisguiseWatcher(SlimeWatcher.class, sw -> sw.setSize(SLIME_SIZE));
		changeDisguise(MobDisguise.class, disguise -> {
			disguise.setReplaceSounds(false);
			disguise.setHearSelfDisguise(false);
		});
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		spawnParticles();
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
			ability.tryUse();
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getType() == GameDamageType.MELEE) {
			damage.multiplyKnockback(4,2);
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		setFloatiness(sneaking);
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(MagmaCube.class, magmaCube -> {
			magmaCube.setSize(SLIME_SIZE);
		});
	}
	
	@Override
	public void resetFloatiness() {
		setFloatiness();
	}
	
	private void setFloatiness() {
		setFloatiness(monster.getPlayer().isSneaking());
	}
	
	private void setFloatiness(boolean sneaking) {
		if (sneaking) {
			monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -6);
		} else {
			monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -2);
		}
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 15);
	}
	
	
	private static final double PARTICLE_VISIBLE_RADIUS = 50;
	private void spawnParticles() {
		Location location = monster.getLocation().subtract(0, 0.4, 0);
		World world = monster.getWorld();
		
		boolean spawnSmoke = (Math.random() <= 0.2);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (location.distance(player.getLocation()) <= PARTICLE_VISIBLE_RADIUS) {
				world.spawnParticle(Particle.CLOUD, location, 2, 0.5, 0.5, 0.5, 0.1);
				if (spawnSmoke) world.spawnParticle(Particle.SMOKE_LARGE, location, 1, 0.5, 0.5, 0.5, 0.1);
			}
		}
	}
	
	private static final double AOE_RANGE = 12;
	private void implosion() {
		Location location = monster.getLocation();
		World world = monster.getWorld();
		world.spawnParticle(Particle.CLOUD, location, 30, 0.5, 0.5, 0.5, 0.5);
		world.spawnParticle(Particle.EXPLOSION_LARGE, location, 2, 0.1, 0.1, 0.1);
		
		playSound("implode");
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			double distance = dwarf.distanceTo(monster);
			if (distance > AOE_RANGE) continue;
			
			double damageAmt = 40 - distance;
			Vector kb = monster.offsetFrom(dwarf);
			kb.normalize().multiply(2 - distance/AOE_RANGE);
			
			DwarfDamage damage = dwarf.createDamage(monster, GameDamageType.TEMPORARY, damageAmt);
			damage.setArmourShred(20);
			damage.setKnockback(kb);
			damage.fire();
		}
	}
}
