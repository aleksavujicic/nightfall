package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.VariableRepeaterCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.PI;
import static java.lang.Math.floorDiv;

public class Soulblade extends AbstractItem implements CooldownPiece {
	private static final int MAX_SOULS = 50;
	private static final int SHATTER_DELAY = 10;
	private static final int SHATTER_PULSE_COST = 5;
	
	private static final double SHATTER_RANGE = 6;
	private static final double SHATTER_DAMAGE = 20;

	private double souls = 0;
	private boolean shattering = false;
	private final Cooldown shatterDelay = new VariableRepeaterCooldown(SHATTER_DELAY, this::soulShatterTick, () -> shattering);

	public Soulblade(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "soulblade");

	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getPickupType() {
		return PickupType.SWORD;
	}

	@Override
	public void update() {
		super.update();
		shatterDelay.update();
		
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
		if (shattering && damage.getAttacker() instanceof AIEntity) {
			damage.cancel();
			return;
		}
		
		if (isHoldingItem()) {
			double resistance = soulScaling(0, 0.3);
			damage.getMultiPartDamage().timesMult(1 - resistance);
		}
	}
	
	@Override
	public boolean onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		if (click.isRightClick() && !dwarf.getNoSpecial() && souls > 10) {
			startShatter();
		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		return Math.min(1, (float) souls/MAX_SOULS);
	}
	
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		
		if (dwarf.isDebugMode()) {
			souls++;
			soulCheck();
		}
	}
	
	
	private void soulCheck() {
		if (souls > MAX_SOULS) {
			souls = MAX_SOULS;
		}
	}
	
	private double soulScaling(double min, double max) {
		return min + (max - min) * souls/MAX_SOULS;
	}
	
	private void startShatter() {
		dwarf.playSound(Sound.ENTITY_GHAST_SCREAM, 1f, 0.5f, true);
		dwarf.playSound(Sound.ENTITY_GHAST_WARN, 1f, 0.5f, true);
		
		int timesUsed = (int) Math.ceil(souls/SHATTER_PULSE_COST);
		int duration = timesUsed * SHATTER_DELAY;
		dwarf.givePotionEffect(PotionEffectType.SPEED, duration + 20, 2, true, false, true);
		
		shattering = true;
		shatterDelay.tryUse();
	}
	
	private static final Particle.DustOptions DUST_OPTIONS = new Particle.DustOptions(
			Color.fromRGB(240, 35, 200), 1
	);
	private void soulShatterTick() {
		final double soulsUsed = Math.min(souls, SHATTER_PULSE_COST);
		souls -= soulsUsed;
		final double shatterStrength = soulsUsed/SHATTER_PULSE_COST;
		
		if (souls <= 0) {
			souls = 0;
			shattering = false;
		}
		
		dwarf.playSound(Sound.ENTITY_GENERIC_BURN, 1f, 1.5f, true);
		dwarf.playSound(Sound.ENTITY_GHAST_SHOOT, 1f, 2f, true);
		
		World world = dwarf.getWorld();
		Location center = dwarf.getEyeLocation();
		world.spawnParticle(Particle.REDSTONE, center, 100, 1.5, 1.5, 1.5, 0, DUST_OPTIONS);
		world.spawnParticle(Particle.SMOKE_NORMAL, center, 50, 1.5, 1.5,  1.5, 0.003);
		
		for (MonsterEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
			Vector offset = entity.getEyeLocation().subtract(center).toVector();
			if (offset.length() > SHATTER_RANGE) continue;

			Vector knockback = offset.normalize().multiply(-3 / Math.sqrt(Math.max(2, offset.length())));
			knockback.setY(knockback.getY() / 2 + 0.1);

			MonsterDamage mDamage = entity.createDamage(dwarf, GameDamageType.SOUL_SHATTER, SHATTER_DAMAGE * shatterStrength);
			if (entity.isAI()) mDamage.getMultiPartDamage().timesMult(2.5);
			mDamage.setKnockback(knockback);
			mDamage.fire(true);
		}
	}
	
	
	private static final double TWOPI = 2*PI;
	private static final Particle.DustOptions DEFAULT_COLOUR = new Particle.DustOptions(Color.fromRGB(97,0,216), 1);
	private static final Particle.DustOptions MAX_COLOUR = new Particle.DustOptions(Color.fromRGB(25,2,102), 1);
	
	
	private double polar = 0;
	private double azimuthal = 0;
	
	private void showParticle() {
		polar     = (polar     + 0.1) % TWOPI;
		azimuthal = (azimuthal + 0.03) % TWOPI;
		
		if (!dwarf.everyNthTick(2)) return;
		
		Particle.DustOptions colour = (souls == MAX_SOULS ? MAX_COLOUR : DEFAULT_COLOUR);
		
		int numParticles = (int) soulScaling(0, 15);
		Location center = dwarf.getEyeLocation().subtract(0, 0.5, 0);
		
		for (int i=0; i<numParticles; i++) {
			double particlePolar = polar     + PI/8 * i;
			double particleAzi   = azimuthal + TWOPI/8 * i;
			particlePolar = particlePolar % TWOPI;
			particleAzi   = particleAzi   % TWOPI;
			
			Location particleLocation = Util.getSphericalPosition(center, 1.5, particlePolar, particleAzi);
			particleLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, colour);
		}
	}
}
