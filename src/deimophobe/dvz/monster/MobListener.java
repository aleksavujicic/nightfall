package deimophobe.dvz.monster;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class MobListener implements Listener {
	
	/*@EventHandler
	public void onInvClose(InventoryCloseEvent event) {
		if (MobManager.getManager().isMobSpawnMenu(event.getInventory())) {
			final MonsterPlayer mob = MobManager.getManager().getMob((Player) event.getGamePlayer());
			if (!mob.isAlive()) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!mob.isAlive())
							mob.showMobMenu();
					}
				}.runTaskLater(Game.getGame().getPlugin(), 1);
			}
		}
	}*/
	
	private static final MobManager mm = MobManager.getManager();
	
	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		HumanEntity entity = event.getWhoClicked();
		if (entity.getType() == EntityType.PLAYER) {
			Player player = (Player) entity;
			MonsterPlayer monster = mm.getMob(player);
			
			if (monster != null) { //&& !mob.isAlive()) {
				event.setCancelled(true);
				
				boolean closeInv = MobManager.getManager().onClick(event.getSlot(), event.getClickedInventory(), monster);
				if (closeInv)
					player.closeInventory();
			}
		}
	}
	
	@EventHandler
	public void deadLRClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		MonsterPlayer monster = mm.getMob(player);
		if (monster != null && !monster.isAlive()) {
			mm.showMobMenu(monster);
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventDropping(PlayerDropItemEvent event) {
		if (mm.getMob(event.getPlayer()) != null)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void preventAIBurning(EntityCombustEvent event) {
		if (event.getEntityType() == EntityType.ZOMBIE)
			event.setCancelled(true);
	}
}
