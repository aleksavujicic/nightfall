package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.LifetimeObject;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class Scepter extends AbstractItem implements KitCooldownElement {
	public Scepter(Dwarf dwarf) { super(dwarf); }
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "scepter");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public ItemStack getCooldownToggleItem() { return ITEM.createItemStack(); }
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	private final static double DAMAGE = 10;
	static { ITEM.addModifier(ItemModifierType.POWER, (int) DAMAGE); }
	
	
	private final ComplexCooldown lanceCD = new ComplexCooldown(10, this::shootLance);
	private final ComplexCooldown buffpoolCD = new ComplexCooldown(120*20, this::createBuffpool);
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		lanceCD.update();
		buffpoolCD.update();
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromItem(damage)) {
			damage.cancel();
		}
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace face){
		if (Misc.isRightClick(action)) {
			return buffpoolCD.tryUse();
		} else if (Misc.isLeftClick(action)) {
			return lanceCD.tryUse();
		}
		return false;
	}
	
	@Override
	public float fractionComplete() {
		return buffpoolCD.fractionComplete();
	}

	
	// ----- LANCE -----
	private static final Consumer<Location> PARTICLE_PLACER = (location) -> {
		double dx = Misc.randomDouble(-0.1,0.1);
		double dy = Misc.randomDouble(-0.1,0.1);
		double dz = Misc.randomDouble(-0.1,0.1);
		
		for (int i=0; i<2; i++)
			location.getWorld().spawnParticle(Particle.REDSTONE, location.clone().add(dx, dy, dz), 0, 0.8, 0.05, 0.9, 1);
	};
	
	private static final Consumer<Dwarf> DWARF_BUFFER = (dwarf1) ->
			dwarf1.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5*20, 1, true, false, false);
	
	private final Consumer<MonsterEntity> DAMAGER = (monster) -> {
		MonsterDamage damage = (MonsterDamage) monster.createDamage(dwarf, CustomDamageType.SCEPTER_OF_MAGMA, DAMAGE + dwarf.getBonusMeleeDamage()/2);
		if (dwarf.hasProc()) damage.setProc(true);
		damage.setNoDmgTicks(1);
		damage.fire(true);
		
		if (monster instanceof AIEntity)
			monster.givePotionEffect(PotionEffectType.SLOW, 5*20, 2, true, true, true);
	};
	
	private void shootLance() {
		dwarf.fireBeam(8, 1.25, 0.2, 0.2, PARTICLE_PLACER, DWARF_BUFFER, DAMAGER);
	}
	
	
	// ----- BUFFPOOL -----
	private Buffpool activePool;
	
	private void createBuffpool() {
		activePool = new Buffpool();
	}

	private class Buffpool extends LifetimeObject {
		
		private static final double BUFFPOOL_RADIUS = 2;
		private static final double VISIBLE_RADIUS = BUFFPOOL_RADIUS - 0.3;
		private final int NUM_PARTICLES = 6;
		
		private double theta = 0;
		private final Location location;
		
		private Buffpool() {
			super(15*20, 1);
			this.location = dwarf.getLocation().add(0, 0.5, 0);
		}
		
		@Override
		public void run() {
			super.run();
			
			// Buffpool particles
			World world = location.getWorld();
			world.spawnParticle(Particle.SPELL_WITCH, location, 3, VISIBLE_RADIUS/2, 0, VISIBLE_RADIUS/2, 0);
			for (int i = 0; i < 25; i++) {
				double dx = Misc.randomDouble(-1,1);
				double maxZ = Math.sqrt(1 - dx*dx);
				double dz = Misc.randomDouble(-maxZ, maxZ);
				
				double r = 0.2;
				double g = 0.8;
				double b = 1;
				
				Location particleLoc = location.clone().add(dx*VISIBLE_RADIUS, 0, dz*VISIBLE_RADIUS);
				world.spawnParticle(Particle.REDSTONE, particleLoc, 0, r, g, b, 1);
			}
			
			// Flame particles
			theta = (theta + 0.05) % (2 * Math.PI);
			
			for (int i = 0; i < NUM_PARTICLES; i++) {
				double frac = (double) i / NUM_PARTICLES;
				double myTheta = theta - frac * 2 * Math.PI;
				
				Vector offset = new Vector(Math.cos(myTheta), 0, Math.sin(myTheta));
				offset.multiply(VISIBLE_RADIUS);
				Location particleLoc = location.clone().add(offset);
				particleLoc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0.1,0.03,0.1,0);
			}
			
			// Buff Dwarves
			for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
				if (dwarf.getLocation().distance(location) <= BUFFPOOL_RADIUS) {
					if (getLifeLeft() % 3 == 0) dwarf.regenMana(1);
					dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, getLifeLeft(), 3,true,false,false);
					dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Math.min(20, getLifeLeft()),2,true,false,false);
					dwarf.givePotionEffect(PotionEffectType.REGENERATION, getLifeLeft(),3,true,false,false);
					dwarf.updateVisibility();
				}
			}
			
			// Damage Mobs
			if (getLifeLeft() % 5 == 0) {
				for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
					if (monster.getLocation().distance(location) <= BUFFPOOL_RADIUS) {
						GameDamage damage = monster.createDamage(dwarf, CustomDamageType.BUFFPOOL, 6);
						if (monster instanceof AIEntity) damage.instaKill();
						damage.setNoDmgTicks(1);
						damage.fire(true);
					}
				}
			}
		}
		
		@Override
		public synchronized void cancel() throws IllegalStateException {
			super.cancel();
			activePool = null;
		}
	}
	
	@Override
	public void notifyDeath(Dwarf deadDwarf) {
		super.notifyDeath(deadDwarf);
		if (deadDwarf == dwarf && activePool != null) {
			activePool.cancel();
		}
	}
}