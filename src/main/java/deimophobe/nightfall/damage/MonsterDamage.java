package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class MonsterDamage extends GameDamage<Dwarf, MonsterEntity> {
	
	private boolean proc;
	public void setProc(boolean proc) { this.proc = proc;}
	
	private double arrowRes = 0;
	public void setArrowRes(double res) { this.arrowRes = res; }
	public void addArrowRes(double res) { this.arrowRes += res; }
	public void multiplyArrowRes(double mult) { this.arrowRes *= mult; }
	public void removeArrowRes() { this.arrowRes = 0; }
	
	public MonsterDamage(EntityDamageEvent event, Dwarf attacker, MonsterEntity receiver, GameDamageType type, double damage, Projectile arrow) {
		super(event, attacker, receiver, type, damage, arrow);
	}
	
	public Dwarf getDwarf() {
		return getAttacker();
	}
	
	public MonsterEntity getMonster() {
		return getReceiver();
	}
	
	@Override
	public void fire() {
		attacker.onDamageAttack(this);
		receiver.onDamageReceive(this);
		applyDamage();
	}
	
	@Override
	protected boolean applyDamage() {
		if (proc) instaKill();
		
		if (type == NaturalDamageType.RANGED || type == CustomDamageType.EBOW)
			multiplyDamage(1 - arrowRes);
		
		boolean successful = super.applyDamage();
		
		if (receiver.getHealth() - damage <= 0.1) {
			
			// Prevent killing a monster and set to spectator instead
			if (receiver instanceof MonsterPlayer) {
				((MonsterPlayer)receiver).kill(false);
				event.setDamage(0);
			}
			
			// Notify dwarf if there is one
			attacker.onKill(receiver, type);
		}
		
		return successful;
	}
}
