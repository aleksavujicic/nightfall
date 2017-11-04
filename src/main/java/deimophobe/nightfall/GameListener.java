package deimophobe.nightfall;

import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.damage.DamageManager;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.loadout.LoadoutMenu;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.Bopen;
import deimophobe.nightfall.monster.mob.Goblin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
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

	public GameListener() {}
	
	public void updateManagers() {
		game = Game.getGame();
		dm = DwarfManager.getManager();
		mm = MonsterManager.getManager();
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024);
		Game.getGame().giveShrineBarToPlayer(player);
		Game.getGame().giveScoreboard(player);
		
		if (player.isDead())
			return;
		
		if (dm.goOnline(player)) {
			game.updateDwarfCount();
			return;
		}
		if (mm.goOnline(player)) {
			return;
		}
		
		game.resetPlayer(player);
	}
	
	@EventHandler
	public void onLogoff(PlayerQuitEvent event) {
		boolean wasDwarf = dm.goOffline(event.getPlayer());
		mm.goOffline(event.getPlayer());
		game.unreadyPlayer(event.getPlayer());
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
		Block block = event.getClickedBlock();
		GamePlayer gp = game.getGamePlayer(event.getPlayer());
		if (gp != null && event.getAction() != Action.PHYSICAL) {
			
			if ((gp instanceof MonsterPlayer) && ((MonsterPlayer) gp).isFrozen()) {
				event.setCancelled(true);
				return;
			}
			
			if (block == null)
				block = gp.getTargetBlock(null, 5);
			gp.onUse(event.getAction(), block, event.getBlockFace());
			
			TimedBlock.hitBlock(block, gp);
		}
		
		if (block != null && BlockType.UNINTERACTABLE_BLOCKS.matchesBlock(block)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE && !GameMap.getCurrentMap().isBlockPlaceable(block)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLeftClick(PlayerAnimationEvent event) {
		GamePlayer gp = game.getGamePlayer(event.getPlayer());
		if (gp != null) {
			gp.onUse(Action.LEFT_CLICK_AIR, gp.getTargetBlock(null, 5), null);
		}
	}
	
	@EventHandler
	public void cancelSheepClick(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.SHEEP) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void cancelArmourStandClick(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
			event.setCancelled(true);
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
		
		// Don't damage lobbyers and respawn if void.
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (game.isLobbyPlayer(player)) {
				event.setCancelled(true);
				event.setDamage(0);
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
					game.resetPlayer(player);
					
					new BukkitRunnable() {
						@Override
						public void run() {
							game.resetPlayer(player);
						}
					}.runTaskLater(NightfallPlugin.getPlugin(), 20);
				}
				return;
			}
		}
		
		// Special cases for void/suffocation/starvation.
		if (event.getEntity().getType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			// Instakill if in survival and void damage
			if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
				if (player.getGameMode() == GameMode.SURVIVAL) {
					event.setDamage(10000);
				} else {
					event.setDamage(0);
					event.setCancelled(true);
					return;
				}
			}
		}
		
		// Ignore starvation/suffocation/thorns
		switch (cause) {
			case STARVATION:
			case SUFFOCATION:
			case THORNS:
			case BLOCK_EXPLOSION:
			case ENTITY_EXPLOSION:
			case ENTITY_SWEEP_ATTACK:
				event.setDamage(0);
				event.setCancelled(true);
				return;
		}
		
		GameEntity damagee = game.getGameEntity(event.getEntity());
		if (damagee != null)
			DamageManager.getManager().processDamageEvent(event);
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
					
					// Label it with force and damage
					ArrowMisc.setArrowForce(arrow, event.getForce());
					ArrowMisc.setArrowDamage(arrow, 0);
					
					// FIRE
					Projectile newProj = gp.onBowFire(arrow, event.getForce());
					
					if (newProj == null) {
						event.setCancelled(true);
					} else if (newProj instanceof Arrow && ArrowMisc.getArrowDamage((Arrow) newProj) == 0) {
						Bukkit.getLogger().severe("Arrow fired with 0 damage - meaning game player did not update!\nGameplayer: " + gp.getName() + " (" + gp.getDisplayName() + ").");
						event.setCancelled(true);
					} else {
						event.setProjectile(newProj);
					}
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

	@EventHandler
	public void thrownGoboBoxExplosion(EntityExplodeEvent event) {
		event.setCancelled(true);
		Location centerLoc = event.getLocation();
		World world = centerLoc.getWorld();
		world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 3, 1, 1, 1);
		world.playSound(centerLoc, "entity.generic.explode", 2, 1);
		Object thrower = event.getEntity().getMetadata("thrower").get(0).value();
		((Goblin)thrower).thrownGoboBox(centerLoc);
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
			event.setDeathMessage(dwarf.getDeathMessage());
			
			if (Game.getGame().getPhase() == Phase.GAME) {
				for (Player player : Bukkit.getOnlinePlayers())
					player.sendTitle("", dwarf.getDisplayName() + ChatColor.DARK_RED + " has fallen!", 20, 60, 20);
			}
			
			// Delayed to prevent concurrent modification exceptions hopefully ._.
			new BukkitRunnable() {
				@Override public void run() {dm.removeGamePlayer(dwarf, true);}
			}.runTaskLater(NightfallPlugin.getPlugin(), 1);
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
			}.runTaskLater(NightfallPlugin.getPlugin(), 10);
			
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					MonsterPlayer mp = mm.addGamePlayer(event.getPlayer());
					if (mp != null)
						mp.kill(true);
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), 10);
		}
		event.setRespawnLocation(GameMap.getCurrentMap().getLobbySpawn());
	}
	
	@EventHandler
	public void onAllDeaths(EntityDeathEvent event) {
		event.getDrops().clear();
	}
	
	
	
	// --------------------------------------------------------
	//                        MISC
	// --------------------------------------------------------
	
	// Blocks
	@EventHandler
	public void preventFireSpread(BlockSpreadEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void preventBlockBurn(BlockBurnEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void preventIceMelt(BlockFadeEvent event) {
		if (event.getNewState().getType() == Material.STATIONARY_WATER)
			event.setCancelled(true);
		
		// Prevent snow melt too
		if (event.getBlock().getType() == Material.SNOW)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		
		Block block = event.getBlock();
		GameMap map = GameMap.getCurrentMap();
		
		if (!map.isBlockBreakable(block)) {
			event.setCancelled(true);
		}
		
		GamePlayer gp = game.getGamePlayer(event.getPlayer());
		if (gp != null)
			gp.onBlockBreak(event.getBlock(), !event.isCancelled());
	}
	
	@EventHandler
	public void preventWaterFlow(BlockFromToEvent event) {
		Block toBlock = event.getToBlock();
		if (event.getBlock().getType() == Material.STATIONARY_WATER) {
			if (!toBlock.getRelative(0,-1,0).getType().isSolid()) return;
			
			int numFaceWaterBlocks = 0;
			if (toBlock.getRelative(1,0,0).getType() == Material.STATIONARY_WATER)
				numFaceWaterBlocks++;
			if (toBlock.getRelative(-1,0,0).getType() == Material.STATIONARY_WATER)
				numFaceWaterBlocks++;
			if (toBlock.getRelative(0,0,1).getType() == Material.STATIONARY_WATER)
				numFaceWaterBlocks++;
			if (toBlock.getRelative(0,0,-1).getType() == Material.STATIONARY_WATER)
				numFaceWaterBlocks++;
			
			if (numFaceWaterBlocks >= 2) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void disablePortalTravel(PlayerPortalEvent event) {
		event.setCancelled(true);
	}
	
	
	// Inventory/Items
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
		if (event.isShiftClick() && event.getCurrentItem().getType() == Material.SHIELD)
			event.setCancelled(true);
		
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
			
			if (!DwarvenItems.isDroppableItem(clickedItem) || !DwarvenItems.isDroppableItem(hotbarItem)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void preventDropping(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE ||
				( dm.getGamePlayer(event.getPlayer()) != null && !DwarvenItems.isDroppableItem(event.getItemDrop().getItemStack()) ) ||
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
	
	
	// ------ MOB STUFF ------
	@EventHandler
	public void deadLRClick(PlayerAnimationEvent event) {
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
	@EventHandler
	public void preventFlightChange(PlayerToggleFlightEvent event){
		if (mm.isGamePlayer(event.getPlayer())) {
			event.setCancelled(true);
			mm.getGamePlayer(event.getPlayer()).resetFrozen();
		}
	}
	
	@EventHandler
	public void preventMobPickup(EntityPickupItemEvent event){
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player && mm.isGamePlayer((Player) entity)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventAITarget(EntityTargetEvent event) {
		Entity target = event.getTarget();
		if (target instanceof Player && mm.isGamePlayer((Player) target))
			event.setCancelled(true);
	}
	
	
	// Other Miscellani
	@EventHandler
	public void preventPaintingsDestroy(HangingBreakByEntityEvent event) {
		Entity remover = event.getRemover();
		// Cancel event if not destroyed by player in creative
		if (!(remover instanceof Player && ((Player) remover).getGameMode() == GameMode.CREATIVE))
			event.setCancelled(true);
	}
}
