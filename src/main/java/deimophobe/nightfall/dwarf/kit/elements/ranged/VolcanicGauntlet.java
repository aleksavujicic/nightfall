package deimophobe.nightfall.dwarf.kit.elements.ranged;

import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.items.CustomItem;
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
	
	private final static int POWER = 60;
	private final static CustomItem ITEM = getBow("volcanic", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "VOLCANIC";}
	@Override public int getPower() {return POWER;}
	
	private static final double MAX_RANGE = 40;
	private static final double THICKNESS = 1.4;
	private static final double PARTICLE_OFFSET = THICKNESS/10;
	private static final double AOE_RADIUS = 1.4;
	
	private static final Consumer<Location> PARTICLE_PLACER =
			(location) -> location.getWorld().spawnParticle(Particle.FLAME, location, 3, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0);
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {

		if (!dwarf.hasArrows(3)) return null;
		dwarf.useArrows(3);
		
		double range = MAX_RANGE * force * force;
		double radius = AOE_RADIUS * force;
		
		double damage = POWER * force * force;
		
		GamePlayer.GameEntityDamager<MonsterEntity> entityDamager = dwarf.new GameEntityDamager<MonsterEntity>(
				CustomDamageType.VOLCANIC_BOW,
				(monster) -> ((monster instanceof MonsterPlayer) ? damage : damage*2d/3d)
		);
		dwarf.fireBeam(range, THICKNESS, 0.33, PARTICLE_PLACER, null, entityDamager);
		
		Location feets = dwarf.getLocation().add(0, 0.25, 0);
		World world = feets.getWorld();
		world.spawnParticle(Particle.FLAME, feets, (int) (30*force), 1f, 1f, 1f, 0.07);
		world.spawnParticle(Particle.FLAME, feets, (int) (100*force*force), radius/2, 0.1f, radius/2, 0);
		world.spawnParticle(Particle.LAVA, feets, (int) (20*force*force), radius/2, 0.1f, radius/2, 0);
		
		dwarf.playSound("entity.generic.burn", 1f, 1.2f - force*0.5f, true);
		dwarf.playSound("entity.ghast.shoot", 1f, 1.35f - force*0.5f, true);
		
		return null;
	}
}

