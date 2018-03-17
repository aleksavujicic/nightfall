package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.ranged.AbstractBow;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class Luminous extends AbstractBow {
    public Luminous(Dwarf dwarf) {
        super(dwarf);
    }

    private final static int POWER = 80;
    private final static CustomItem ITEM = getBow("hero", "luminous", POWER);
    @Override public CustomItem getItem() {
        return ITEM;
    }
    @Override public KitGiveType getGiveType() { return KitGiveType.START; }
    @Override public String getBowIdentifier() {return "LUMINOUS";}
    @Override public int getPower() {return POWER;}

    private static final double MAX_RANGE = 80;
    private static final double THICKNESS = 1.6;
    private static final double MIN_DISTANCE_FROM_SHOOTER = 1;

    @Override
    public void onDamageAttack(MonsterDamage damage) {
        super.onDamageAttack(damage);
        if (damage.getType() == GameDamageType.LUMINOUS) {
            damage.getArrowRes().timesMult(0.5);
            
            if (damage.getMonster() instanceof AIEntity) {
            	damage.getMultiPartDamage().timesMult(1.5);
			}
        }
    }

    @Override
    public Projectile onBowFire(Projectile arrow, float force) {
		if (force < 0.5) return null;
        if (!dwarf.hasArrows(4)) return null;
        dwarf.useArrows(4);

        double range = MAX_RANGE * force * force;
	
		ParticleSwirler swirler = new ParticleSwirler(dwarf.getLocation().getDirection());
		GamePlayer.ProcGiver procGiver = dwarf.new ProcGiver(ProcType.LUMINOUS, MIN_DISTANCE_FROM_SHOOTER);
		GamePlayer.GameEntityDamager<MonsterEntity> entityDamager = dwarf.new GameEntityDamager<MonsterEntity>(GameDamageType.LUMINOUS, getPower()*force);
		dwarf.fireParticle(4, range, THICKNESS, 0.33, swirler, procGiver, entityDamager);
	
		dwarf.playSound("entity.ghast.shoot", 1f, 1.35f - force*0.5f, true);
		
		Location chest = dwarf.getEyeLocation().subtract(0, 0.5, 0);
		World world = chest.getWorld();
		world.spawnParticle(Particle.FLAME, chest, (int) (20*force), 0.6f, 0.6f, 0.6f, 0.07);
		world.spawnParticle(Particle.VILLAGER_HAPPY, chest, (int) (15*force), 0.6f, 0.6f, 0.6f, 0.07);
		world.spawnParticle(Particle.END_ROD, chest, (int) (10*force), 0.6f, 0.6f, 0.6f, 0.07);
		world.spawnParticle(Particle.DRAGON_BREATH, chest, (int) (5*force), 0.6f, 0.6f, 0.6f, 0.07);

        return null;
    }
    
    private static class ParticleSwirler implements Consumer<Location> {
    	private final Vector u1;
		private final Vector u2;
		private double theta = 0;
	
		private ParticleSwirler(Vector planeNormal) {
			Misc.Pair<Vector> planeBasis = Misc.orthonormalBasisOfPlaneFromNormal(planeNormal);
			u1 = planeBasis.first.multiply(0.125);
			u2 = planeBasis.second.multiply(0.125);
		}
	
		@Override
		public void accept(Location location) {
			theta = (theta + 0.3) % (2*Math.PI);
			double emerTheta = (theta + 2d/3 * Math.PI) % (2*Math.PI);
			double purpTheta = (theta + 4d/3 * Math.PI) % (2*Math.PI);
			
			// u1 * cos(theta) + u2*sin(theta)
			Vector fireOffset = u1.clone().multiply(Math.cos(theta)).add(u2.clone().multiply(Math.sin(theta)));
			Vector emerOffset = u1.clone().multiply(Math.cos(emerTheta)).add(u2.clone().multiply(Math.sin(emerTheta)));
			Vector purpOffset = u1.clone().multiply(Math.cos(purpTheta)).add(u2.clone().multiply(Math.sin(purpTheta)));
			Location firePos = location.clone().add(fireOffset);
			Location emerPos = location.clone().add(emerOffset);
			Location purpPos = location.clone().add(purpOffset);
			
			World world = location.getWorld();
			world.spawnParticle(Particle.FLAME, firePos, 3, 0.05, 0.05, 0.05, 0);
			world.spawnParticle(Particle.VILLAGER_HAPPY, emerPos, 2, 0.05, 0.05, 0.05, 0);
			world.spawnParticle(Particle.DRAGON_BREATH, purpPos, 1, 0.05, 0.05, 0.05, 0);
		}
	}
}
