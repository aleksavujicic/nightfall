package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.event.MobSpawnEvent;
import deimophobe.nightfall.game.GameSize;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

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
	
	private static final Set<MobBuffer> torusMobBuffers = new HashSet<>();
	
	static void resetBuffers() {
		torusMobBuffers.forEach(HandlerList::unregisterAll);
		torusMobBuffers.clear();
	}
	
	@Override
	public void startDoom() {
		MobBuffer buffer = new MobBuffer();
		NightfallPlugin.registerListener(buffer);
		torusMobBuffers.add(buffer);
		
		super.startDoom();
	}
	
	private final class MobBuffer implements Listener {
		@EventHandler
		public void buffMob(MobSpawnEvent event) {
			event.addWeaponModifier(ItemModifierType.ATTACK, 5, "Torus Doom");
		}
	}
}
