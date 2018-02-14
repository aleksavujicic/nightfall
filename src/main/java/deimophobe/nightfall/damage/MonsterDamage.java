package deimophobe.nightfall.damage;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.entity.Projectile;

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
		addHandlers();
	}
	
	MonsterDamage(GameEntity attacker, MonsterEntity receiver, GameDamageType type, double damage, Projectile arrow) {
		super(attacker, receiver, type, damage, arrow);
		addHandlers();
	}
	
	public MonsterEntity getMonster() {
		return getReceiver();
	}
	
	@Override
	void notifyEntities() {
		if (attacker instanceof Dwarf) {
			((Dwarf) attacker).onDamageAttack(this);
		}
		receiver.onDamageReceive(this);
		
		
		// 'Self notification'
		if (proc && !isCancelled()) instaKill();
		
		if (type.isArrow()) {
			getMulitPartDamage().timesMult(1 - arrowRes.getValue());
		}
	}
	
	private void addHandlers() {
		addPreDamageHandler(damage -> {
			if (willKill()) {
				// Prevent killing a monster and set to spectator instead
				if (receiver instanceof MonsterPlayer) {
					((MonsterPlayer)receiver).kill(false);
					damage.softCancel();
				}
				
				if (receiver instanceof AIEntity) {
					((AIEntity) receiver).onDeath(this);
				}
				
				// Notify dwarf if there is one
				if (attacker instanceof Dwarf)
					((Dwarf) attacker).onKill(this);
			}
		});
	}
	
}
