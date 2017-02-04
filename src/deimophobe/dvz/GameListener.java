package deimophobe.dvz;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.timedblock.TimedBlock;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class GameListener implements Listener {
	
	private final Game game;
	private final DwarfManager dm;
	private final MonsterManager mm;
	
	public GameListener() {
		game = Game.getGame();
		dm = DwarfManager.getManager();
		mm = MonsterManager.getManager();
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		game.giveBossBarToPlayer(event.getPlayer());
		
		if (dm.goOnline(event.getPlayer())) {
			game.updateSidebar();
			return;
		}
		if (mm.goOnline(event.getPlayer())) {
			game.updateSidebar();
			return;
		}
		
		
		switch (game.getPhase().playerTypeOnJoin()) {
			case DWARF:
				dm.addGamePlayer(event.getPlayer());
				break;
			case MOB:
				mm.addGamePlayer(event.getPlayer());
				break;
			case NONE:
				break;
		}
		game.updateSidebar();
	}
	
	@EventHandler
	public void onLogoff(PlayerQuitEvent event) {
		dm.goOffline(event.getPlayer());
		mm.goOffline(event.getPlayer());
		game.updateSidebar();
	}
	
	// --------------------------------------------------------
	//                        EVENTS
	// --------------------------------------------------------
	
	
	@EventHandler
	public void useItems(PlayerInteractEvent event) {
		GamePlayer gp = game.getGamePlayer(event.getPlayer());
		if (gp != null) {
			Block block = event.getClickedBlock();
			gp.onUse(event.getAction(), block, event.getBlockFace());
			TimedBlock.hitBlock(block, gp);
		}
	}
	
	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		GamePlayer gp = game.getGamePlayer(event.getPlayer());
		if (gp != null)
			gp.onShift(event.isSneaking());
	}
	
	
	
	private static final EntityDamageEvent.DamageCause[] IMMUNE_CAUSES = {
			EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
			EntityDamageEvent.DamageCause.STARVATION,
			EntityDamageEvent.DamageCause.DROWNING,
			EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
			EntityDamageEvent.DamageCause.FALL,
			EntityDamageEvent.DamageCause.SUFFOCATION,
			EntityDamageEvent.DamageCause.LAVA,
			EntityDamageEvent.DamageCause.HOT_FLOOR,
			EntityDamageEvent.DamageCause.FIRE_TICK,
			EntityDamageEvent.DamageCause.FIRE,
	};
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		EntityDamageEvent.DamageCause cause = event.getCause();
		
		// Special cases for void/suffocation/starvation.
		if (event.getEntity().getType() == EntityType.PLAYER) {
			// Instakill if in survival and void damage
			if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
				if (((Player) event.getEntity()).getGameMode() == GameMode.SURVIVAL) {
					event.setDamage(10000);
				} else {
					event.setDamage(0);
					event.setCancelled(true);
				}
			}
		}
		
		// Ignore starvation/suffocation
		if (cause == EntityDamageEvent.DamageCause.STARVATION || cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
			event.setDamage(0);
			event.setCancelled(true);
			return;
		}
		
		// The grunt of the work
		GameEntity damagee = game.getGameEntity(event.getEntity());
		if (damagee != null) {
			double damage = event.getDamage();
			
			DamageType type = null;
			GameEntity damager = null;
			
			// Work out what caused it, and allocate the appropriate damagee and damager
			switch (cause) {
				case STARVATION:
				case SUFFOCATION:
					Bukkit.broadcastMessage("Failed to cancel starve/suffocate damage?!");
					return;
					
				case VOID:
					type = DamageType.VOID;
					break;
				case CUSTOM:
					type = damagee.getLastDamageType();
					damager = damagee.getLastDamager();
					break;
				
				case CONTACT: type = DamageType.CONTACT; break;
				case DROWNING: type = DamageType.DROWNING; break;
				case FALL: type = DamageType.FALL; break;
				case HOT_FLOOR: type = DamageType.HOT_FLOOR; break;
				case CRAMMING: type = DamageType.CRAMMING; break;
				case FALLING_BLOCK: type = DamageType.FALLING_BLOCK; break;
				case LIGHTNING: type = DamageType.LIGHTNING; break;
				case LAVA: type = DamageType.LAVA; break;
				
				case FIRE:
				case FIRE_TICK:
					type = DamageType.FIRE; break;
				
				case BLOCK_EXPLOSION:
				case ENTITY_EXPLOSION:
					type = DamageType.EXPLOSION; break;
					
				case ENTITY_ATTACK:
				case ENTITY_SWEEP_ATTACK:
					type = DamageType.REGULAR_MELEE;
					damager = game.getGameEntity( ((EntityDamageByEntityEvent) event).getDamager() );
					break;
					
				case PROJECTILE:
					type = DamageType.REGULAR_RANGED;
					Projectile proj = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
					damager = game.getGameEntity((Entity) proj.getShooter());
					break;
					
				case POISON:
				case WITHER:
					type = DamageType.POISON;
					break;
					
				default:
					Bukkit.broadcastMessage("Unhandled damage: " + cause);
					break;
			}
			if (cause != EntityDamageEvent.DamageCause.CUSTOM)
				damagee.registerNonCustomDamage(damager, type);
			//if (damager != null)
			//	Bukkit.broadcastMessage(damage + " damage type " + cause + " to " + damagee.getName() + " by " + damager.getName());
			//else
			//	Bukkit.broadcastMessage(damage + " damage type " + cause + " to " + damagee.getName() + " by null");
			
						
			// Prevent mobs hitting ais
			if (damager instanceof MonsterPlayer && damagee instanceof AIEntity) {
				event.setCancelled(true);
				return;
			}
			
			// Ignore blocking and armor damage
			if (event.getEntityType() == EntityType.PLAYER)
				event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
			event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
			
			// Notify both parties about the damage
			if (damager != null) {
				damage = damager.onHit(damagee, type, damage);
			}
			//Bukkit.broadcastMessage("After hit " + damage);
			if (damagee != null && damage != -1) {
				damage = damagee.onGotHit(damager, type, damage);
			}
			//Bukkit.broadcastMessage("After gothit " + damage);
			
			// Apply force calculations to arrow
			if (type == DamageType.REGULAR_RANGED && damage != -1) {
				Entity arrow = ((EntityDamageByEntityEvent) event).getDamager();
				if (arrow.hasMetadata("force")) {
					damage *= arrow.getMetadata("force").get(0).asDouble();
				} else {
					Bukkit.getLogger().warning("Arrow has no attached force?");
				}
			}
			//Bukkit.broadcastMessage("After arrow calc " + damage);
			
			// apply damage and cancel if -1
			if (damage == -1) {
				event.setDamage(0);
				event.setCancelled(true);
			} else {
				event.setDamage(damage);
			}
			
			// Kill detection for monsters and AIs
			if (damagee instanceof MonsterPlayer || damagee instanceof  AIEntity) {
				double dmg = event.getFinalDamage();
				if (damagee.getHealth() - dmg <= 0.1 || type.isInstaKill()) {
					
					// Prevent killing a monster and set to spectator instead
					if (damagee instanceof MonsterPlayer) {
						((MonsterPlayer)damagee).kill();
						event.setDamage(0);
					}
					
					// Notify dwarf if there is one
					if (damager instanceof Dwarf) {
						((Dwarf)damager).onKill(damagee, type);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onArrowFire(EntityShootBowEvent event) {
		if (event.getEntity().getType() == EntityType.PLAYER) {
			GamePlayer gp = game.getGamePlayer((Player) event.getEntity());
			if (gp != null) {
				Entity proj = event.getProjectile();
				if (proj != null && proj.getType() == EntityType.ARROW) {
					Arrow arrow = (Arrow) proj;
					
					// Prevent pickup
					arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
					
					// Translate arrow to behave like 1.8
					double yaw = arrow.getLocation().getYaw() * Math.PI/180;
					arrow.teleport(arrow.getLocation().add(-0.15*Math.cos(yaw), 0, 0.15*Math.sin(yaw)));
					
					// Label it with force
					arrow.setMetadata("force", new FixedMetadataValue(game.getPlugin(), event.getForce()));
					
					// FIRE
					Projectile newProj = gp.onBowFire(arrow, event.getForce());
					event.setProjectile(newProj);
				}
			}
		}
	}
	
	@EventHandler
	public void onArrowLand(ProjectileHitEvent event) {
		Projectile proj = event.getEntity();
		if (proj.getType() == EntityType.ARROW) {
			ProjectileSource source = proj.getShooter();
			if (source instanceof Player) {
				GamePlayer player = game.getGamePlayer((Player) source);
				if (player != null) {
					player.onArrowLand((Arrow) event.getEntity(), event.getHitBlock());
				}
			}
		}
	}
	
	
	
	// --------------------------------------------------------
	//                        DEATH
	// --------------------------------------------------------
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Dwarf dwarf = dm.getGamePlayer(event.getEntity());
		if (dwarf != null) {
			event.setDeathMessage(dwarf.generateDeathMsg());
			dm.removeGamePlayer(dwarf);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		boolean success = mm.addGamePlayer(event.getPlayer());
		event.setRespawnLocation(game.getCurrentMobspawn());
	}
	
	@EventHandler
	public void onAllDeaths(EntityDeathEvent event) {
		event.getDrops().clear();
	}
	
	
	
	// --------------------------------------------------------
	//                        MISC
	// --------------------------------------------------------
	
	@EventHandler
	public void preventMobPickup(PlayerPickupItemEvent event){
		if (mm.isGamePlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSwapHand(PlayerSwapHandItemsEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void preventInvClicking(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Player && DwarfManager.getManager().isGamePlayer((Player) holder)) {
			if (event.getSlot() == 40 || event.getSlotType() == InventoryType.SlotType.ARMOR) {
				event.setCancelled(true);
			}
		}
	}
	
	private static final Material[] FIXED_BLOCKS = {
			Material.LOG,
			Material.LOG_2,
			Material.SPONGE,
			Material.IRON_FENCE,
			Material.JACK_O_LANTERN,
			Material.RAILS,
			Material.ACTIVATOR_RAIL,
			Material.DETECTOR_RAIL,
			Material.POWERED_RAIL,
			Material.LADDER,
			Material.REDSTONE_TORCH_ON,
			Material.REDSTONE_TORCH_OFF,
			Material.PISTON_BASE,
			Material.PISTON_EXTENSION,
			Material.PISTON_STICKY_BASE,
			Material.PISTON_MOVING_PIECE,
			Material.IRON_BLOCK,
	};
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Material blockType = event.getBlock().getType();
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			for (Material material : FIXED_BLOCKS) {
				if (material == blockType)
					event.setCancelled(true);
			}
		}
		
		if (blockType == Material.GOLD_ORE) {
			game.mineGold();
		}
		
		if (blockType == Material.GRAVEL) {
			Dwarf dwarf = dm.getGamePlayer(event.getPlayer());
			if (dwarf != null)
				dwarf.mineGravel();
		}
	}
}
