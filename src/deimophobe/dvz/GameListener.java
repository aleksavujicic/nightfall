package deimophobe.dvz;

import deimophobe.dvz.blocks.BlockManager;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.Kit;
import deimophobe.dvz.dwarf.loadout.LoadoutMenu;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.monster.mob.Bopen;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class GameListener implements Listener {
	
	private Game game;
	private DwarfManager dm;
	private MonsterManager mm;
	
	public GameListener() {
		updateManagers();
	}
	
	public void updateManagers() {
		game = Game.getGame();
		dm = DwarfManager.getManager();
		mm = MonsterManager.getManager();
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ShrineManager.getManager().giveShrineBarToPlayer(player);
		
		if (dm.goOnline(player)) {
			game.updateDwarfCount();
			return;
		}
		if (mm.goOnline(player)) {
			return;
		}
		
		
		switch (game.getPhase()) {
			case STARTING:
				game.resetPlayer(player);
				break;
				
			case BUILD:
				dm.addGamePlayer(player);
				game.updateDwarfCount();
				break;
				
			case PLAGUE:
			case GAME:
			case END:
				mm.addGamePlayer(player);
				break;
		}
	}
	
	@EventHandler
	public void onLogoff(PlayerQuitEvent event) {
		boolean wasDwarf = dm.goOffline(event.getPlayer());
		mm.goOffline(event.getPlayer());
		
		if (wasDwarf)
			game.updateDwarfCount();
	}
	
	// --------------------------------------------------------
	//                        EVENTS
	// --------------------------------------------------------
	
	@EventHandler
	public void updateHotbarSlot(PlayerItemHeldEvent event) {
		GamePlayer gp = game.getGamePlayer(event.getPlayer());
		if (gp != null) {
			int slot = event.getNewSlot();
			gp.updateHotbarSlot(event.getPlayer().getInventory().getItem(slot), slot);
		}
	}
	
	
	@EventHandler
	public void useItems(PlayerInteractEvent event) {
		GamePlayer gp = game.getGamePlayer(event.getPlayer());
		if (gp != null) {
			
			if ((gp instanceof MonsterPlayer) && ((MonsterPlayer) gp).isFrozen()) {
				event.setCancelled(true);
				return;
			}
			
			Block block = event.getClickedBlock();
			gp.onUse(event.getAction(), block, event.getBlockFace());
			TimedBlock.hitBlock(block, gp);
			
			if (block != null && block.getType() == Material.CHEST) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		GamePlayer gp = game.getGamePlayer(event.getPlayer());
		if (gp != null) {
			if ((gp instanceof MonsterPlayer) && ((MonsterPlayer) gp).isFrozen()) {
				event.setCancelled(true);
				return;
			}
			
			gp.onShift(event.isSneaking());
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		EntityDamageEvent.DamageCause cause = event.getCause();
		
		// Don't damage lobbyers and reset if void.
		if (entity instanceof Player && ((Player)entity).getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
			if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
				Game.getGame().resetPlayer((Player)event.getEntity());
			}
			return;
		}
		
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
			
			// Ignore if damager is frozen
			if ((damager instanceof MonsterPlayer) && ((MonsterPlayer) damager).isFrozen()) {
				event.setCancelled(true);
				return;
			}
			
			// Notify if not custom
			if (cause != EntityDamageEvent.DamageCause.CUSTOM)
				damagee.registerNonCustomDamage(damager, type);
			
			// Debug messages
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
			
			// Ignore crit
			/*
			if (damager instanceof GamePlayer) {
				Player damagerPl = ((GamePlayer) damager).getPlayer();
				Material material = damagerPl.getLocation().getBlock().getType();
				boolean crit = (
						(damagerPl.getFallDistance() > 0) &&
						(!damagerPl.isOnGround()) &&
						(material != Material.LADDER) &&
						(material != Material.VINE) &&
						(!damagerPl.getLocation().getBlock().isLiquid()) &&
						(!damagerPl.hasPotionEffect(PotionEffectType.BLINDNESS)) &&
						(damagerPl.getVehicle() == null) &&
						(!damagerPl.isSprinting())
				);
				
				if (crit)
					damage /= 1.5;
			}
			*/
			
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
			
			// Kill detection for dwarves before shrine falling
			if (Game.getGame().getPhase() == Phase.BUILD && damagee instanceof Dwarf) {
				double dmg = event.getFinalDamage();
				if (damagee.getHealth() - dmg <= 0.1 || type.isInstaKill()) {
					event.setDamage(0);
					event.setCancelled(true);
					
					((Dwarf) damagee).reset();
				}
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
				if ((gp instanceof MonsterPlayer) && ((MonsterPlayer) gp).isFrozen()) {
					event.setCancelled(true);
					return;
				}
				
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
	public void onProjectileLand(ProjectileHitEvent event) {
		Projectile proj = event.getEntity();
		ProjectileSource source = proj.getShooter();
		if (source instanceof Player && event.getHitBlock() != null) {
			GamePlayer player = game.getGamePlayer((Player) source);
			if (player != null) {
				player.onProjectileLand(event.getEntity(), event.getHitBlock());
				proj.remove();
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
			for (Dwarf dwarf2 : dm.getGamePlayers()) {
				dwarf2.notifyDeath(dwarf);
			}
			event.setDeathMessage(dwarf.generateDeathMsg());
			dm.removeGamePlayer(dwarf);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Phase phase = Game.getGame().getPhase();
		if (phase == Phase.STARTING) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Game.getGame().resetPlayer(event.getPlayer());
				}
			}.runTaskLater(Game.getGame().getPlugin(), 1);
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					mm.addGamePlayer(event.getPlayer());
					mm.getGamePlayer(event.getPlayer()).kill();
				}
			}.runTaskLater(Game.getGame().getPlugin(), 1);
			
			event.setRespawnLocation(ShrineManager.getManager().getCurrentMobspawn());
		}
	}
	
	@EventHandler
	public void onAllDeaths(EntityDeathEvent event) {
		event.getDrops().clear();
	}
	
	
	
	// --------------------------------------------------------
	//                        MISC
	// --------------------------------------------------------
	
	@EventHandler
	public void preventFireSpread(BlockSpreadEvent event){
		if (event.getNewState().getType() == Material.FIRE) {
			event.setCancelled(true);
			if (event.getSource().getType() == Material.FIRE) {
				event.getSource().setType(Material.AIR);
			}
		}
	}
	
	@EventHandler
	public void preventBlockBurn(BlockBurnEvent event){
		event.setCancelled(true);
	}
	
	
	@EventHandler
	public void preventFlightChange(PlayerToggleFlightEvent event){
		if (mm.isGamePlayer(event.getPlayer())) {
			event.setCancelled(true);
			mm.getGamePlayer(event.getPlayer()).resetFrozen();
		}
	}
	
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
	public void preventPotionDrinking(PlayerItemConsumeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void preventInvClicking(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Player) {
			if (game.isPlayer((Player) holder) && (event.getSlot() == 40 || event.getSlotType() == InventoryType.SlotType.ARMOR)) {
				event.setCancelled(true);
			}
			if (((Player) holder).getGameMode() == GameMode.ADVENTURE) {
				event.setCancelled(true);
			}
		}
		
		// Shared chest handling - prevent putting undroppable items in chest
		if (dm.isSharedChest(event.getInventory())) {
			int button = event.getHotbarButton();
			ItemStack hotbarItem;
			if (button != -1)
				hotbarItem = event.getWhoClicked().getInventory().getItem(button);
			else
				hotbarItem = null;
			ItemStack clickedItem = event.getCurrentItem();
			
			if (!Kit.isDroppableItem(clickedItem) || !Kit.isDroppableItem(hotbarItem))
				event.setCancelled(true);
		}
		
		// Main DwarfData handling
		LoadoutMenu loadout = LoadoutMenu.getMenu();
		HumanEntity e = event.getWhoClicked();
		if (event.getInventory() != null && loadout.getTitle().equals(event.getInventory().getTitle()) && e instanceof Player) {
			if (event.getClickedInventory() != null && loadout.getTitle().equals(event.getClickedInventory().getTitle())) {
				loadout.select(event.getSlot(), (Player) e);
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventDropping(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE ||
				( dm.getGamePlayer(event.getPlayer()) != null && !Kit.isDroppableItem(event.getItemDrop().getItemStack()) ) ||
				mm.getGamePlayer(event.getPlayer()) != null)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void preventInvDragging(InventoryDragEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Player && game.isPlayer((Player) holder)) {
			if (event.getInventorySlots().contains(40))
				event.setCancelled(true);
		}
		
		LoadoutMenu loadout = LoadoutMenu.getMenu();
		HumanEntity e = event.getWhoClicked();
		if (event.getInventory() != null && loadout.getTitle().equals(event.getInventory().getTitle()) && e instanceof Player) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		boolean canBreak = BlockManager.getManager().breakBlockEvent(game.getGamePlayer(event.getPlayer()), event.getBlock());
		event.setCancelled(!canBreak);
	}
	
	@EventHandler
	public void onDismount(EntityDismountEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			MonsterPlayer monster = mm.getGamePlayer((Player) entity);
			if (monster != null && monster.getMob() instanceof Bopen) {
				((Bopen) monster.getMob()).dismountHorse();
			}
		}
	}
	
	
	// ------ MOB STUFF ------
	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		HumanEntity entity = event.getWhoClicked();
		if (entity.getType() == EntityType.PLAYER) {
			Player player = (Player) entity;
			MonsterPlayer monster = mm.getGamePlayer(player);
			
			if (monster != null) { //&& !mob.isAlive()) {
				//event.setCancelled(true);
				
				MonsterManager.getManager().onClick(event.getSlot(), event.getClickedInventory(), monster);
			}
		}
	}
	
	@EventHandler
	public void deadLRClick(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		boolean succ = tryShowMobMenu(player);
		if (succ) event.setCancelled(true);
	}
	
	@EventHandler
	public void deadLRClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		boolean succ = tryShowMobMenu(player);
		if (succ) event.setCancelled(true);
	}
	
	private boolean tryShowMobMenu(Player player) {
		MonsterPlayer monster = mm.getGamePlayer(player);
		if (monster != null && !monster.isAlive()) {
			mm.showMobMenu(monster);
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void preventAIBurning(EntityCombustEvent event) {
		if (event.getEntityType() == EntityType.ZOMBIE)
			event.setCancelled(true);
		
		Entity entity = event.getEntity();
		if (entity instanceof Player && mm.isGamePlayer((Player) entity))
			event.setCancelled(true);
	}
}
