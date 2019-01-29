package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.DynamicPageResetter;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.upgrades.MonsterUpgrades;
import org.bukkit.inventory.ItemStack;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 16/01/19.
 */
class UpgradeSelectMenuItem extends DynamicPageResetter<MonsterPlayer> {
	
	private final MobType primaryMobType;
	private final int cost;
	
	UpgradeSelectMenuItem(ItemStack item, UpgradeContainerMenu menu, MobType primaryMobType, int cost) {
		super(item, menu);
		checkArgument(primaryMobType.isUpgradeable(), "Mob type must be a primary mob (got %s)", primaryMobType);
		
		this.primaryMobType = primaryMobType;
		this.cost = cost;
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		MonsterPlayer monster = session.getData();
		MonsterUpgrades upgrades = monster.getUpgrades();
		
		if (!monster.hasExperience(cost)) {
			monster.sendInsufficientExperienceMessage(cost);
			return false;
		}
		
		monster.useExperience(cost);
		upgrades.setPrimaryMob(primaryMobType);
		return super.onClick(session);
	}
}
