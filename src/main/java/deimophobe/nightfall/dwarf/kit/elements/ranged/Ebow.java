package deimophobe.nightfall.dwarf.kit.elements.ranged;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Projectile;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Ebow extends AbstractBow {
	public Ebow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 10;
	private final static CustomItem ITEM = getBow("ebow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.BOW; }
	@Override public String getBowIdentifier() {return "EBOW";}
	@Override public int getPower() {return POWER;}
	
	private static final double MAX_RANGE = 50;
	private static final double THICKNESS = 1.5;
	private static final double MIN_DISTANCE_FROM_SHOOTER = 1;
	
	private static final Consumer<Location> PARTICLE_PLACER =
			(location) -> location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location, 4, 0.1, 0.1, 0.1);
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		if (force < 0.5) return null;
		if (!dwarf.hasArrows(3)) return null;
		dwarf.useArrows(3);
		
		double range = MAX_RANGE * force * force;
		
		GamePlayer.ProcGiver procGiver = dwarf.new ProcGiver(ProcType.EBOW, MIN_DISTANCE_FROM_SHOOTER);
		GamePlayer.GameEntityDamager<MonsterEntity> entityDamager = dwarf.new GameEntityDamager<MonsterEntity>(CustomDamageType.EBOW, getPower()*force);
		dwarf.fireParticle(4, range, THICKNESS, 0.33, PARTICLE_PLACER, procGiver, entityDamager);
		
		return null;
	}
}
