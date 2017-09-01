package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 9/07/17.
 */
class Wraith extends AbstractMob {
	Wraith(MonsterPlayer monster) {
		super(monster, MobType.WRAITH);
	}
	
	
	private static final int MAX_CHARGE_CD = 80;
	private static final int CLOUD_TIME = 30;
	private static final int FLOAT_TIME = 40;
	private int chargerCD;
	private boolean chargeActive = false;
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		setFloatiness();
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 25);
		
		FlagWatcher watch = getDisguise().getWatcher();
		watch.setItemInMainHand(new ItemStack(Material.AIR));
		if (watch instanceof SkeletonWatcher) {
			((SkeletonWatcher) watch).setSwingArms(true);
		} else {
			Bukkit.getLogger().severe("Wraith not disguised as skeletal mob?!");
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		setFloatiness(sneaking);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace face) {
		if (!Misc.isRightClick(action)) return;
		if (!isPlayerHoldingWeapon()) return;
		
		if (chargerCD == 0) {
			chargerCD = MAX_CHARGE_CD;
			chargeActive = true;
			charge();
		}
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float) chargerCD/MAX_CHARGE_CD;
	}
	
	@Override
	public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
		if (chargerCD > 0) {
			chargerCD--;
			
			if (chargeActive && chargerCD >= MAX_CHARGE_CD - CLOUD_TIME) {
				Location loc = monster.getLocation();
				loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 30, 0.7, 0.7, 0.7, 0.03);
				aoeDamage();
			}
			
			if (chargeActive && chargerCD < MAX_CHARGE_CD - FLOAT_TIME) {
				chargeActive = false;
				setFloatiness();
			}
		}
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		if (chargeActive) {
			chargeActive = false;
			setFloatiness();
		}
		return super.onGotHit(dwarf, type, damage);
	}
	
	//private final ComplexCooldown charger = new ComplexCooldown(40, this::charge,  this::setFloatiness);
	
	private void setFloatiness() {
		setFloatiness(monster.getPlayer().isSneaking());
	}
	
	private void setFloatiness(boolean sneaking) {
		chargeActive = false;
		
		if (sneaking) {
			monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -8);
		} else {
			monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -1);
		}
	}
	
	private void charge() {
		double yaw = monster.getPlayer().getLocation().getYaw();
		double radYaw = yaw*Math.PI/180;
		Vector velocity = new Vector(-3 * Math.sin(radYaw), -3, 3 * Math.cos(radYaw));
		monster.setVelocity(velocity);
		monster.givePotionEffect(PotionEffectType.LEVITATION, FLOAT_TIME, 7, true, false, true);
		
		monster.playSound("entity.ghast.hurt", 2f, 0.7f, true);
		monster.playSound("entity.ghast.shoot", 1f, 0.5f, true);
	}
	
	private static final double AOE_RADIUS = 3.5;
	private static final int AOE_DMG = 40; // This is a one off hit so its not as strong as it seems.
	private static final int AOE_SHRED = 25;
	private void aoeDamage() {
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (dwarf.distanceTo(monster) <= AOE_RADIUS) {
				if (dwarf.getPlayer().getNoDamageTicks() == 0)
					dwarf.getArmour().damage(AOE_SHRED);
				dwarf.customDamage(dwarf, DamageType.TEMPORARY, AOE_DMG);
			}
		}
	}
	
	@Override
	public void onDeath() {
		monster.playSound("entity.ghast.death", 2f, 0.5f, true);
	}
}
