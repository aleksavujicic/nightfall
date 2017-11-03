package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class Luminous extends AbstractBow {
    Luminous(Dwarf dwarf) {
        super(dwarf);
    }

    private final static int POWER = 60;
    private final static CustomItem ITEM = DwarvenItems.getBow("luminous", POWER);
    @Override public CustomItem getItem() {
        return ITEM;
    }
    @Override public KitGiveType getGiveType() { return KitGiveType.START; }
    @Override public String getBowIdentifier() {return "LUMINOUS";}
    @Override public int getPower() {return POWER;}

    private static final double MAX_RANGE = 50;
    private static final double THICKNESS = 1.5;
    private static final double MIN_DISTANCE_FROM_SHOOTER = 1;
    private static final double AOE_RADIUS = 1.5;

    @Override
    public void onDamageAttack(MonsterDamage damage) {
        super.onDamageAttack(damage);
        if (damageFromBow(damage)) {
            damage.getArrowRes().timesMult(0.5);
        }
    }

    @Override
    public Projectile onBowFire(Projectile arrow, float force) {
        Location location = dwarf.getPlayer().getEyeLocation();
        double yaw = location.getYaw() * Math.PI/180;
        location.add(-0.3*Math.cos(yaw), -0.3, -0.3*Math.sin(yaw));
        Vector direction = location.getDirection();


        if (!dwarf.hasArrows(3)) return null;
        dwarf.useArrows(3);

        double range = MAX_RANGE * force * force;
        double radius = AOE_RADIUS * force;

        // Show particles
        Vector delta = direction.clone().multiply(0.33);
        int times = (int) (range/0.33);
        Location particlePos = location.clone();
        World world = particlePos.getWorld();
		
		Misc.Pair<Vector> planeBasis = Misc.orthonormalBasisOfPlaneFromNormal(delta);
		planeBasis.first.multiply(0.125);
		planeBasis.second.multiply(0.125);
		double theta = 0;
        for (int i = 0; i<= times; i++) {
            particlePos.add(delta);
            
			Vector u1 = planeBasis.first.clone();
			Vector u2 = planeBasis.second.clone();
            
            theta = (theta + 0.2) % (2*Math.PI);
            Vector offset = u1.multiply(Math.cos(theta)).add(u2.multiply(Math.sin(theta)));
            Location firePos = particlePos.clone().add(offset);
			Location emerPos = particlePos.clone().subtract(offset);
			
			world.spawnParticle(Particle.FLAME, firePos, 2, 0.05, 0.05, 0.05, 0);
			world.spawnParticle(Particle.VILLAGER_HAPPY, emerPos, 2, 0.05, 0.05, 0.05, 0);
			
            //world.spawnParticle(Particle.VILLAGER_HAPPY, particlePos, 3, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET);
            //world.spawnParticle(Particle.FLAME, particlePos, 2, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0);
			//world.spawnParticle(Particle.END_ROD, location, 0, dx, dy, dz, i*0.05);

            // Stop beam if it hits a block
            if (particlePos.getBlock().getType().isSolid()) {
                range = location.distance(particlePos);
                break;
            }
        }

        Location feets = dwarf.getLocation().add(0, 0.25, 0);
        world.spawnParticle(Particle.FLAME, feets, (int) (30*force), 1f, 1f, 1f, 0.07);
        world.spawnParticle(Particle.VILLAGER_HAPPY, feets, (int) (30*force), 1f, 1f, 1f, 0.07);
        world.spawnParticle(Particle.END_ROD, feets, (int) (20*force), 1f, 1f, 1f, 0.07);

        // Calculate collision
        for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
            // Skip if further than distance shot or too close
            Location monsterLocation = monster.getEyeLocation();
            double distance = location.distance(monsterLocation);
            if (distance <= range) {
                // Find if close enough to beam
                Vector monsterOffset = monsterLocation.clone().subtract(location).toVector();
                Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
                double radialOffset = radialPostion.subtract(monsterOffset).length();

                // If close enough damage mob
                if (monster.distanceTo(dwarf) <= radius) {
                    monster.doDamage(dwarf, CustomDamageType.LUMINOUS, getPower()*force/2);
                } else if (radialOffset <= THICKNESS) {
                    monster.doDamage(dwarf, CustomDamageType.LUMINOUS, getPower()*force);
                }
            }
        }

        boolean gaveProc = false;
        for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
            // Dont give proc to self
            if (dwarf == this.dwarf) continue;

            // Skip if further than distance shot or too close
            Location dwarfLoc = dwarf.getEyeLocation();
            double distance = location.distance(dwarfLoc);
            if (MIN_DISTANCE_FROM_SHOOTER <= distance && distance <= range) {
                // Find if close enough to beam
                Vector monsterOffset = dwarfLoc.clone().subtract(location).toVector();
                Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
                double radialOffset = radialPostion.subtract(monsterOffset).length();

                // If close enough to give dwarf proc
                if (radialOffset <= THICKNESS) {
                    gaveProc = true;
                    dwarf.giveProc(ProcType.LUMINOUS);
                }
            }
        }

        if (gaveProc) {
            Sounds.DWARF_ITEM_EBOW_GIVE_PROC.playSound(dwarf);
        }
        dwarf.playSound("entity.ghast.shoot", 1f, 1.35f - force*0.5f, true);

        return null;
    }
}
