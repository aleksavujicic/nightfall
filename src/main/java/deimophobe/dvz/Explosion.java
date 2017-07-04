package deimophobe.dvz;

import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Collection;


/**
 * Created by TKiwisi on 6/16/17.
 */
public class Explosion<A extends GameEntity, R extends GameEntity, P extends GamePlayer> {
    private final A attacker;
    private final Collection<P> targets;
    private final Location origin;
    private final DamageType type;
    private double damage;
    double range;
    private double kb;

    public Explosion(A attacker, Collection<P> targets, Location origin, DamageType type, double damage, double range, double kb) {
        this.attacker = attacker;
        this.targets = targets;
        this.origin = origin;
        this.type = type;
        this.damage = damage;
        this.range = range;
        this.kb = kb;
    }

    public void explode() {
        for (P target : targets) {
            double distance = origin.distance(target.getPlayer().getLocation());
            if (distance <= range) {
                Vector kbaway = target.getPlayer().getLocation().toVector().subtract(origin.toVector());
                kbaway.normalize().multiply(kb / Math.sqrt(Math.max(1, distance)));
                kbaway.setY(kbaway.getY() + 0.4); // Slight Y boost to make the players jump a little
                
                target.setVelocity(kbaway);
                target.customDamage(attacker, type, damage);
            }
        }
    }
}
