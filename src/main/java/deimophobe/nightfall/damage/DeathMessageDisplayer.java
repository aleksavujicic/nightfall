package deimophobe.nightfall.damage;

import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by Deimophobe on 13/02/18.
 */
public interface DeathMessageDisplayer {
	String getDeathMessage(GamePlayer deadPlayer, @Nullable GameEntity<?> killer, @Nullable ItemStack weapon);
}
