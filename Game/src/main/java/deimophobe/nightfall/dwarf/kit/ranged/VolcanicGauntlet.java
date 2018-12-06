package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.death.DeathMessageMaker;
import deimophobe.nightfall.damage.death.KeywordDeathMessageMaker;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.util.Hitscan;
import deimophobe.nightfall.util.HitscanBuilder;
import deimophobe.nightfall.util.HitscanProjectile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 5/10/17.
 */
public class VolcanicGauntlet extends AbstractBow {
	public VolcanicGauntlet(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 70;
	private final static CustomItem ITEM = getBow("volcanic", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "VOLCANIC";}
	@Override public int getPower() {return POWER;}
	
	private static final double MAX_RANGE = 50;
	private static final double THICKNESS = 1.25;
	private static final double PARTICLE_OFFSET = THICKNESS/10;
	private static final double AOE_RADIUS = 1.5;
	
	private static final int COST = 2;
	
	
	private static final HitscanBuilder HITSCAN_BUILDER = HitscanBuilder.aHitscan()
			.withThickness(THICKNESS)
			.withParticlePeriod(0.33)
			.withParticlePlacer(location -> location.getWorld().spawnParticle(Particle.FLAME, location, 3, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0))
			;
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		if (force < 0.9) return null;
		if (!dwarf.hasArrows(COST)) return null;
		dwarf.useArrows(COST);
		
		GamePlayer.GameEntityDamager<MonsterEntity> entityDamager = dwarf.new GameEntityDamager<MonsterEntity>(
				GameDamageType.VOLCANIC_BOW,
				(monster) -> (monster.isAI() ? POWER*2d/3d : POWER)
		);
		Hitscan hitscan = HITSCAN_BUILDER.but().withMobConsumer(entityDamager).build();
		HitscanProjectile.fireProjectile(dwarf, 3, MAX_RANGE, hitscan);
		
		Location feets = dwarf.getLocation().add(0, 0.25, 0);
		World world = feets.getWorld();
		world.spawnParticle(Particle.FLAME, feets, 50, AOE_RADIUS/2, 0.1f, AOE_RADIUS/2, 0);
		world.spawnParticle(Particle.LAVA, feets, 20, AOE_RADIUS/2, 0.1f, AOE_RADIUS/2, 0);
		
		Location hands = dwarf.getEyeLocation();
		Misc.moveLocation(hands, 0, 0.3, -0.3);
		world.spawnParticle(Particle.FLAME, hands, 10, 0f, 0f, 0f, 0.15);
		
		
		dwarf.playSound("entity.generic.burn", 1f, 0.7f, true);
		dwarf.playSound("entity.ghast.shoot", 1f, 0.85f, true);
		
		return null;
	}
	
	private static final DeathMessageMaker PUNCHED = new KeywordDeathMessageMaker("punched");
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isMeleeDamageFromItem(damage)) {
			damage.setDeathMessageMaker(PUNCHED);
		}
	}
}

