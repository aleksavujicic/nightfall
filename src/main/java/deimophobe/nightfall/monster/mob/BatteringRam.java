package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.GhastWatcher;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 15/01/18.
 */
public class BatteringRam extends AbstractMob {
	private final ComplexCooldown ram = new ComplexCooldown(2*20, this::wallRam);
	private final ComplexCooldown faceResetter = new ComplexCooldown(10, null, () -> setFace(false));
	
	private Location lastLocation;
	
	protected BatteringRam(MonsterPlayer monster) {
		super(monster, MobType.BATTERING_RAM);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		
		changeDisguise(MobDisguise.class, (md) -> {
			md.setReplaceSounds(false);
			md.setHearSelfDisguise(false);
		});
		
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, -5);
		lastLocation = monster.getLocation();
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		ram.update();
		faceResetter.update();
		
		if (sec) {
			if (monster.distanceTo(lastLocation) >= 1.5) {
				monster.playSound("entity.zombie.infect", 1f, 0.5f, true);
				if (quadSec) monster.playSound("entity.minecart.inside", 1f, 0.5f, true);
			}
			lastLocation = monster.getLocation();
		}
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (isPlayerHoldingWeapon())
			ram.tryUse();
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.cancel();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		if (damage.isArrow())
			damage.cancel();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (sneaking && monster.getPlayer().isOnGround()) {
			monster.leap(0.1, 0.4);
		}
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
	public float getCooldown() {
		return ram.getCooldown();
	}
}
