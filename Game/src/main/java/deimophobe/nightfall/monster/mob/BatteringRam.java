package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.GhastWatcher;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 15/01/18.
 */
public class BatteringRam extends AbstractRideableMob {
	@Update private final Cooldown ram = new UseCooldown(2*20, this::wallRam);
	@Update @Display private final Cooldown toggleCooldown = new UseCooldown(4*20, this::toggleMoveState);
	@Update private final Cooldown faceResetter = new ComplexCooldown(10, null, () -> setFace(false));
	
	private Location lastLocation;
	private boolean moveState;
	
	protected BatteringRam(MonsterPlayer monster) {
		super(monster, MobType.BATTERING_RAM);
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
		
		moveState = false;
		toggleMoveState();
	}
	
	@Override
	protected void setupItems() {
		super.setupItems();
		giveItem("toggle");
	}
	
	@Override
	public void update() {
		super.update();
		
		if (everyNthTick(20)) {
			if (monster.distanceTo(lastLocation) >= 1.5) {
				monster.playSound("entity.zombie.infect", 1f, 0.5f, true);
				if (everyNthTick(80)) monster.playSound("entity.minecart.inside", 1f, 0.5f, true);
			}
			lastLocation = monster.getLocation();
		}
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (isPlayerHoldingWeapon()) {
			if (!moveState) ram.tryUse();
		} else if (isPlayerHoldingItem("toggle")) {
			toggleCooldown.tryUse();
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
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (sneaking) toggleCooldown.tryUse();
	}
	
	private void wallRam() {
		Block center = monster.getTargetBlock(null, 3);
		BlockConverter.convert(BlockConverter.Type.EXPLOSION, center.getLocation(), 8);
		monster.playSound("entity.generic.explode", 2f, 0.5f, true);
		monster.playSound("entity.zombie.attack_door_wood", 2f, 0.5f, true);
		monster.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, center.getLocation(), 3, 1, 1,1);
		
		setFace(true);
		faceResetter.reset();
	}
	
	private void setFace(boolean angry) {
		changeDisguiseWatcher(GhastWatcher.class, (gw) -> gw.setAggressive(angry));
	}
	
	
	@Override
	protected boolean canMount(MonsterPlayer player) {
		int numPassengers = monster.getPlayer().getPassengers().size();
		return (numPassengers < 1);
	}
	
	private void toggleMoveState() {
		moveState = !moveState;
		if (moveState) {
			monster.removePotionEffect(PotionEffectType.JUMP);
			monster.removePotionEffect(PotionEffectType.SLOW);
		} else {
			monster.givePermanentPotionEffect(PotionEffectType.SLOW, 10);
			monster.givePermanentPotionEffect(PotionEffectType.JUMP, -5);
		}
	}
}
