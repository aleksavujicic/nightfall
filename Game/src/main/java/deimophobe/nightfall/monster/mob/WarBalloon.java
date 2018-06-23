package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 24/06/18.
 */
public class WarBalloon extends AbstractMob {
	protected WarBalloon(MonsterPlayer monster) {
		super(monster, MobType.WAR_BALLOON);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		
		Player player = monster.getPlayer();
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed(0.01f);
		
		monster.teleportTo(monster.getLocation().add(0,0.5,0));
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, -100);
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		
		Player player = monster.getPlayer();
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setFlySpeed(0.1f);
	}
}
