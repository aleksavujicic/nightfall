package deimophobe.nightfall.map;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.game.entity.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by Deimophobe on 10/11/18.
 */
public class GameCompass {
	private final GamePlayer player;
	
	private int nextIndex = 0;
	private boolean canUse = true;
	
	public GameCompass(GamePlayer player) {
		this.player = player;
	}
	
	public boolean tryUse(ClickType click) {
		if (!canUse) return false;
		
		// Get compass list
		List<CompassLocation> locations = GameMap.getCurrentMap().getCompassLocations();
		
		// Change index
		if (click.isRightClick()) {
			if (player.getPlayer().isSneaking()) {
				nextIndex = (nextIndex == 0 ? locations.size() - 1 : nextIndex - 1);
			} else {
				nextIndex = (nextIndex + 1) % locations.size();
			}
		}
		
		// Get new compass location
		CompassLocation cl = locations.get(nextIndex);
		
		// Set location
		player.sendTitleMessage(ChatColor.LIGHT_PURPLE + cl.getName());
		player.getPlayer().setCompassTarget(cl.getLocation());
		
		// Lock to prevent 'double clicking'
		canUse = false;
		new BukkitRunnable() {
			@Override public void run() {canUse = true;}
		}.runTaskLater(NightfallPlugin.getPlugin(), 4);
		
		return true;
	}
}
