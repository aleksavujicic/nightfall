package deimophobe.nightfall.damage;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class DwarfDamage<A extends GameEntity> extends GameDamage<A, Dwarf> {
	private int armourShred = 0;
	public int getArmourShred() {return armourShred;}
	public void setArmourShred(int armourShred) {this.armourShred = armourShred;}
	public void addArmourShred(int amt) {this.armourShred += amt;}
	public void multiplyArmourShred(double multiply) {this.armourShred *= multiply;}
	
	private int manaDrain = 0;
	public int getManaDrain() {return manaDrain;}
	public void setManaDrain(int manaDrain) {this.manaDrain = manaDrain;}
	public void addManaDrain(int manaDrain) {this.manaDrain += manaDrain;}
	public void multiplyManaDrain(double multiply) {this.manaDrain *= multiply;}
	
	DwarfDamage(A attacker, Dwarf receiver, GameDamageType type, double damage, Projectile arrow) {
		super(attacker, receiver, type, damage, arrow);
	}
	
	public Dwarf getDwarf() {
		return getReceiver();
	}
	
	@Override
	void notifyEntities() {
		if (attacker instanceof MonsterEntity)
			((MonsterEntity) attacker).onDamageAttack(this);
		receiver.onDamageReceive(this);
	}
	
	@Override
	boolean applyDamage(EntityDamageEvent event) {
		boolean successful = super.applyDamage(event);
		
		if (successful) {
			receiver.getArmour().damage(armourShred);
			receiver.useMana(manaDrain);
			
			if (Game.getGame().getPhase() == Phase.BUILD) {
				if (receiver.getHealth() - getCurrentDamage() <= 0.1 || instaKill) {
					event.setDamage(0);
					event.setCancelled(true);
					
					receiver.respawn();
				}
			}
		}
		
		
		return successful;
	}
}
