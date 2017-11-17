package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class ScepterOfMagma extends AbstractItem implements KitCooldownElement {
	public ScepterOfMagma(Dwarf dwarf) { super(dwarf); }

	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "scepter");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public ItemStack getCooldownToggleItem() { return ITEM.createItemStack(); }
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	private static final Consumer<Location> PARTICLE_PLACER =
			(location) -> location.getWorld().spawnParticle(Particle.DRAGON_BREATH, location, 2, 0.07, 0.07, 0.07, 0);
	
	
	private final ComplexCooldown cd = new ComplexCooldown(1*20);
	private final ComplexCooldown bpcd = new ComplexCooldown(10*20);
	

	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		cd.update();
		bpcd.update();
		if (sec){
			if(dwarf.getMana() >= 800){
//				cd.setMaxCD(5*20);
				fireRing = false;
			}
			else if (dwarf.getMana() < 800 && dwarf.getMana() >= 600){
//				cd.setMaxCD(4*20);
				fireRing = false;
			}
			else if(dwarf.getMana() < 600 && dwarf.getMana() >= 400){
//				cd.setMaxCD(3*20);
				fireRing = false;
			}
			else if(dwarf.getMana() < 400 && dwarf.getMana() >= 200){
//				cd.setMaxCD(2*20);
				fireRing = true;
			}
			else if(dwarf.getMana() < 200){
//				cd.setMaxCD(1*20);
				fireRing = true;
			}
		}
		if (isHoldingItem()) {
			if (fireRing) {
				//To play the particle effect
				spawnParticles();
			}
		}
	}
	
	
	private final int NUM_PARTICLES = 4;
	private final double PARTICLE_DPT = 2;
	private final double PARTICLE_INFLUENCE = 1;
	private double theta = 0;

	private void spawnParticles() {
		//UHHHHMMMM THE THING WHERE THE FIRE GOES WOOSHY WOOSH
		theta = (theta + 0.1) % (2 * Math.PI);

		Location playerLoc = dwarf.getPlayer().getEyeLocation();

		for (int i = 0; i < NUM_PARTICLES; i++) {
			double frac = (double) i / NUM_PARTICLES;
			double myTheta = theta - frac * 2 * Math.PI;

			Location particleLoc = playerLoc.clone().add(Math.cos(myTheta), -1, Math.sin(myTheta));
			particleLoc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 2, 0,0,0,0);

			for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
				Location monsterLoc = monster.getEyeLocation().subtract(0, 1, 0);

				if (monsterLoc.distance(particleLoc) <= PARTICLE_INFLUENCE) {
					GameDamage damage = monster.createDamage(dwarf, CustomDamageType.SCEPTER_OF_MAGMA, PARTICLE_DPT *1);
					damage.setNoDmgTicks(2);
					damage.fire(true);
				}
			}
		}
	}

	private static final double THICKNESS = 1.5;
	private static final double MIN_DISTANCE_FROM_SHOOTER = 1;

	private void createInferno() {
		
		GamePlayer.GameEntityDamager<MonsterEntity> damager = dwarf.new GameEntityDamager<MonsterEntity>(CustomDamageType.SCEPTER_OF_MAGMA, 12);
		dwarf.fireBeam(10, 1.25, 0.25, PARTICLE_PLACER, null, damager);

	
		/*
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			// Dont give buff to self
			if (dwarf == this.dwarf) continue;

			// Skip if further than distance shot or too close
			Location dwarfLoc = dwarf.getEyeLocation();
			double distance = location.distance(dwarfLoc);
			if (MIN_DISTANCE_FROM_SHOOTER <= distance && distance <= range) {
				// Find if close enough to beam
				Vector monsterOffset = dwarfLoc.clone().subtract(location).toVector();
				Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
				double radialOffset = radialPostion.subtract(monsterOffset).length();

				// If close enough to give dwarf proc
				if (radialOffset <= THICKNESS) {
					if(dwarf.hasPotionEffect(PotionEffectType.BLINDNESS)){
						dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION,10*20, 3,true,true,true);
					}
					if(dwarf.getMana() < 300){
						dwarf.regenMana(50);
					}
					else{
						dwarf.givePotionEffect(PotionEffectType.INCREASE_DAMAGE,5*20,1,true,true,true);
					}
				}
			}
		}
		*/

	}

	private static final int BUFFPOOL_LIFE = 30*20;
	private static final int BUFFPOOL_DELAY = 1;
	private static final double BUFFPOOL_RADIUS = 3.5;

	private void createBuffPool(){
		Location spawnLoc = dwarf.getEyeLocation().add(0,-1.25,0);

		new BuffPool(spawnLoc, new Vector());
	}

	private class BuffPool{
		private int buffleft = BUFFPOOL_LIFE;

		private BuffPool(Location position, Vector velocity){
			new BukkitRunnable() {
				@Override
				public void run() {
					buffleft -= BUFFPOOL_DELAY;

					position.add(velocity);

					// BuffPool particles
					position.getWorld().spawnParticle(Particle.DRAGON_BREATH, position, 15, 0.5, 0, 0.5, 0);

					// Buff Dwarves
					for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
						if (dwarf.getEyeLocation().distance(position) <= BUFFPOOL_RADIUS) {

							if(dwarf.hasPotionEffect(PotionEffectType.BLINDNESS)){
								dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION,10*20, 3,true,true,true);
							}
							if(dwarf.getMana() < 300){
								dwarf.regenMana(50);
							}
							else{
								dwarf.givePotionEffect(PotionEffectType.INCREASE_DAMAGE,5*20,1,true,true,true);
							}

						}
					}

					if (buffleft <= 0) this.cancel();
				}
			}.runTaskTimer(NightfallPlugin.getPlugin(), 0, BUFFPOOL_DELAY);
		}
	}

	private boolean fireRing = false;
	public boolean onUse (Action action, Block clickedBlock, BlockFace face){
		if (Misc.isRightClick(action) && bpcd.tryUse()){
			createBuffPool();
		}
		else if (Misc.isLeftClick(action) && cd.tryUse()){
			createInferno();
		}
		return true;
	}

	@Override
	public float fractionComplete() {
		return bpcd.fractionComplete();
	}
}

//	private static final int INFERNO_LIFE = 60;
//	private static final int INFERNO_DELAY = 4;
//	private static final double INFERNO_RADIUS = 2;
//	private static final double INFERNO_VELOCITY = 0.6;
//	private static final double INFERNO_DPT = 10; // Damage per tick
//
//	private void createInferno() {
//		Location spawnLoc = dwarf.getEyeLocation();
//		Vector looking = spawnLoc.getDirection();
//
//		looking.normalize().multiply(INFERNO_VELOCITY);
//		looking.add(dwarf.getVelocity().setY(0));
//		spawnLoc.add(looking.clone().multiply(3));
//
//		dwarf.playSound("foosh", 1, 1, true);
//		dwarf.playSound("entity.generic.burn", 1f, 0.5f, true);
//		dwarf.playSound("entity.ghast.shoot", 1f, 0.5f, true);
//
//		new Inferno(spawnLoc, looking);
//	}
//
//	private class Inferno {
//		private int lifeLeft = INFERNO_LIFE;
//
//		private Inferno(Location position, Vector velocity) {
//			new BukkitRunnable() {
//				@Override
//				public void run() {
//					lifeLeft -= INFERNO_DELAY;
//
//					position.add(velocity);
//
//					// Flame particles
//					position.getWorld().spawnParticle(Particle.FLAME, position, 50, 0.5, 0.5, 0.5, .05);
//
//					// Damage mobs
//					for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
//						if (monster.getEyeLocation().distance(position) <= INFERNO_RADIUS) {
//							GameDamage damage = monster.createDamage(dwarf, CustomDamageType.SCEPTER_OF_MAGMA, INFERNO_DPT * INFERNO_DELAY);
//							damage.setNoDmgTicks(9);
//							damage.fire(true);
//						}
//					}
//
//					if (lifeLeft <= 0) this.cancel();
//				}
//			}.runTaskTimer(NightfallPlugin.getPlugin(), 0, INFERNO_DELAY);
//
//		}
//	}