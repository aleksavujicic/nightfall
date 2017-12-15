package deimophobe.nightfall.lobby;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 2/11/17.
 */
public class LobbyListener implements Listener, PluginMessageListener {
	
	@EventHandler
	public void portalJoinEvent(EntityPortalEnterEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			player.teleport(player.getLocation().subtract(0,300,0));
			
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("NightfallGame1");
			
			player.sendPluginMessage(NightfallLobbyPlugin.getPlugin(), "BungeeCord", out.toByteArray());
		}
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("SomeSubChannel")) {
			// Use the code sample in the 'Response' sections below to read
			// the data.
		}
	}
	
	// =====================================
	// ===        Event Canceling        ===
	// =====================================
	
	
	// ---- Player Events ----
	
	private void resetPlayer(Player player, boolean teleport) {
		if (player.isDead())
			player.spigot().respawn();
		
		if (teleport)
			player.teleport(player.getWorld().getSpawnLocation());
		player.getInventory().clear();
		for (PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(GameMode.ADVENTURE);
		double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		player.setHealth(maxHealth);
		player.setSaturation(100000);
		player.setFoodLevel(100000);
		player.setExp(0);
		player.setLevel(0);
		player.setDisplayName(player.getName());
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event) {
		event.setJoinMessage("");
		resetPlayer(event.getPlayer(), true);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		event.setRespawnLocation(player.getWorld().getSpawnLocation());
		resetPlayer(event.getPlayer(), false);
	}
	
	@EventHandler
	public void resetVoidPlayerOnSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if (player.getLocation().getY() <= -500) {
			resetPlayer(player, true);
		}
	}
	
	
	private <PC extends PlayerEvent & Cancellable> void cancelNonCreativePlayerEvent(PC event) {
		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler public void onPlayerArmorStand(PlayerArmorStandManipulateEvent event) { cancelNonCreativePlayerEvent(event); }
	@EventHandler public void onPlayerBedEvent(PlayerBedEnterEvent event) { cancelNonCreativePlayerEvent(event); }
	@EventHandler public void onPlayerDropItem(PlayerDropItemEvent event) { cancelNonCreativePlayerEvent(event); }
	@EventHandler public void onPlayerInteract(PlayerInteractEntityEvent event) { cancelNonCreativePlayerEvent(event); }
	@EventHandler public void onPlayerInteract(PlayerInteractAtEntityEvent event) { cancelNonCreativePlayerEvent(event); }
	@EventHandler public void onPlayerItemConsume(PlayerItemConsumeEvent event) { cancelNonCreativePlayerEvent(event); }
	@EventHandler public void onPlayerShearEntity(PlayerShearEntityEvent event) { cancelNonCreativePlayerEvent(event); }
	@EventHandler public void onPlayerSwapHand(PlayerSwapHandItemsEvent event) { cancelNonCreativePlayerEvent(event); }
	
	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE) {
			event.setAmount(0);
		}
	}
	
	private static final Set<Material> interactable = new HashSet<>();
	static {
		interactable.add(Material.ACACIA_DOOR);
		interactable.add(Material.BIRCH_DOOR);
		interactable.add(Material.DARK_OAK_DOOR);
		interactable.add(Material.WOOD_DOOR);
		interactable.add(Material.WOODEN_DOOR);
		interactable.add(Material.JUNGLE_DOOR);
		interactable.add(Material.SPRUCE_DOOR);
		interactable.add(Material.TRAP_DOOR);
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		cancelNonCreativePlayerEvent(event);
		
		Block block = event.getClickedBlock();
		if (block == null) return;
		
		Material blockType = block.getType();
		if (interactable.contains(blockType)) {
			event.setCancelled(false);
		}
	}
	
	@EventHandler
	public void onPlayerUnleash(PlayerUnleashEntityEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	
	// ---- Entity Events ----
	private <PC extends EntityEvent & Cancellable> void cancelNonCreativeEntityEvent(PC event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}
	
	@EventHandler public void onEntityAirChange(EntityAirChangeEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityBreed(EntityBreedEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityChangeBlock(EntityChangeBlockEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityCombust(EntityCombustEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityCreatePortal(EntityCreatePortalEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityDamage(EntityDamageEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityInteract(EntityInteractEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityPickupItem(EntityPickupItemEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityPortal(EntityPortalEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityShootBow(EntityShootBowEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntityTame(EntityTameEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onEntity(EntityTargetEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onFoodChange(FoodLevelChangeEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onPotionLinger(LingeringPotionSplashEvent event) { cancelNonCreativeEntityEvent(event); }
	@EventHandler public void onSpawnerSpawn(SpawnerSpawnEvent event) { cancelNonCreativeEntityEvent(event); }
	
	
	// ---- Hanging Events ----
	@EventHandler
	public void onHangBreak(HangingBreakEvent event) {
		if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHangBreakEntity(HangingBreakByEntityEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}
	
}
