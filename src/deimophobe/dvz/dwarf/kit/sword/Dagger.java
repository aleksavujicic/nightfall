package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MobManager;
import deimophobe.dvz.monster.PlayerMonster;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Dagger extends Sword {
	
	Dagger(Dwarf dwarf) {
		super(dwarf, SwordType.DAGGER, 1200);
	}
	
	@Override
	public void onKill(PlayerMonster monster) {
		reduceCooldown(200);
	}
	
	@Override
	public void onHit(PlayerMonster monster) {
		monster.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 50, 4, true), true);
	}
	
	private static final double EPSILON = 1;
	private static final double RANGE = 4;
	@Override
	protected boolean ability() {
		Location playerLoc = dwarf.getPlayer().getLocation();
		Vector lookDir = playerLoc.getDirection();
		
		PlayerMonster closestMonster = null;
		double closestRange = RANGE;
		double closestOffset = EPSILON;
		for (PlayerMonster monster : MobManager.getManager().getMobs()) {
			Location testLoc = monster.getPlayer().getLocation();
			Vector offsetDir = testLoc.subtract(playerLoc).toVector();
			double distance = offsetDir.length();
			
			if (distance > RANGE) continue;
			
			double eyeOffset = distance * Math.acos(offsetDir.dot(lookDir) / distance);
			
			if (eyeOffset > EPSILON) continue;
			
			if (distance <= closestRange - 1 || (distance <= closestRange + 1 && eyeOffset <= closestOffset)) {
				closestMonster = monster;
				closestRange = distance;
				closestOffset = eyeOffset;
			}
		}
		
		if (closestMonster != null) {
			Location loc = closestMonster.getPlayer().getEyeLocation();
			
			closestMonster.damage(200, dwarf);
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 20, 0.3, 0.3, 0.3, 1);
			//world.spigot().playEffect(loc, Effect.COLOURED_DUST, 0, 1, red, green, blue, 1, 0, 64);
			//world.spawnParticle(Particle.SPELL_INSTANT, ltarget.getEyeLocation(), 1, 0.3, 0.3, 0.3, 0);
			dwarf.playSound("entity.wither.shoot", 1f, 1.5f, true);
			return true;
			
		}
		return false;
	}
}
