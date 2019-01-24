package deimophobe.nightfall.game;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.event.HatChangeEvent;
import deimophobe.nightfall.common.event.TitleChangeEvent;
import deimophobe.nightfall.common.util.Keys;
import deimophobe.nightfall.damage.DamageUtil;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.game.entity.GameEntityShooter;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.game.entity.GameShooter;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.mob.Bopen;
import deimophobe.nightfall.monster.mob.Goblin;
import deimophobe.nightfall.util.ArrowMisc;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class GameListener implements Listener {
	
	private final Game game;
	private final DwarfManager dwarfManager;
	private final MonsterManager monsterManager;

	public GameListener() {
		game = Game.getGame();
		dwarfManager = DwarfManager.getManager();
		monsterManager = MonsterManager.getManager();
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		game.goOnline(player);
	}
	
	@EventHandler
	public void onLogoff(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		Entity vehicle = player.getVehicle();
		if (vehicle != null) vehicle.removePassenger(player);
		
		game.goOffline(player);
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
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		BlockFace blockFace = event.getBlockFace();
		Action action = event.getAction();
		GamePlayer gp = game.getGamePlayer(player);
		
		if (gp != null && action != Action.PHYSICAL) {
			
			// Prevent frozen mobs using stuff
			if ((gp instanceof MonsterPlayer) && ((MonsterPlayer) gp).isFrozen()) {
				event.setCancelled(true);
				return;
			}
			
			if (block == null) block = gp.getTargetBlock(null, 5);
			ClickType click = ClickType.fromAction(action);
			gp.onUse(click, block, blockFace);
			
			BlockManager.getManager().hitBlock(block, gp, click, blockFace);
		}
		
		if (block != null && NFBlocks.UNINTERACTABLE_BLOCKS.matchesBlock(block) && player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE) return;
		
		if (!GameMap.getCurrentMap().isBlockPlaceable(block)) {
			event.setCancelled(true);
		}
		
		// Hack to prevent plagued zombies placing blocks
		if (MonsterManager.getManager().isGamePlayer(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLeftClick(PlayerAnimationEvent event) {
		GamePlayer gp = game.getGamePlayer(event.getPlayer());
		if (gp != null) {
			gp.onUse(ClickType.LEFT, gp.getTargetBlock(null, 5), null);
		}
	}
	
	@EventHandler
	public void cancelSheepClick(PlayerInteractEntityEvent event) {
		switch (event.getRightClicked().getType()) {
			case SHEEP:
			case HORSE:
			case SKELETON_HORSE:
			case ZOMBIE_HORSE:
			case VILLAGER:
			case ITEM_FRAME:
			case MINECART_HOPPER: {
				event.setCancelled(true);
				break;
			}
			
			
			case MINECART_CHEST: {
				event.setCancelled(true);
				
				Player player = event.getPlayer();
				Dwarf dwarf = DwarfManager.getManager().getGamePlayer(player);
				if (dwarf == null) break;
				
				dwarf.showSharedChest();
				break;
			}
			
			case MINECART_FURNACE: {
				event.setCancelled(true);
				
				Player player = event.getPlayer();
				Dwarf dwarf = DwarfManager.getManager().getGamePlayer(player);
				if (dwarf == null) break;
				
				dwarf.interactFurnace();
				break;
			}
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
	
	@EventHandler
	public void onSwim(EntityToggleSwimEvent event) {
		Entity entity = event.getEntity();
		EntityType type = event.getEntityType();
		if (type != EntityType.PLAYER) return;
		
		GamePlayer gamePlayer = game.getGamePlayer((Player) entity);
		if (gamePlayer == null) return;
		
		gamePlayer.onSwim(event.isSwimming());
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		EntityDamageEvent.DamageCause cause = event.getCause();
		
		// Protect santa
		switch (entity.getType()) {
			case ARMOR_STAND:
			case ITEM_FRAME:
			case MINECART_FURNACE:
			case MINECART_CHEST: {
				event.setCancelled(true);
				return;
			}
		}
		
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
					}.runTaskLater(NightfallPlugin.getPlugin(), 1);
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
		
		if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
			AIEntity ai = AIManager.getManager().getAI(entity);
			if (ai != null) {
				ai.suffocationTick();
				event.setDamage(0);
				event.setCancelled(true);
				return;
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
			DamageUtil.processDamageEvent(event);
	}
	
	@EventHandler
	public void onArrowFire(EntityShootBowEvent event) {
		Entity entity = event.getEntity();
		GameEntity<?> gameEntity = game.getGameEntity(entity);
		if (gameEntity instanceof GameEntityShooter) {
			GameEntityShooter<?> shooter = (GameEntityShooter) gameEntity;
			Entity proj = event.getProjectile();
			if (proj != null && proj.getType() == EntityType.ARROW) {
				Arrow arrow = (Arrow) proj;
				final float force = event.getForce();
				
				// Set appropriate velocity
				double speed = arrow.getVelocity().length();
				Vector velocity = shooter.getEyeLocation().getDirection();
				velocity.multiply(speed);
				arrow.setVelocity(velocity);
				
				// Set appropriate spawn location
				Location location = arrow.getLocation();
				location.setDirection(velocity);
				Misc.moveLocation(location, 0.3, 0.15);
				
				// Rotate correctly: note that minecraft is dumb and the facing is direction is inverted for X and Y (BUT NOT Z)
				Vector facing = velocity.clone();
				facing.setX(-facing.getX());
				facing.setY(-facing.getY());
				location.setDirection(facing);
				arrow.teleport(location);
				
				// Get bow and damage
				ItemStack bow = event.getBow();
				ItemMeta meta = bow.getItemMeta();
				CustomItemTagContainer container = meta.getCustomTagContainer();
				int damage = 0;
				if (container.hasCustomTag(Keys.BOW_POWER_KEY, ItemTagType.INTEGER)) {
					damage = container.getCustomTag(Keys.BOW_POWER_KEY, ItemTagType.INTEGER);
				}
				
				// Update arrow properties
				ArrowMisc.setArrowForce(arrow, force);
				ArrowMisc.setArrowDamage(arrow, damage);
				arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
				arrow.setBounce(false);
				
				// FIRE
				Projectile newProj = shooter.onBowFire(bow, arrow, force);
				
				if (newProj == null) {
					event.setCancelled(true);
				} else if (newProj instanceof Arrow && ArrowMisc.getArrowDamage((Arrow) newProj) == 0) {
					NightfallPlugin.logger().severe("Arrow fired with 0 damage - meaning game entity did not update arrow!\nGameEntity: " + gameEntity.getName() + " (" + gameEntity.getDisplayName() + ").");
					event.setCancelled(true);
				} else {
					event.setProjectile(newProj);
				}
			}
		} else {
			NightfallPlugin.logger().warning("Bow fired with non GameEntityShooter holder ('" + entity.getName() + "'). Wrapping arrow safely.");
			
			Entity proj = event.getProjectile();
			if (proj != null && proj.getType() == EntityType.ARROW) {
				Arrow arrow = (Arrow) proj;
				
				float force = event.getForce();
				double damage = arrow.spigot().getDamage();
				
				ArrowMisc.setArrowForce(arrow, force);
				ArrowMisc.setArrowDamage(arrow, damage);
				
				arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
				arrow.setBounce(false);
				event.setProjectile(arrow);
			}
		}
	}
	
	@EventHandler
	public void onProjectileLand(ProjectileHitEvent event) {
		Projectile proj = event.getEntity();
		ProjectileSource source = proj.getShooter();
		BlockFace hitFace = event.getHitBlockFace();
		
		if (source == null) return;
		if (source instanceof Entity) {
			GameEntity<?> gameSource = game.getGameEntity((Entity) source);
			if (gameSource instanceof GameShooter) {
				Entity hitEntity = event.getHitEntity();
				GameEntity<?> gameEntity = game.getGameEntity(hitEntity);
				
				((GameShooter) gameSource).onProjectileLand(event.getEntity(), event.getHitBlock(), hitFace, gameEntity);
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
		
		Entity exploder = event.getEntity();
		if (exploder.hasMetadata("thrower")) {
			Object thrower = exploder.getMetadata("thrower").get(0).value();
			exploder.removeMetadata("thrower", NightfallPlugin.getPlugin());
			
			if (thrower instanceof Goblin) {
				((Goblin) thrower).thrownGoboBox(centerLoc);
			}
		}
		event.getEntity().remove();
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
		if (gp != null) {
			boolean shouldBreak = gp.onBlockBreak(event.getBlock(), !event.isCancelled());
			event.setCancelled(!shouldBreak);
		}
	}
	
	// --------------------------------------------------------
	//                        DEATH
	// --------------------------------------------------------
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Dwarf dwarf = dwarfManager.getGamePlayer(player);
		if (dwarf != null) {
			for (Dwarf dwarf2 : dwarfManager.getGamePlayers()) {
				dwarf2.notifyDeath(dwarf);
			}
			event.setDeathMessage("");
			
			BaseComponent deathMessage = dwarf.getDeathMessage();
			Bukkit.spigot().broadcast(deathMessage);
			Bukkit.getConsoleSender().spigot().sendMessage(deathMessage);
			Game.getGame().getDeathTracker().registerDeathMessage(deathMessage);
			
			Phase phase = Game.getGame().getPhase();
			if (phase.isOrIsAfter(Phase.GAME)) {
				for (Player player1 : Bukkit.getOnlinePlayers()) {
					player1.sendTitle("", dwarf.getDisplayName() + ChatColor.DARK_RED + " has fallen!", 20, 60, 20);
				}
			}
			
			dwarfManager.removeGamePlayer(dwarf);
			
			if (phase.isOrIsAfter(Phase.BUILD)) {
				MonsterPlayer monster = new MonsterPlayer(player, false);
				monsterManager.registerGamePlayer(monster);
			}

			if (Game.getGame().getPhase() == Phase.PLAGUE) {
				Game.getGame().getPlague().onDwarfDeath(dwarf);
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Phase phase = Game.getGame().getPhase();
		Player player = event.getPlayer();
		
		GamePlayer gamePlayer = Game.getGame().getGamePlayer(player);
		if (gamePlayer != null) {
			event.setRespawnLocation(gamePlayer.getRespawnLocation());
			gamePlayer.doLater(gamePlayer::onRespawn, 1);
		} else {
			event.setRespawnLocation(GameMap.getCurrentMap().getLobbySpawn());
			
			if (phase.isBefore(Phase.PLAGUE)) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!player.isOnline()) return;
						Game.getGame().resetPlayer(player);
					}
				}.runTaskLater(NightfallPlugin.getPlugin(), 10);
			}
		}
		
	}
	
	@EventHandler
	public void onAllDeaths(EntityDeathEvent event) {
		event.getDrops().clear();
	}
	
	
	
	// --------------------------------------------------------
	//                       CUSTOM
	// --------------------------------------------------------
	
	@EventHandler
	public void changeTitle(TitleChangeEvent event) {
		Player player = event.getPlayer();
		Dwarf dwarf = DwarfManager.getManager().getGamePlayer(player);
		
		if (dwarf != null) {
			dwarf.updateTitle();
		} else if (game.isLobbyPlayer(player)) {
			event.setUpdateDisplayName(true);
		}
	}
	
	@EventHandler
	public void changeHat(HatChangeEvent event) {
		Player player = event.getPlayer();
		Dwarf dwarf = DwarfManager.getManager().getGamePlayer(player);
		
		if (dwarf != null) {
			dwarf.updateHat();
		} else if (game.isLobbyPlayer(player)) {
			event.setUpdateHat(true);
		}
	}
	
	
	
	// --------------------------------------------------------
	//                        MISC
	// --------------------------------------------------------
	
	
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
			if (game.isGamePlayer((Player) holder) && (event.getSlot() == 40 || event.getSlotType() == InventoryType.SlotType.ARMOR)) {
				event.setCancelled(true);
			}
			if (((Player) holder).getGameMode() == GameMode.ADVENTURE) {
				event.setCancelled(true);
			}
		}
		
		// Shared chest handling - prevent putting undroppable items in chest
		if (dwarfManager.isSharedChest(event.getInventory())) {
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
	
	// This cause /give to give players an extra item for some bizarre reason, not that it really matters
	@EventHandler
	public void preventDropping(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE ||
				( dwarfManager.getGamePlayer(event.getPlayer()) != null && !DwarvenItems.isDroppableItem(event.getItemDrop().getItemStack()) ) ||
				monsterManager.getGamePlayer(event.getPlayer()) != null)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void preventInvDragging(InventoryDragEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Player && game.isGamePlayer((Player) holder)) {
			if (event.getInventorySlots().contains(40))
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		HumanEntity human = event.getPlayer();
		if (human instanceof Player) {
			Dwarf dwarf = DwarfManager.getManager().getGamePlayer((Player) human);
			if (dwarf != null) {
				DwarfManager.getManager().notifyCloseEvent(dwarf);
			}
		}
	}
	
	
	// ------ MOB STUFF ------
	@EventHandler
	public void preventAIBurning(EntityCombustEvent event) {
		if (event.getEntityType() == EntityType.ZOMBIE) {
			event.setCancelled(true);
			return;
		}
		
		Entity entity = event.getEntity();
		if (entity instanceof Player && monsterManager.isGamePlayer((Player) entity)) {
			event.setCancelled(true);
			return;
		}
		
		if (!Game.getGame().isGameEntity(entity)) {
			event.setCancelled(true);
			//noinspection UnnecessaryReturnStatement
			return;
		}
		
	}
	
	@EventHandler
	public void onDismount(EntityDismountEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			MonsterPlayer monster = monsterManager.getGamePlayer((Player) entity);
			if (monster != null && monster.getMob() instanceof Bopen) {
				((Bopen) monster.getMob()).dismountHorse();
			}
		}
	}
	@EventHandler
	public void preventFlightChange(PlayerToggleFlightEvent event){
		Player player = event.getPlayer();
		MonsterPlayer monster = monsterManager.getGamePlayer(player);
		if (monster != null && monster.isFrozen()) {
			event.setCancelled(true);
			monster.resetFrozen();
		}
	}
	
	@EventHandler
	public void preventMobPickup(EntityPickupItemEvent event){
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player && monsterManager.isGamePlayer((Player) entity)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventAITarget(EntityTargetEvent event) {
		Entity targetter = event.getEntity();
		AIEntity ai = AIManager.getManager().getAI(targetter);
		
		if (ai != null && event.getReason() != EntityTargetEvent.TargetReason.CUSTOM) {
			event.setCancelled(true);
			if (ai.getTarget() == null) ai.forceUpdateTarget();
		}
	}
	
	@EventHandler
	public void preventSpectatorTeleport(PlayerTeleportEvent event) {
		MonsterManager monsterManager = MonsterManager.getManager();
		Player player = event.getPlayer();
		
		boolean isMonster = monsterManager.isGamePlayer(player);
		boolean isSpectateTeleport = event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE;
		
		if (isMonster && isSpectateTeleport) {
			event.setCancelled(true);
		}
	}
	
	
	// Other Miscellani
	@EventHandler
	public void preventPaintingsDestroy(HangingBreakByEntityEvent event) {
		Entity remover = event.getRemover();
		// Cancel event if not destroyed by player in creative
		if (!(remover instanceof Player && ((Player) remover).getGameMode() == GameMode.CREATIVE))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void preventTaming(EntityTameEvent event) {
		event.setCancelled(true);
	}
}
