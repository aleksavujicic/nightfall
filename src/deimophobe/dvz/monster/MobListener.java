package deimophobe.dvz.monster;

import deimophobe.dvz.monster.spawnmenu.SpawnManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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
		LivingEntity entity = event.getWhoClicked();
		if (entity.getType() == EntityType.PLAYER) {
			Player player = (Player) entity;
			final MonsterPlayer mob = mm.getMob(player);
			if (mob != null) {
				if (SpawnManager.getManager().isMobSpawnMenu(event.getInventory())) {
					if (event.getSlot() == -999) {
						event.setCancelled(true);
					} else {
						SpawnManager.getManager().spawnMob(event.getSlot(), mob);
					}
				} else {
					event.setCancelled(true);
					player.closeInventory();
					mob.showMobMenu();
				}
			}
		}
	}
	
	@EventHandler
	public void deadLRClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		MonsterPlayer mob = mm.getMob(player);
		if (mob != null && !mob.isAlive()) {
			mob.showMobMenu();
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
