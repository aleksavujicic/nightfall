package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.event.DwarfDamageEvent;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Deimophobe on 8/07/17.
 */

@DoomMeta(
		title = "Hellhounds",
		regularMobs = { MobType.HELLHOUND }
)
class HellhoundDoom extends AnnotatedDoom {
	private static final int DURATION = 90*20;
	
	@Override
	public void startDoom() {
		super.startDoom();
		FireListener listener = new FireListener();
		FireStorm storm = new FireStorm(DURATION, listener);
		Game.getGame().addUpdateable(storm);
		
		// Sound effects
		Location spawn = GameMap.getCurrentMap().getCurrentMobspawn();
		spawn.getWorld().playSound(spawn, "entity.wolf.howl", 10000f, 0.85f);
		spawn.getWorld().playSound(spawn, "entity.ghast.shoot", 10000f, 0.5f);
	}
	
	private class FireStorm extends LifetimeExpireable {
		private final FireListener listener;
		
		
		protected FireStorm(int lifetime, FireListener listener) {
			super(lifetime);
			this.listener = listener;
			Game.getGame().addGameListener(listener);
		}
		
		@Override
		public void update() {
			super.update();
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				Location location = player.getLocation();
				player.spawnParticle(Particle.FLAME, location, 15, 10, 10, 10, 0.02);
			}
		}
		
		@Override
		public void onExpiry() {
			Game.getGame().removeGameListener(listener);
		}
	}
	
	private class FireListener implements Listener {
		@EventHandler
		public void onDamage(DwarfDamageEvent event) {
			DwarfDamage damage = event.getDamage();
			GameDamageType type = damage.getType();
			switch (type) {
				case FIRE:
					damage.getMultiPartDamage().timesMult(2.5);
					break;
				case LAVA:
					damage.getMultiPartDamage().timesMult(1.5);
					break;
					
				default:
					if (damage.getAttacker() instanceof MonsterPlayer) {
						damage.increaseFireTicks(3*20);
					}
					break;
			}
		}
	}
}
