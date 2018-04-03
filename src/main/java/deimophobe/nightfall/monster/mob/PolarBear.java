package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.MultipleCooldown;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.DwarfEntity;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

final class PolarBear extends AbstractMob {

	protected PolarBear(MonsterPlayer monster){
		super (monster, MobType.POLARBEAR);
	}

	private ComplexCooldown regenCD = new ComplexCooldown(10*20,this::regenAmmo);
	private ComplexCooldown frostBreath = new ComplexCooldown(10,this::breatheFrost);
	private final MultipleCooldown pounceCD = new MultipleCooldown(30*20, 10*20, this::polarPounce, null);
	private final int FULL_AMMO = 16;

	private final Set<Frost> frosts = new HashSet<>();
	private final Set<DwarfEntity> frostedDwarf = new HashSet<>();

	private int currentAmmo = FULL_AMMO;

	@Override
	public void onSpawn(SpawnMethod spawnMethod){
		super.onSpawn(spawnMethod);
		giveItem("frost-ammo",FULL_AMMO);
	}

	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		regenCD.update();
		frostBreath.update();
		pounceCD.update();
		processFrost();
		if(currentAmmo < FULL_AMMO){
			if (regenCD.isAvailable()) {
				regenCD.tryUse();
			}
		}
	}

	@Override
	public void onShift(boolean sneak){
		super.onShift(sneak);
		pounceCD.tryUse();
	}

	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isRightClick(action) && isPlayerHoldingItem("frost-ammo")) {
			frostBreath.tryUse();
		}
	}

	private void polarPounce(){
		monster.leap(1,1);
	}

	private void regenAmmo(){
		currentAmmo += 5;
		giveItem("frost-ammo",5);
	}

	private void breatheFrost(){
		monster.useHeldItem();
		currentAmmo --;
		frosts.add(new Frost());
	}


	private void processFrost() {
		frostedDwarf.clear();

		Iterator<Frost> iterator = frosts.iterator();
		while (iterator.hasNext()) {
			Frost frost = iterator.next();
			frost.update();

			if (frost.isDead()) iterator.remove();
		}
	}


	private class Frost {

		private final Vector velocity;
		private Location location;

		private int life;

		private static final double FROST_VELOCITY = 0.3;
		private static final int FROST_LIFE = 40;

		private Frost() {
			Location spawnLoc = monster.getEyeLocation();
			Misc.moveLocation(spawnLoc, 0, 0.3, -0.3);

			Vector velocity = spawnLoc.getDirection();
			velocity.normalize().multiply(Frost.FROST_VELOCITY);
			velocity.add(monster.getVelocity().setY(0));

			spawnLoc.add(velocity.clone().multiply(2));

			this.location = spawnLoc;
			this.velocity = velocity;

			this.life = FROST_LIFE;
		}


		public void update() {
			if (location.getBlock().getType().isSolid()) {
				life = 0;
				return;
			}

			double frac = (double) life / FROST_LIFE;
			double radius = 2.5 - 0.5*frac;
			double visibleRadius = 0.75 - 0.5*frac;
			double damageAmt = frac*2 + 1;

			// Frost particles
			World world = location.getWorld();
			world.spawnParticle(Particle.FIREWORKS_SPARK, location, (int) (frac*6 + 2), visibleRadius, visibleRadius, visibleRadius, 0);

			// Damage dwarves
			for (DwarfEntity dwarf : DwarfManager.getManager().getDwarves()) {
				// Only allows one frost to hit a dwarf per tick
				if (frostedDwarf.contains(dwarf)) continue;

				if (dwarf.getEyeLocation().distance(location) <= radius) {
					GameDamage damage = dwarf.createDamage(monster, GameDamageType.FROST_BREATH, damageAmt);
					damage.setNoDamageTicks(1);
					damage.fire();

					frostedDwarf.add(dwarf);
				}
			}

			location.add(velocity);
			life--;
		}

		private boolean isDead() {
			return life <= 0;
		}
	}

	@Override
	public float getCooldown() {
		return pounceCD.getCooldown();
	}

}
