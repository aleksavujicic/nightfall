package deimophobe.dvz.monster.mob;

import deimophobe.dvz.dwarf.kit.DwarvenItem;
import deimophobe.dvz.monster.PlayerMonster;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WitherWatcher;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 20/01/17.
 */
class WitherSkele extends SkeletonMob {
	WitherSkele(PlayerMonster monster) {
		super(monster, MobType.WITHERSKELE);
	}
}
