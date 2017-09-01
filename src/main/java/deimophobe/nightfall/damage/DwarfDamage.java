package deimophobe.nightfall.damage;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.MonsterEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class DwarfDamage extends GameDamage<MonsterEntity, Dwarf> {
	private int armourShred = 0;
	public int getArmourShred() {return armourShred;}
	public void setArmourShred(int armourShred) {this.armourShred = armourShred;}
	public void multiplyArmourShred(double multiply) {this.armourShred *= multiply;}
	
	private int manaDrain = 0;
	public int getManaDrain() {return manaDrain;}
	public void setManaDrain(int manaDrain) {this.manaDrain = manaDrain;}
	public void multiplyManaDrain(double multiply) {this.manaDrain *= multiply;}
	
	DwarfDamage(EntityDamageEvent event, MonsterEntity attacker, Dwarf receiver, GameDamageType type, double damage, Projectile arrow) {
		super(event, attacker, receiver, type, damage, arrow);
	}
	
	@Override
	public void fire() {
		attacker.onDamageAttack(this);
		receiver.onDamageReceive(this);
		applyDamage();
	}
	
	@Override
	protected boolean applyDamage() {
		boolean successful = super.applyDamage();
		
		if (successful) {
			receiver.getArmour().damage(manaDrain);
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
