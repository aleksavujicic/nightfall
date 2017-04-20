package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.Bukkit;

/**
 * Created by Deimophobe on 15/04/17.
 */
class Direwolf extends Wolf {
	
	@Override protected MobType getType() {return MobType.DIREWOLF;}
	
	Direwolf(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override
	public void spawn() {
		super.spawn();
		
		Disguise disguise = getDisguise();
		FlagWatcher watcher = disguise.getWatcher();
		if (watcher instanceof WolfWatcher) {
			((WolfWatcher) watcher).setAngry(true);
		} else {
			Bukkit.getLogger().warning("Direwolf not disguised as wolf?");
		}
	}
}
