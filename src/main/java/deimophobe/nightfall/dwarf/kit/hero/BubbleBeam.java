package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Expirable;
import deimophobe.nightfall.cooldown.MultiEventCooldown;
import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;
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
	private Geyser geyser = null;
	
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
		
		if (geyser != null) {
			geyser.update();
			if (geyser.hasExpired()) {
				fallImmunity.reset();
				geyser = null;
			}
		}
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
		if (damage.getType() == NaturalDamageType.FALL) {
			if (!fallImmunity.isAvailable() || geyser != null)
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
				
				MonsterDamage damage = (MonsterDamage) monster.createDamage(dwarf, CustomDamageType.BUBBLE_BEAM, damageAmt);
				if (dwarf.hasProc()) damage.setProc(true);
				damage.setNoDmgTicks(1);
				damage.fire(true);
			}
		};
		
		dwarf.fireParticle(0.5, 15, 1, offsetPerp, offsetY, 0.2, PARTICLE_PLACER, null, monsterDamager);
		dwarf.playSound("entity.player.hurt_drown", 0.8f, 1.5f, true);
	}
	
	
	
	
	private void geyser() {
		geyser = new Geyser();
	}
	
	@Override
	public float getCooldown() {
		return geyserCD.getCooldown();
	}
	
	private class Geyser implements Updateable, Expirable {
		
		private static final int MAX_LIFETIME = 10*20;
		private int lifetime = MAX_LIFETIME;
		
		private Location floatLoc;
		private Location midLoc;
		private static final double halfHeight = 6;
		
		private Geyser() {
			dwarf.leap(0, 1.5);
		}
		
		private void setFloatLoc() {
			floatLoc = dwarf.guessClientSideLocation();
			midLoc = floatLoc.clone().subtract(0, halfHeight, 0);
		}
		
		@Override
		public void update() {
			lifetime--;
			
			if (lifetime == MAX_LIFETIME - 10) setFloatLoc();
			if (lifetime >= MAX_LIFETIME - 10) return;
			
			
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
			if (lifetime % 2 == 0) {
				world.playSound(midLoc, "item.bucket.fill", 1f, pitch);
			} else {
				world.playSound(midLoc, "entity.generic.swim", 1f, pitch);
			}
			if (lifetime % 5 == 0) world.playSound(midLoc, "entity.generic.splash", 1f, pitch);
			
			if (lifetime % 4 == 0) {
				for (MonsterEntity<?> monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
					if (monster.distanceTo(midLoc) >= 15) continue;

					if (monster.distanceTo(midLoc) <= 4) {
						boolean isAI = (monster instanceof AIEntity<?>);
						monster.doDamage(dwarf, CustomDamageType.GEYSER, 10, true, isAI);
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
		
		@Override
		public boolean hasExpired() {
			return lifetime <= 0;
		}
	}
}
