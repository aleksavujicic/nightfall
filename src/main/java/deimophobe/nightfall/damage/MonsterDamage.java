package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class MonsterDamage<A extends GameEntity> extends GameDamage<A, MonsterEntity> {
	
	private boolean proc;
	public void setProc(boolean proc) { this.proc = proc;}
	
	private double arrowRes = 0;
	public void setArrowRes(double res) { this.arrowRes = res; }
	public void addArrowRes(double res) { this.arrowRes += res; }
	public void multiplyArrowRes(double mult) { this.arrowRes *= mult; }
	public void removeArrowRes() { this.arrowRes = 0; }
	
	public MonsterDamage(A attacker, MonsterEntity receiver, GameDamageType type, double damage, Projectile arrow) {
		super(attacker, receiver, type, damage, arrow);
	}
	
	public MonsterEntity getMonster() {
		return getReceiver();
	}
	
	@Override
	void notifyEntities() {
		if (attacker instanceof Dwarf)
			((Dwarf) attacker).onDamageAttack(this);
		receiver.onDamageReceive(this);
	}
	
	@Override
	boolean applyDamage(EntityDamageEvent event) {
		if (proc) instaKill();
		
		if (type == NaturalDamageType.RANGED || type == CustomDamageType.EBOW)
			timesMultiplier(1 - arrowRes);
		
		boolean successful = super.applyDamage(event);
		
		if (willKill()) {
			
			// Prevent killing a monster and set to spectator instead
			if (receiver instanceof MonsterPlayer) {
				((MonsterPlayer)receiver).kill(false);
				event.setDamage(0);
			}
			
			// Notify dwarf if there is one
			if (attacker instanceof Dwarf)
				((Dwarf) attacker).onKill(this);
		}
		
		return successful;
	}
}
