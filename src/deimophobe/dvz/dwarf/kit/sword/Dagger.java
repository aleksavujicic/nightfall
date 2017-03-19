package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
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
	public void onKill(GameEntity monster, DamageType b) {
		reduceCooldown(200);
	}
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		monster.givePotionEffect(PotionEffectType.WITHER, 100, 1, true, false, true);
		return damage;
	}
	
	private static final double EPSILON = 1;
	private static final double RANGE = 4;
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			MonsterPlayer closestMonster = dwarf.getLookingAt(EPSILON, RANGE, MonsterManager.getManager());
			
			if (closestMonster != null) {
				Location loc = closestMonster.getPlayer().getEyeLocation();
				
				closestMonster.customDamage(dwarf, DamageType.EVISCERATE, 200);
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 20, 0.3, 0.3, 0.3, 1);
				//world.spigot().playEffect(loc, GameEffect.COLOURED_DUST, 0, 1, red, green, blue, 1, 0, 64);
				//world.spawnParticle(Particle.SPELL_INSTANT, ltarget.getEyeLocation(), 1, 0.3, 0.3, 0.3, 0);
				dwarf.playSound("entity.wither.shoot", 1f, 1.5f, true);
				resetCooldown();
			}
		}
	}
}
