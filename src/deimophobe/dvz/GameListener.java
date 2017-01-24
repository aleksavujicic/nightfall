package deimophobe.dvz;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MobManager;
import deimophobe.dvz.monster.PlayerMonster;
import deimophobe.dvz.monster.AIEntity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class GameListener implements Listener {
	
	private final Game game;
	private final DwarfManager dm;
	private final MobManager mm;
	
	public GameListener() {
		game = Game.getGame();
		dm = DwarfManager.getManager();
		mm = MobManager.getManager();
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		game.giveBossBarToPlayer(event.getPlayer());
	}
	
	// --------------------------------------------------------
	//                        EVENTS
	// --------------------------------------------------------
	
	
	@EventHandler
	public void useItems(PlayerInteractEvent event) {
		GamePlayer gp = game.getPlayer(event.getPlayer());
		if (gp != null)
			gp.onUse(event.getAction(), event.getClickedBlock(), event.getBlockFace());
	}
	
	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		GamePlayer gp = game.getPlayer(event.getPlayer());
		if (gp != null)
			gp.onShift(event.isSneaking());
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		DamageTriplet triplet = new DamageTriplet(event);
		DamageType type = triplet.type;
		PlayerOrAI damager = game.getPlayerOrAI(triplet.damager);
		PlayerOrAI damagee = game.getPlayerOrAI(triplet.damagee);
		
		if (damager instanceof PlayerMonster && damagee instanceof AIEntity) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getEntityType() == EntityType.PLAYER)
			event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
		
		double damage = event.getDamage();
		
		// Notify both parties about the damage
		if (damager != null) {
			damage = damager.onHit(damagee, type, damage);
		}
		if (damagee != null) {
			damage = damagee.onGotHit(damager, type, damage);
		}
		
		// Apply force calculations to arrow
		if (type == DamageType.BOW) {
			Entity arrow = event.getDamager();
			if (arrow.hasMetadata("force")) {
				damage *= arrow.getMetadata("force").get(0).asDouble();
			} else {
				Bukkit.getLogger().warning("Arrow has no attached force?");
			}
		}
		
		// apply damage and cancel if -1
		event.setDamage(damage);
		if (damage == -1) {
			event.setCancelled(true);
		}
		
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
			
			PlayerMonster mob = mm.getMob((Player) event.getEntity());
			if (mob != null) {
				// Ignore certain damages (see above)
				for (EntityDamageEvent.DamageCause immunity : IMMUNE_CAUSES) {
					if (event.getCause() == immunity) {
						event.setCancelled(true);
						event.setDamage(0);
						return;
					}
				}
				// x4 dagger poison damage
				if (event.getCause() == EntityDamageEvent.DamageCause.WITHER) {
					event.setDamage(event.getDamage()*4);
				}
				
				// Prevent killing and set to spectator
				double dmg = event.getFinalDamage();
				if (mob.getPlayer().getHealth() - dmg <= 0.1) {
					mob.kill();
					event.setDamage(0);
					
					if (event instanceof EntityDamageByEntityEvent) {
						
						DamageTriplet triplet = new DamageTriplet((EntityDamageByEntityEvent) event);
						DamageType type = triplet.type;
						Dwarf dwarf = dm.getDwarf(triplet.damager);
						
						if (dwarf != null)
							dwarf.onKill(mob, type);
					}
				}
			}
			
			Dwarf dwarf = dm.getDwarf((Player) event.getEntity());
			if (dwarf != null) {
				if (event.getCause() == EntityDamageEvent.DamageCause.POISON) {
					event.setDamage(event.getDamage()*2);
				}
			}
		}
	}
	
	/**
	 * Handles AI getting killed and notifies dwarf if there is one.
	 */
	@EventHandler
	public void onAIKill(EntityDeathEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			LivingEntity dead = event.getEntity();
			Dwarf dwarf = dm.getDwarf(dead.getKiller());
			AIEntity ai = (AIEntity) game.getPlayerOrAI(dead);
			
			DamageType type;
			switch (dead.getLastDamageCause().getCause()) {
				case ENTITY_ATTACK:
					type = DamageType.MELEE;
					break;
				case PROJECTILE:
					type = DamageType.BOW;
					break;
				default:
					type = null;
					break;
			}
			
			if (dwarf != null && ai != null && type != null)
				dwarf.onKill(ai, type);
		}
	}
	
	@EventHandler
	public void onArrowFire(EntityShootBowEvent event) {
		if (event.getEntity().getType() == EntityType.PLAYER) {
			GamePlayer gp = game.getPlayer((Player) event.getEntity());
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
					if (newProj != null)
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
				GamePlayer player = game.getPlayer((Player) source);
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
		Dwarf dwarf = dm.getDwarf(event.getEntity());
		if (dwarf != null) {
			event.setDeathMessage(dwarf.generateDeathMsg());
			dm.removeDwarf(dwarf);
			mm.addMob(dwarf.getName());
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
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
	public void onArrowPickup(PlayerPickupItemEvent event){
		if (event.getItem() == new ItemStack(Material.ARROW)) {
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
		if (holder instanceof Player && DwarfManager.getManager().isDwarf((Player) holder)) {
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
			Dwarf dwarf = dm.getDwarf(event.getPlayer());
			if (dwarf != null)
				dwarf.mineGravel();
		}
	}
	
	
	/**
	 * A kind of hacky way to get the the actual damager,
	 * but to take into account that it might be from an
	 * arrow - not just a melee hit.
	 */
	protected static class DamageTriplet {
		protected final DamageType type;
		protected final Entity damager;
		protected final Entity damagee;
		
		protected DamageTriplet(EntityDamageByEntityEvent event) {
			damagee = event.getEntity();
			
			Entity tempDamager = null;
			DamageType tempType = null;
			
			if (event.getDamager().getType() == EntityType.ARROW) {
				Arrow arrow = (Arrow) event.getDamager();
				if (arrow.getShooter() instanceof Entity) {
					tempDamager = (Entity) arrow.getShooter();
					tempType = DamageType.BOW;
				}
			} else {
				tempDamager = event.getDamager();
				tempType = DamageType.MELEE;
			}
			
			type = tempType;
			damager = tempDamager;
		}
	}
}
