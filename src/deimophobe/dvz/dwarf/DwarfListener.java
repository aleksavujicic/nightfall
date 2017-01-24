package deimophobe.dvz.dwarf;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.dwarf.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DwarfListener implements Listener {
	
	private final static DwarfManager dm = DwarfManager.getManager();
	
	@EventHandler
	public void onGotHit(EntityDamageEvent event) {
		Entity damagee = event.getEntity();
		
		if (damagee.getType() == EntityType.PLAYER) {
			//Dwarf dwarf = dm.getDwarf((Player) damagee);
			if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION || event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void preventInvDragging(InventoryDragEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Player && dm.isDwarf((Player) holder)) {
			if (event.getInventorySlots().contains(40)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void preventDropping(PlayerDropItemEvent event) {
		if (dm.getDwarf(event.getPlayer()) != null && !Kit.isDroppableItem(event.getItemDrop().getItemStack()))
			event.setCancelled(true);
	}
}
