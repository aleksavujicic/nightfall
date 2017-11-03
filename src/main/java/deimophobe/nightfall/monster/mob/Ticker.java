package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.doom.DoomManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 7/10/17.
 */
class Ticker extends AbstractMob {
	private final int maxTime = 30 + (int) (120*Math.random());
	private int deathTimer = maxTime;
	
	protected Ticker(MonsterPlayer monster) {
		super(monster, MobType.TICKER);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		monster.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
		giveSpawnProtection(deathTimer*20);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (sec) {
			deathTimer--;
			
			if (deathTimer == 0)
				explode();
			else
				tick();
		}
		
		float frac = 1 - (float)deathTimer/maxTime;
		
		double red = (r2 - r1)*frac + r1;
		double green = (g2 - g1)*frac + g1;
		double blue = (b2 - b1)*frac + b1;
		red *= 1d/256;
		green *= 1d/256;
		blue *= 1d/256;
		
		red = 1 - Math.pow(1-red,2);
		green = 1 - Math.pow(1-green,2);
		
		Location loc = monster.getEyeLocation();
		loc.getWorld().spawnParticle(Particle.REDSTONE,loc, 0, red, green, blue,1);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getType() == NaturalDamageType.MELEE)
			damage.cancel();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.cancel();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (!sneaking)
			monster.setVelocity(0, 0.8, 0);
	}
	
	@Override
	public float getCooldown() {
		return (float)deathTimer/maxTime;
	}
	
	
	private static final double r1 = 51, g1 = 248, b1 = 14;
	private static final double r2 = 255, g2 = 14, b2 = 14;
	
	private void tick() {
		
		// Sound
		monster.playSound("block.note.hat", 1f, 1f, true);
		
		// Title
		ChatColor colour;
		if (deathTimer > 10)
			colour = ChatColor.GREEN;
		else if (deathTimer > 3)
			colour = ChatColor.YELLOW;
		else
			colour = ChatColor.RED;
		
		if (maxTime - deathTimer >= 10) // Don't override doom title
			monster.sendTitleMessage(colour.toString() + deathTimer);
	}
	
	
	private final static double RADIUS = 9;
	private final static double DAMAGE = 200;
	private final static int ARMOUR_SHRED = 800;
	private final static int MANA_DRAIN = 500;
	
	private void explode() {
		Location loc = monster.getEyeLocation();
		loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 10, 1, 1, 1,0);
		loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 25, 1.5, 1.5, 1.5,0.3);
		loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 100, 1, 1, 1,0.5);
		monster.playSound("entity.generic.explode", 1f, 0.7f, true);
		
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(monster.getLocation()).toVector();
			double distance = offset.length();
			if (distance <= RADIUS) {
				double affectRate = 1.5/Math.max(distance,1.5);
				
				offset.normalize();
				offset.add(new Vector(0,6,0));
				offset.multiply(5*affectRate);
				
				double damageDealt = DAMAGE*affectRate;
				int armourShred = (int) (ARMOUR_SHRED*affectRate);
				int drain = (int) (MANA_DRAIN*affectRate);
				
				DwarfDamage damage = dwarf.createDamage(monster, CustomDamageType.GOBO_KABOOM, damageDealt);
				damage.setArmourShred(armourShred);
				damage.setManaDrain(drain);
				damage.setKnockback(offset);
				damage.fire(true);
			}
		}
		BlockConverter.convert(BlockConverter.Type.EXPLOSION, monster.getLocation(), 15);
		monster.doDamage(null, CustomDamageType.SELF_GOBO_KABOOM, 100000, true, true);
		
		DoomManager.getManager().reduceDoom(maxTime);
	}
}
