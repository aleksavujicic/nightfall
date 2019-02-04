package deimophobe.nightfall.dwarf.kit.ranged;

import com.google.common.collect.Lists;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.CompletionCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

import java.util.List;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Longbow extends AbstractBow implements CooldownPiece {
	public Longbow(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	private final static int POWER = 75;
	private final static CustomItem ITEM = getBow("longbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "LONGBOW";}
	@Override public int getPower() {return POWER;}
	
	private final Cooldown stackRemover = new CompletionCooldown(6*20, this::resetStacks);
	
	private int stacks = 0;
	private static final int MAX_STACKS = 5;
	private static final double DMG_PER_STACK = 10;
	
	@Override
	public void update() {
		super.update();
		stackRemover.update();
		
		showParticles();
	}
	
	@Override
	public Projectile onBowFire(Projectile proj, float force) {
		Arrow arrow = (Arrow) super.onBowFire(proj, force);
		ArrowMisc.increaseArrowDamage(arrow, stacks*DMG_PER_STACK);
		return arrow;
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		super.onKill(damage);
		if (isRangedDamageFromBow(damage)) {
			incrementStacks();
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (dwarf.isDebugMode() && sneaking) {
			incrementStacks();
		}
	}
	
	@Override
	public float getCooldown() {
		return 1 - stackRemover.getCooldown();
	}
	
	private void incrementStacks() {
		stacks += 1;
		if (stacks > MAX_STACKS) stacks = MAX_STACKS;
		
		dwarf.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f, false);
		stackRemover.reset();
	}
	
	private void resetStacks() {
		stacks = 0;
		dwarf.playSound(Sound.BLOCK_BEACON_DEACTIVATE, 1f, 2f, false);
	}
	
	
	private double theta = 0;
	private void showParticles() {
		if (stacks == 0) return;
		if (!isHoldingItem()) return;
		
		theta = (theta + 0.15) % (2 * Math.PI);
		
		Location playerLoc = dwarf.getPlayer().getEyeLocation();
		
		for (int i = 0; i < stacks; i++) {
			double frac = (double) i / MAX_STACKS;
			double particleTheta = theta - frac * 2 * Math.PI;
			
			Particle.DustOptions colour = (stacks == MAX_STACKS ? MAX_COLOUR : PARTICLE_COLOURS.get(i));
			
			Location particleLoc = playerLoc.clone().add(Math.cos(particleTheta), -1, Math.sin(particleTheta));
			particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0,0, colour);
		}
	}
	
	private static final Particle.DustOptions MAX_COLOUR = new Particle.DustOptions(Color.fromRGB(18,209,205),1);
	private static final List<Particle.DustOptions> PARTICLE_COLOURS;
	static {
		PARTICLE_COLOURS = Lists.newArrayList();
		
		double r1 = 3;
		double g1 = 100;
		double b1 = 200;
		double r2 = 18;
		double g2 = 209;
		double b2 = 180;
		
		for (int i = 0; i < MAX_STACKS; i++) {
			double frac = (double) i / MAX_STACKS;
			int red = (int) (r1 + frac * (r2-r1));
			int green = (int) (g1 + frac * (g2-g1));
			int blue = (int) (b1 + frac * (b2-b1));
			
			PARTICLE_COLOURS.add(new Particle.DustOptions(Color.fromRGB(red,green,blue), 1));
		}
	}
}
