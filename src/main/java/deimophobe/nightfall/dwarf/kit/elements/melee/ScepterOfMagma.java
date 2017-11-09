package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ScepterOfMagma extends AbstractItem implements KitCooldownElement {
	public ScepterOfMagma(Dwarf dwarf) { super(dwarf); }

	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "scepter");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public ItemStack getCooldownToggleItem() { return ITEM.createItemStack(); }
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	private final ComplexCooldown cd = new ComplexCooldown(20*20, this::createInferno);
	

	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		cd.update();
		//To increase mana regen because weapon is mana-heavy
		if (doubleSec) {
			dwarf.regenMana(5);
		}
		if (sec){
			if(dwarf.getMana() >= 800){
				cd.setMaxCD(20*20);
			}
			else if (dwarf.getMana() < 800 && dwarf.getMana() >= 600){
				cd.setMaxCD(16*20);
			}
			else if(dwarf.getMana() < 600 && dwarf.getMana() >= 400){
				cd.setMaxCD(12*20);
			}
			else if(dwarf.getMana() < 400 && dwarf.getMana() >= 200){
				cd.setMaxCD(8*20);
			}
			else if(dwarf.getMana() < 200){
				cd.setMaxCD(4*20);
			}
		}
		if (isHoldingItem()){
			dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, 5*20, 3, true, false, true);
			if(fireRing && dwarf.hasMana(100)){
				//To play the particle effect
				spawnParticles();
			}
		}
	}
	
	
	private final int NUM_PARTICLES = 4;
	private final double PARTICLE_DPT = 3;
	private final double PARTICLE_INFLUENCE = 1;
	private double theta = 0;

	private void spawnParticles() {
		//UHHHHMMMM THE THING WHERE THE FIRE GOES WOOSHY WOOSH
		theta = (theta + 0.1) % (2 * Math.PI);
		dwarf.useMana(1);

		Location playerLoc = dwarf.getPlayer().getEyeLocation();

		for (int i = 0; i < NUM_PARTICLES; i++) {
			double frac = (double) i / NUM_PARTICLES;
			double myTheta = theta - frac * 2 * Math.PI;

			Location particleLoc = playerLoc.clone().add(Math.cos(myTheta), -1, Math.sin(myTheta));
			particleLoc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 2, 0,0,0,0);

			for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
				Location monsterLoc = monster.getEyeLocation().subtract(0, 1, 0);

				if (monsterLoc.distance(particleLoc) <= PARTICLE_INFLUENCE) {
					GameDamage damage = monster.createDamage(dwarf, CustomDamageType.SCEPTER_OF_MAGMA, PARTICLE_DPT *2);
					damage.setNoDmgTicks(2);
					damage.fire(true);
				}
			}
		}
	}

	private static final int INFERNO_LIFE = 60;
	private static final int INFERNO_DELAY = 4;
	private static final double INFERNO_RADIUS = 2;
	private static final double INFERNO_VELOCITY = 0.6;
	private static final double INFERNO_DPT = 10; // Damage per tick
	
	private void createInferno() {
		Location spawnLoc = dwarf.getEyeLocation();
		Vector looking = spawnLoc.getDirection();
		
		looking.normalize().multiply(INFERNO_VELOCITY);
		looking.add(dwarf.getVelocity().setY(0));
		spawnLoc.add(looking.clone().multiply(3));
		
		dwarf.playSound("foosh", 1, 1, true);
		dwarf.playSound("entity.generic.burn", 1f, 0.5f, true);
		dwarf.playSound("entity.ghast.shoot", 1f, 0.5f, true);
		
		new Inferno(spawnLoc, looking);
	}

	private class Inferno {
		private int lifeLeft = INFERNO_LIFE;

		private Inferno(Location position, Vector velocity) {
			new BukkitRunnable() {
				@Override
				public void run() {
					lifeLeft -= INFERNO_DELAY;

					position.add(velocity);

					// Flame particles
					position.getWorld().spawnParticle(Particle.FLAME, position, 50, 0.5, 0.5, 0.5, .05);

					// Damage mobs
					for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
						if (monster.getEyeLocation().distance(position) <= INFERNO_RADIUS) {
							GameDamage damage = monster.createDamage(dwarf, CustomDamageType.SCEPTER_OF_MAGMA, INFERNO_DPT * INFERNO_DELAY);
							damage.setNoDmgTicks(9);
							damage.fire(true);
						}
					}

					if (lifeLeft <= 0) this.cancel();
				}
			}.runTaskTimer(NightfallPlugin.getPlugin(), 0, INFERNO_DELAY);

		}
	}

	private boolean fireRing = false;
	public boolean onUse (Action action, Block clickedBlock, BlockFace face){
		if (Misc.isRightClick(action)){
			fireRing = !fireRing;
			return true;
		} else {
			return cd.tryUse();
		}
	}

	@Override
	public float fractionComplete() {
		return cd.fractionComplete();
	}
}
