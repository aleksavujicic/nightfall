package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.GhastWatcher;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 15/01/18.
 */
public class BatteringRam extends AbstractRideableMob {
	private static final int DURATION = 2*20;
	
	@Update @Display @Interact(click = ClickType.LEFT)
	private final Cooldown ram = new UseCooldown(2*20, this::wallRam);
	@Update
	private final Cooldown faceResetter = new CompletionCooldown(10, () -> setFace(false));
	
	private Location lastLocation;
	
	private final ArmorStand carryingArmourStand;
	
	protected BatteringRam(MonsterPlayer monster) {
		super(monster, MobType.BATTERING_RAM);
		
		World world = monster.getWorld();
		Location location = monster.getLocation();
		carryingArmourStand = world.spawn(location, ArmorStand.class, armorStand -> {
			armorStand.setInvulnerable(true);
			armorStand.setGravity(false);
			armorStand.setVisible(false);
			armorStand.setBasePlate(false);
		});
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		
		changeDisguise(MobDisguise.class, md -> {
			md.setReplaceSounds(false);
			md.setHearSelfDisguise(false);
		});
		
		//monster.givePermanentPotionEffect(PotionEffectType.JUMP, -5);
		lastLocation = monster.getLocation();
		
		
		monster.getPlayer().setGameMode(GameMode.ADVENTURE);
	}
	
	@Override
	public void update() {
		super.update();
		updateCarryingArmourStand();
		
		if (everyNthTick(20)) {
			if (monster.distanceTo(lastLocation) >= 1.5) {
//				monster.playSound("entity.zombie.infect", 1f, 0.5f, true);
//				if (everyNthTick(80)) monster.playSound("entity.minecart.inside", 1f, 0.5f, true);
			}
			lastLocation = monster.getLocation();
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.cancel();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		
		if (damage.getType().isArrow()) {
			damage.cancel();
		}
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		return false;
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		carryingArmourStand.remove();
	}
	
	@Override
	protected boolean canMount(MonsterPlayer player) {
		int numPassengers = carryingArmourStand.getPassengers().size();
		return (numPassengers < 1);
	}
	
	@Override
	protected Entity getCarryingEntity() {
		return carryingArmourStand;
	}
	
	private void wallRam() {
		Block center = monster.getTargetBlock(null, 3);
		BlockConverter.convert(BlockConverter.Type.EXPLOSION, center.getLocation(), 8);
		monster.playSound("entity.generic.explode", 2f, 0.5f, true);
		monster.playSound("entity.zombie.attack_door_wood", 2f, 0.5f, true);
		monster.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, center.getLocation(), 3, 1, 1,1);
		
		monster.givePotionEffect(PotionEffectType.SLOW, DURATION, 10, true, false, true);
		monster.givePotionEffect(PotionEffectType.JUMP, DURATION, -5, true, false, true);
		
		monster.setVelocity(0, -1, 0);
		
		setFace(true);
		faceResetter.reset();
	}
	
	private void setFace(boolean angry) {
		changeDisguiseWatcher(GhastWatcher.class, (gw) -> gw.setAggressive(angry));
	}
	
	private void updateCarryingArmourStand() {
		Location location = monster.getLocation().add(0, 2.7, 0);
		NMSUtil.teleportEntityWithPassenger(carryingArmourStand, location);
	}
}
