package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 3/05/18.
 */
public class Silverbell extends AbstractRideableMob {
	protected Silverbell(MonsterPlayer monster) {
		super(monster, MobType.SILVERBELL);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 3);
	}
	
	@Override
	protected boolean canMount(MonsterPlayer player) {
		if (player.getMob().getType() == MobType.BATTERING_RAM) return false;
		
		int numPassengers = monster.getPlayer().getPassengers().size();
		return (numPassengers < 2);
	}
}
