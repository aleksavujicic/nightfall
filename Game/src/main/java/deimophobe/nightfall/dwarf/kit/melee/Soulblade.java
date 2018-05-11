package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.util.Util;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Soulblade extends AbstractItem implements CooldownPiece {

	private final Cooldown soulShatterCD = new UseCooldown(10, this::soulShatter);

	private static final double SOUL_SHATTER_RADIUS = 4;
	private static final int MAX_SOULS = 50;

	private double souls = 0;

	public Soulblade(Dwarf dwarf){
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "soulblade");

	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	}

	@Override
	public void update() {
		super.update();
		soulShatterCD.update();
		
		if (isHoldingItem()) {
			showParticle();
		}
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isMeleeDamageFromItem(damage)) {
			if (damage.getReceiver().isAI()) {
				souls += 0.5;
			} else {
				souls += 0.75;
			}
		}
		soulCheck();
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (isHoldingItem()) {
			double resistance = soulScaling(0, 0.3);
			damage.getMultiPartDamage().timesMult(1 - resistance);
		}
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isRightClick() && !dwarf.getNoSpecial()) {
			if (souls >= 10) {
				soulShatterCD.tryUse();
			}
		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		return Math.min(1, (float) souls/MAX_SOULS);
	}
	
	
//	@Override
//	public void onShift(boolean sneaking) {
//		super.onShift(sneaking);
//		souls++;
//		soulCheck();
//	}
	
	
	private void soulCheck() {
		if (souls > MAX_SOULS) {
			souls = MAX_SOULS;
		}
	}
	
	private double soulScaling(double min, double max) {
		return min + (max - min) * souls/MAX_SOULS;
	}
	
	private void soulShatter() {
		Location center = dwarf.getEyeLocation();
		center.add(center.getDirection());
		
		World world = center.getWorld();
		world.spawnParticle(Particle.DRAGON_BREATH, center, (int) soulScaling(50, 250), 0.5, 0.1, 0.5, 0.05 + 0.005*souls);
		world.spawnParticle(Particle.SMOKE_NORMAL, center, (int) soulScaling(100, 500), 1.5, 1,  1.5, 0.003);
		
		float pitch = (float) soulScaling(0.5, 0.9);
		world.playSound(center,"dash", 1f, pitch);
		world.playSound(center,"entity.generic.burn", 1f, pitch);
		
		double kb = soulScaling(1, 3);
		double baseDamage = soulScaling(25, 150);
		
		souls = 0;
		
		for (MonsterEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
			Vector offset = entity.getEyeLocation().subtract(center).toVector();
			if (offset.length() > SOUL_SHATTER_RADIUS) continue;

			Vector knockback = offset.normalize().multiply(kb / Math.sqrt(Math.max(2, offset.length())));
			knockback.setY(knockback.getY() / 2 + 0.1);

			MonsterDamage mDamage = entity.createDamage(dwarf, GameDamageType.SOUL_SHATTER, baseDamage);
			if (entity.isAI()) mDamage.instaKill();
			mDamage.setKnockback(knockback);
			mDamage.fire(true);
		}
	}
	
	
	private static final double TWOPI = 2*Math.PI;
	
	private double polar = 0;
	private double azimuthal = 0;
	
	private void showParticle() {
		double velocity = soulScaling(0.1, 0.2);
		polar     = (polar     + velocity) % TWOPI;
		azimuthal = (azimuthal + velocity*velocity) % TWOPI;
		
		
		int numParticles = (int) soulScaling(0, 15);
		Location center = dwarf.getEyeLocation().subtract(0, 0.5, 0);
		for (int i=0; i<numParticles; i++) {
			double particlePolar = polar     + i;
			double particleAzi   = azimuthal + i;
			particlePolar = particlePolar % TWOPI;
			particleAzi   = particleAzi   % TWOPI;
			
			//particlePolar = (particlePolar <= Math.PI ? particlePolar : TWOPI - particlePolar);
			
			Location particleLocation = Util.getSphericalPosition(center, 1.5, particlePolar, particleAzi);
			particleLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 0, 0.75, 0.1, 0.8);
		}
	}
}
