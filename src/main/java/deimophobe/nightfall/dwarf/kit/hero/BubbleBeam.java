package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.MultiEventCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.util.LifetimeObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class BubbleBeam extends AbstractItem implements CooldownPiece {
	
	public BubbleBeam(Dwarf dwarf) {
		super(dwarf);
		beamer.addEvent(7, this::shootBubble);
		beamer.addEvent(4, this::shootBubble);
	}
	
	private final MultiEventCooldown beamer = new MultiEventCooldown(10, this::shootBubble);
	private final ComplexCooldown geyserCD = new ComplexCooldown(120*20, this::geyser);
	private final ComplexCooldown fallImmunity = new ComplexCooldown(3*20);
	private Whirlpool whirlpool = null;
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero","bubblebeam");
	private final static double DAMAGE = 10;
	static { ITEM.addModifier(ItemModifierType.ATTACK, (int) DAMAGE); }
	
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		beamer.update();
		geyserCD.update();
		fallImmunity.update();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		
		if (Misc.isLeftClick(action)) {
			return beamer.tryUse();
		} else {
			return geyserCD.tryUse();
		}
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromItem(damage)) {
			damage.cancel();
		}
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() == GameDamageType.FALL) {
			if (!fallImmunity.isAvailable() || whirlpool != null)
				damage.cancel();
		}
	}
	
	private static final Consumer<Location> PARTICLE_PLACER = (location) -> {
		location.getWorld().spawnParticle(Particle.WATER_BUBBLE, location, 7, 0.05, 0.05, 0.05, 0);
		location.getWorld().spawnParticle(Particle.CRIT_MAGIC, location, 1, 0.05, 0.05, 0.05, 0);
	};
	
	private void shootBubble() {
		double offsetPerp = Misc.randomDouble(-0.5, 0.5);
		double offsetY = Misc.randomDouble(-0.5, 0.5);
		
		Consumer<MonsterEntity> monsterDamager = dwarf.new SingleEntityConsumer<MonsterEntity>(0) {
			@Override
			public void onHit(MonsterEntity monster) {
				double damageAmt = DAMAGE + dwarf.getBonusMeleeDamage() / 2;
				if (monster.isUnderwater()) damageAmt *= 1.5;
				
				MonsterDamage damage = monster.createDamage(dwarf, GameDamageType.BUBBLE_BEAM, damageAmt);
				if (dwarf.hasProc()) damage.setProc(true);
				damage.setNoDmgTicks(1);
				damage.fire(true);
			}
		};
		
		dwarf.fireParticle(0.5, 15, 1, offsetPerp, offsetY, 0.2, PARTICLE_PLACER, null, monsterDamager);
		dwarf.playSound("entity.player.hurt_drown", 0.8f, 1.5f, true);
	}
	
	
	
	
	private void geyser() {
		dwarf.leap(0, 1.5);
		new BukkitRunnable() {
			@Override
			public void run() {
				whirlpool = new Whirlpool();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 10);
	}
	
	@Override
	public float getCooldown() {
		return geyserCD.getCooldown();
	}
	
	private class Whirlpool extends LifetimeObject {
		
		private final Location floatLoc;
		private final Location midLoc;
		private static final double halfHeight = 6;
		
		private Whirlpool() {
			super(10*20, 1);
			
			floatLoc = dwarf.guessClientSideLocation();
			midLoc = floatLoc.clone().subtract(0, halfHeight, 0);
		}
		
		@Override
		public void run() {
			super.run();
			
			World world = floatLoc.getWorld();
			world.spawnParticle(Particle.BLOCK_CRACK, floatLoc, 3, 1.5, 0.3, 1.5, 0, new MaterialData(Material.LAPIS_BLOCK));
			world.spawnParticle(Particle.BLOCK_CRACK, floatLoc, 5, 1.5, 0.3, 1.5, 0, new MaterialData(Material.STATIONARY_WATER));
			world.spawnParticle(Particle.BLOCK_CRACK, floatLoc, 30, 1.5, 0.3, 1.5, 0, new MaterialData(Material.CONCRETE, (byte) 3));
			world.spawnParticle(Particle.BLOCK_CRACK, floatLoc, 50, 1.5, 0.3, 1.5, 0, new MaterialData(Material.CONCRETE_POWDER, (byte) 3));
			world.spawnParticle(Particle.CLOUD, floatLoc, 2, 1.5, 0.3, 1.5, 0);
			
			world.spawnParticle(Particle.BLOCK_CRACK, midLoc, 5, 0.2, halfHeight/2, 0.2, 0, new MaterialData(Material.STATIONARY_WATER));
			world.spawnParticle(Particle.BLOCK_CRACK, midLoc, 10, 0.2, halfHeight/2, 0.2, 0, new MaterialData(Material.CONCRETE, (byte) 3));
			world.spawnParticle(Particle.BLOCK_CRACK, midLoc, 20, 0.2, halfHeight/2, 0.2, 0, new MaterialData(Material.CONCRETE_POWDER, (byte) 3));
			
			float pitch = (float) Misc.randomDouble(0.5,2);
			if (getLifeLeft() % 2 == 0) {
				world.playSound(midLoc, "item.bucket.fill", 1f, pitch);
			} else {
				world.playSound(midLoc, "entity.generic.swim", 1f, pitch);
			}
			if (getLifeLeft() % 5 == 0) world.playSound(midLoc, "entity.generic.splash", 1f, pitch);
			
			if (getLifeLeft() % 4 == 0) {
				for (MonsterEntity<?> monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
					if (monster.distanceTo(midLoc) >= 15) continue;

					if (monster.distanceTo(midLoc) <= 4) {
						boolean isAI = (monster instanceof AIEntity<?>);
						
						if (dwarf.isOnline())
							monster.doDamage(dwarf, GameDamageType.GEYSER, 10, true, isAI);
					}

					Vector offset = monster.offsetFrom(midLoc);
					double strength = Math.min(0.2, 1/offset.length());
					offset.normalize().multiply(-strength*2);
					monster.setVelocity(offset);
				}
			
				DwarfManager.getManager().getDwarves().forEach(this::tryLeap);
				//MonsterManager.getManager().getAliveMobsAndAIs().forEach(this::tryLeap);
			}
		}
		
		@Override
		public synchronized void cancel() throws IllegalStateException {
			super.cancel();
			BubbleBeam.this.whirlpool = null;
			fallImmunity.reset();
		}
		
		private void tryLeap(GameEntity<?> entity) {
			if (containsEntity(entity)) {
				entity.leap(0, 0.8);
			}
		}
		
		private boolean containsEntity(GameEntity<?> entity) {
			Location location;
			if (entity instanceof GamePlayer) {
				location = ((GamePlayer) entity).guessClientSideLocation();
			} else {
				location = entity.getLocation();
			}
			location.subtract(midLoc);
			
			
			double x = location.getX();
			double y = location.getY();
			double z = location.getZ();
			
			return (-halfHeight <= y && y <= halfHeight && (x*x + z*z <= 9));
		}
	}
}
