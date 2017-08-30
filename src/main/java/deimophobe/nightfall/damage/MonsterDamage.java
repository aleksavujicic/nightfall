package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.entity.DwarfEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public class MonsterDamage extends GameDamage<DwarfEntity, MonsterEntity> {
	
	private boolean proc;
	public void setProc(boolean proc) { this.proc = proc;}
	
	public MonsterDamage(EntityDamageEvent event, GameDamageType type, DwarfEntity attacker, MonsterEntity receiver, double damage, Projectile arrow) {
		super(event, type, attacker, receiver, damage, arrow);
	}
	
	@Override
	boolean applyDamage() {
		if (proc) instaKill();
		
		boolean successful = super.applyDamage();
		return successful;
	}
}
