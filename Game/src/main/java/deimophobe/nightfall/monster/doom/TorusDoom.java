package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.event.MobSpawnEvent;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameSize;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Deimophobe on 25/02/17.
 */
@DoomMeta(
		title = "Torus",
		subtitles = {
				"King of Minotaurs",
				"Lord of the Labyrinth"
		},
		specialMobs = {
				@SpecialSpawn(special = MobType.TORUS, size = GameSize.MEDIUM)
		},
		regularMobs = { MobType.MINOTAUR }
)
class TorusDoom extends AnnotatedDoom {
	
	@Override
	public void startDoom() {
		MobBuffer buffer = new MobBuffer();
		Game.getGame().addGameListener(buffer);
		
		super.startDoom();
	}
	
	private final class MobBuffer implements Listener {
		@EventHandler
		public void buffMob(MobSpawnEvent event) {
			event.addWeaponModifier(ItemModifierType.ATTACK, 5, "Torus Doom");
		}
	}
}
