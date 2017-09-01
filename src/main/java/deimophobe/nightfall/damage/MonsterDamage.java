package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.DwarfEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class MonsterDamage extends GameDamage<DwarfEntity, MonsterEntity> {
	
	private boolean proc;
	public void setProc(boolean proc) { this.proc = proc;}
	
	public MonsterDamage(EntityDamageEvent event, DwarfEntity attacker, MonsterEntity receiver, GameDamageType type, double damage, Projectile arrow) {
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
		if (proc) instaKill();
		
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
