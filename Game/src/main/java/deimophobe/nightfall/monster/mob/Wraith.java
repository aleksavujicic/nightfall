package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 9/07/17.
 */
public class Wraith extends AbstractMob implements FloatyMob {
	Wraith(MonsterPlayer monster) {
		super(monster, MobType.WRAITH);
	}
	
	
	private static final int MAX_CHARGE_CD = 80;
	private static final int CLOUD_TIME = 30;
	private static final int FLOAT_TIME = 40;
	private int chargerCD;
	private boolean chargeActive = false;
	
	private final Set<Dwarf> hitDwarves = new HashSet<>();
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		setFloatiness();
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 25);
		
		changeDisguiseWatcher(SkeletonWatcher.class, (sw) -> {
			sw.setItemInMainHand(getWeapon().createItemStack());
			sw.setSwingArms(true);
		});
	}
	
	@Override
	public void update() {
		super.update();
		if (chargerCD > 0) {
			chargerCD--;
			
			if (chargeActive && chargerCD >= MAX_CHARGE_CD - CLOUD_TIME) {
				spawnParticles();
				aoeDamage();
			}
			
			if (chargeActive && chargerCD < MAX_CHARGE_CD - FLOAT_TIME) {
				chargeActive = false;
				setFloatiness();
			}
		}
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		if (chargeActive) {
			damage.addPostDamageHandler(() -> {
				chargeActive = false;
				setFloatiness();
			});
		}
		switch (damage.getType()) {
			case POISON:
			case WITHER:
				damage.cancel();
				monster.removeAllPoisons();
				break;
		}
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace face) {
		super.onUse(click, clickedBlock, face);
		if (!click.isRightClick()) return;
		if (!isPlayerHoldingWeapon()) return;
		
		if (chargerCD == 0) {
			chargerCD = MAX_CHARGE_CD;
			chargeActive = true;
			charge();
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (chargerCD < MAX_CHARGE_CD - 5) {
			chargeActive = false;
			setFloatiness(sneaking);
		}
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float) chargerCD/MAX_CHARGE_CD;
	}
	
	//private final ComplexCooldown charger = new ComplexCooldown(40, this::charge,  this::setFloatiness);
	
	@Override
	public void resetFloatiness() {
		setFloatiness();
	}
	
	private void setFloatiness() {
		setFloatiness(monster.getPlayer().isSneaking());
	}
	
	private void setFloatiness(boolean sneaking) {
		if (sneaking) {
			monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -8);
		} else {
			monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -1);
		}
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 25);
	}
	
	private void charge() {
		hitDwarves.clear();
		
		Vector velocity;
		if (monster.isUnderwater()) {
			velocity = new Vector(0, 3, 0);
		} else {
			double yaw = monster.getPlayer().getLocation().getYaw();
			double radYaw = yaw*Math.PI/180;
			velocity = new Vector(-3 * Math.sin(radYaw), -3, 3 * Math.cos(radYaw));
		}
		
		monster.setVelocity(velocity);
		monster.givePotionEffect(PotionEffectType.LEVITATION, FLOAT_TIME, 7, true, false, true);
		monster.removePotionEffect(PotionEffectType.JUMP);
		monster.givePotionEffect(PotionEffectType.JUMP, 20, -1, false, true, true);
		monster.playSound("entity.ghast.hurt", 2f, 0.7f, true);
		monster.playSound("entity.ghast.shoot", 1f, 0.5f, true);
	}
	
	private static final double PARTICLE_VISIBLE_RADIUS = 50;
	private void spawnParticles() {
		Location location = monster.getLocation();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (location.distance(player.getLocation()) <= PARTICLE_VISIBLE_RADIUS) {
				player.spawnParticle(Particle.SMOKE_LARGE, location, 30, 0.7, 0.7, 0.7, 0.03);
			}
		}
	}
	
	private static final double AOE_RADIUS = 3;
	private static final int AOE_DMG = 60; // This is a one off hit so its not as strong as it seems.
	private static final int AOE_SHRED = 40;
	private void aoeDamage() {
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (monster.distanceTo(dwarf) > AOE_RADIUS) continue;
			if (hitDwarves.contains(dwarf)) continue;
			
			// Do damage
			DwarfDamage damage = dwarf.createDamage(monster, GameDamageType.WRAITH_CHARGE, AOE_DMG);
			damage.setArmourShred(AOE_SHRED);
			damage.setManaDrain(40);
			damage.setNoDamageTicks(10);
			damage.addPostDamageHandler(() -> {
				dwarf.givePotionEffect(PotionEffectType.BLINDNESS, 15, 1, false, true, true);
				dwarf.givePotionEffect(PotionEffectType.SLOW, 30, 3, false, true, true);
				dwarf.givePoison(PoisonType.WRAITH, 40);
			});
			boolean hit = damage.fire();
			
			if (hit) hitDwarves.add(dwarf);
		}
	}
}
