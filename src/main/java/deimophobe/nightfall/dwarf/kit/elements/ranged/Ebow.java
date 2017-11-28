package deimophobe.nightfall.dwarf.kit.elements.ranged;

import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.items.CustomItem;
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
	
	private static final double MAX_RANGE = 40;
	private static final double THICKNESS = 1.5;
	private static final double MIN_DISTANCE_FROM_SHOOTER = 1;
	
	private static final Consumer<Location> PARTICLE_PLACER =
			(location) -> location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location, 4, 0.1, 0.1, 0.1);
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		if (!dwarf.hasArrows(3)) return null;
		dwarf.useArrows(3);
		
		double range = MAX_RANGE * force * force;
		
		GamePlayer.ProcGiver procGiver = dwarf.new ProcGiver(ProcType.EBOW, MIN_DISTANCE_FROM_SHOOTER);
		GamePlayer.GameEntityDamager<MonsterEntity> entityDamager = dwarf.new GameEntityDamager<MonsterEntity>(CustomDamageType.EBOW, getPower()*force);
		dwarf.fireBeam(range, THICKNESS, 0.33, PARTICLE_PLACER, procGiver, entityDamager);
		
		if (procGiver.gaveProc()) {
			Sounds.DWARF_ITEM_EBOW_GIVE_PROC.playSound(dwarf);
		}
		
		return null;
	}
}
