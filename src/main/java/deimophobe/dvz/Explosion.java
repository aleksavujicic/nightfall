package deimophobe.dvz;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;


/**
 * Created by TKiwisi on 6/16/17.
 */
public class Explosion<A extends GameEntity, R extends GameEntity> {
    private final A attacker;
    private final Location origin;
    private final DamageType type;
    private double damage;
    double range;
    private double kb;

    public Explosion(A attacker,Location origin, DamageType type, double damage, double range, double kb) {
        this.attacker = attacker;
        this.origin = origin;
        this.type = type;
        this.damage = damage;
        this.range = range;
        this.kb = kb;
    }

    public void explode() {
        Bukkit.broadcastMessage("xplodey");
        for (Dwarf jimmy : DwarfManager.getManager().getGamePlayers()) {
            Bukkit.broadcastMessage("Jimmy: "+ jimmy.getName());
            double distance = origin.distance(jimmy.getPlayer().getLocation());
            Bukkit.broadcastMessage("dist:"+ distance);
            if (distance <= range) {
                Vector kbaway = jimmy.getPlayer().getLocation().toVector().subtract(origin.toVector());
                kbaway.normalize().multiply(kb / Math.sqrt(Math.max(1, distance)));
                kbaway.setY(kbaway.getY() + 0.1); // Slight Y boost to make the players jump a little
                jimmy.customDamage(attacker, type, damage);
    
                Bukkit.broadcastMessage("Kablooey");
            }
        }
    }
}
