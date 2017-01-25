package deimophobe.dvz;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 17/01/17.
 */
public abstract class GamePlayer implements PlayerOrAI {
	protected Player player;
	public void setPlayer(Player player) {
		this.player = player;
	}
	public Player getPlayer() {
		return player;
	}
	
	private String title;
	public void setTitle(String name) {
		title = name;
		player.setDisplayName(name);
	}
	public String getTitle() {
		return title;
	}
	
	@Override
	public Player getEntity() { return player; }
	
	protected GamePlayer(Player player) {
		this.player = player;
	}
	
	public Location getLocation() {
		return player.getLocation();
	}
	
	public void healPlayer(double amt) {
		double newHealth = amt + player.getHealth();
		double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		if (newHealth < maxHealth) {
			player.setHealth(newHealth);
		} else {
			player.setHealth(maxHealth);
		}
		player.damage(0);
	}
	
	public void healPlayerMax() {
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.damage(0);
	}
	
	public void clearInventory() {
		player.getInventory().clear();
	}
	
	
	
	public final void playSound(String sound, float vol, float pitch, boolean toAll) {
		if (sound == null) return;
		
		World world = player.getWorld();
		Location loc = player.getLocation();
		
		if (toAll) {
			world.playSound(loc, sound, vol, pitch);
		} else {
			player.playSound(loc, sound, vol, pitch);
		}
	}
	
	public final void playSound(String sound) {
		playSound(sound, 20, 1, false);
	}
	
	public void givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean force) {
		player.addPotionEffect(new PotionEffect(type, duration, amplifier-1, ambient), force);
	}
	
	public void clearEffects() {
		for (PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
	}
	
	public void damage(double damage, GamePlayer cause) {
		player.damage(damage, cause.player);
	}
	
	public void teleportTo(Location loc) {
		player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
	}
	
	public String getName() {
		return player.getName();
	}
	
	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}
	
	public ItemStack getHeldItem() {
		return player.getInventory().getItemInMainHand();
	}
	
	public void useHeldItem() {
		ItemStack held = getHeldItem();
		if (held != null)
			held.setAmount(held.getAmount() - 1);
	}
	
	public void giveItem(ItemStack item, int quantity) {
		ItemStack copy = item.clone();
		copy.setAmount(quantity);
		player.getInventory().addItem(copy);
	}
	public void giveItem(ItemStack item) {giveItem(item,1);}
	
	public abstract void onUse(Action action, Block clickedBlock, BlockFace blockFace);
	public abstract void onShift(boolean sneaking);
	public abstract Projectile onBowFire(Arrow arrow, float force);
	public abstract void onArrowLand(Arrow arrow, Block hitBlock);
	
	
	public void remove() {
		clearEffects();
		clearInventory();
		player.setDisplayName(player.getName());
	}
	
	public String generateDeathMsg() {
		String name = player.getDisplayName();
		EntityDamageEvent event = player.getLastDamageCause();
		if (event == null) return name + " died.";
		EntityDamageEvent.DamageCause cause = event.getCause();
		
		if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
			GameListener.DamageTriplet triplet = new GameListener.DamageTriplet(edbee);
			
			PlayerOrAI killer = Game.getGame().getPlayerOrAI(triplet.damager);
			if (killer == null)
				return name + " died.";
			String killerName = killer.getDisplayName();
			
			String killMsg;
			if (triplet.type == DamageType.BOW) {
				killMsg = "was shot by";
			} else  {
				killMsg = "was slain by";
			}
			
			String itemName = "";
			if (killer instanceof GamePlayer) {
				ItemStack item = ((GamePlayer) killer).getHeldItem();
				if (item != null && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
					itemName = " using " + item.getItemMeta().getDisplayName();
				}
			}
			return name + " " + killMsg + " " + killerName + itemName;
		}
		
		if (cause == EntityDamageEvent.DamageCause.VOID) {
			return name + " was thrown into the abyss.";
		}
		
		if (cause == EntityDamageEvent.DamageCause.WITHER) {
			return name + " withered away.";
		}
		
		if (cause == EntityDamageEvent.DamageCause.DROWNING) {
			return name + " drowned.";
		}
		
		if (cause == EntityDamageEvent.DamageCause.CONTACT) {
			return name + " was pricked to death.";
		}
		
		
		
		return name + " died.";
	}
}
