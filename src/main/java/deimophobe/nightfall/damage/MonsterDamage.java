package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class MonsterDamage extends GameDamage<GameEntity, MonsterEntity> {
	
	private boolean proc;
	public void setProc(boolean proc) { this.proc = proc;}
	
	private MultiPartValue arrowRes = new MultiPartValue(0);
	public MultiPartValue getArrowRes() { return arrowRes; }
	
	public MonsterDamage(GameEntity attacker, MonsterEntity receiver, GameDamageType type, double damage) {
		super(attacker, receiver, type, damage);
	}
	
	MonsterDamage(GameEntity attacker, MonsterEntity receiver, GameDamageType type, double damage, Projectile arrow) {
		super(attacker, receiver, type, damage, arrow);
	}
	
	public MonsterEntity getMonster() {
		return getReceiver();
	}
	
	public boolean isArrow() {
		return type == NaturalDamageType.RANGED
				|| type == CustomDamageType.JADE_BOW
				|| type == CustomDamageType.VOLCANIC_BOW
				|| type == CustomDamageType.LUMINOUS;
	}
	
	@Override
	void notifyEntities() {
		if (attacker instanceof Dwarf) {
			((Dwarf) attacker).onDamageAttack(this);
		}
		receiver.onDamageReceive(this);
	}
	
	@Override
	boolean applyDamage(EntityDamageEvent event) {
		if (proc && !isCancelled()) instaKill();
		
		if (isArrow()) {
			getDamage().timesMult(1 - arrowRes.getValue());
		}
		
		boolean successful = super.applyDamage(event);
		
		if (willKill()) {
			
			// Prevent killing a monster and set to spectator instead
			if (receiver instanceof MonsterPlayer) {
				((MonsterPlayer)receiver).kill(false);
				event.setDamage(0);
			}
			
			if (receiver instanceof AIEntity) {
				((AIEntity) receiver).onDeath(this);
			}
			
			// Notify dwarf if there is one
			if (attacker instanceof Dwarf)
				((Dwarf) attacker).onKill(this);
		}
		
		return successful;
	}
}
