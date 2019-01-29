package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.SimpleItem;
import deimophobe.nightfall.monster.MobCreator;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 29/01/19.
 */
public class PermanentSpawnEgg extends SimpleItem<MonsterPlayer> {
	private final MobCreator<?> mobCreator;
	
	public PermanentSpawnEgg(ItemStack item, MobCreator<?> mobCreator) {
		super(item);
		this.mobCreator = mobCreator;
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		MonsterPlayer monster = session.getData();
		monster.spawnMob(mobCreator);
		
		session.closeSession();
		return false;
	}
}
