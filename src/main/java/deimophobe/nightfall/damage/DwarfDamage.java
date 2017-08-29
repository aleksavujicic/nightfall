package deimophobe.nightfall.damage;

import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class DwarfDamage<A extends GameEntity, R extends Dwarf>  extends GameDamage<A, R> {
	private int armourShred;
	public int getArmourShred() {return armourShred;}
	public void setArmourShred(int armourShred) {this.armourShred = armourShred;}
	
	private int manaDrain;
	public int getManaDrain() {return manaDrain;}
	public void setManaDrain(int manaDrain) {this.manaDrain = manaDrain;}
	
	public DwarfDamage(EntityDamageEvent event, GameDamageType type, A attacker, R receiver, double damage, boolean force, Projectile arrow) {
		super(event, type, attacker, receiver, damage, force, arrow);
	}
}
