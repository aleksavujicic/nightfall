package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.MonsterEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class DwarfDamage  extends GameDamage<MonsterEntity, Dwarf> {
	private int armourShred;
	public int getArmourShred() {return armourShred;}
	public void setArmourShred(int armourShred) {this.armourShred = armourShred;}
	public void multiplyArmourShred(double multiply) {this.armourShred *= multiply;}
	
	private int manaDrain;
	public int getManaDrain() {return manaDrain;}
	public void setManaDrain(int manaDrain) {this.manaDrain = manaDrain;}
	public void multiplyManaDrain(double multiply) {this.manaDrain *= multiply;}
	
	public DwarfDamage(EntityDamageEvent event, GameDamageType type, MonsterEntity attacker, Dwarf receiver, double damage, Projectile arrow) {
		super(event, type, attacker, receiver, damage, arrow);
	}
	
	@Override
	boolean applyDamage() {
		boolean successful = super.applyDamage();
		
		if (successful) {
			getReceiver().getArmour().damage(manaDrain);
			getReceiver().useMana(manaDrain);
		}
		return successful;
	}
}
