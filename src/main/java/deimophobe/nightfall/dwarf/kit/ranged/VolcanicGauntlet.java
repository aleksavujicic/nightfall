package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Projectile;

import java.util.function.Consumer;

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
	
	private static final double MAX_RANGE = 45;
	private static final double THICKNESS = 1.25;
	private static final double PARTICLE_OFFSET = THICKNESS/10;
	private static final double AOE_RADIUS = 1.5;
	
	private static final Consumer<Location> PARTICLE_PLACER =
			(location) -> location.getWorld().spawnParticle(Particle.FLAME, location, 3, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0);
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		if (force < 0.5) return null;
		if (!dwarf.hasArrows(3)) return null;
		dwarf.useArrows(3);
		
		double force2 = force*force;
		double range = MAX_RANGE * force2;
		double damage = POWER * force2;
		
		GamePlayer.GameEntityDamager<MonsterEntity> entityDamager = dwarf.new GameEntityDamager<MonsterEntity>(
				CustomDamageType.VOLCANIC_BOW,
				(monster) -> ((monster instanceof MonsterPlayer) ? damage : damage*2d/3d)
		);
		dwarf.fireParticle(3, range, THICKNESS, 0.33, PARTICLE_PLACER, null, entityDamager);
		
		Location feets = dwarf.getLocation().add(0, 0.25, 0);
		World world = feets.getWorld();
		world.spawnParticle(Particle.FLAME, feets, (int) (50*force2), AOE_RADIUS/2, 0.1f, AOE_RADIUS/2, 0);
		world.spawnParticle(Particle.LAVA, feets, (int) (20*force2), AOE_RADIUS/2, 0.1f, AOE_RADIUS/2, 0);
		
		Location hands = dwarf.getEyeLocation();
		Misc.moveLocation(hands, 0, 0.3, -0.3);
		world.spawnParticle(Particle.FLAME, hands, (int) (10*force2), 0f, 0f, 0f, 0.15);
		
		
		dwarf.playSound("entity.generic.burn", 1f, 1.2f - force*0.5f, true);
		dwarf.playSound("entity.ghast.shoot", 1f, 1.35f - force*0.5f, true);
		
		return null;
	}
}

