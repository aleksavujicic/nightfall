package deimophobe.dvz;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Created by Deimophobe on 17/01/17.
 */
public abstract class GamePlayer extends GameEntity {
	protected final Player player;
	
	protected GamePlayer(Player player) {
		super(player);
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	
	// ------ TITLE ------
	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}
	
	public void setTitle(ChatColor color, String title, boolean force) {
		if (force) {
			player.setDisplayName(color + title + ChatColor.RESET);
		} else {
			if (title != null)
				player.setDisplayName(color + title + " " + player.getName() + ChatColor.RESET);
			else
				player.setDisplayName(color + player.getName() + ChatColor.RESET);
		}
	}
	
	
	// ------ SOUND ------
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
	
	
	// ------ INVENTORY ------
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
	
	public void clearInventory() {
		player.getInventory().clear();
	}
	
	
	
	
	
	
	// ------ MISC ------
	public void remove() {
		clearEffects();
		clearInventory();
		player.setDisplayName(player.getName());
	}
	
	// TODO
	public String generateDeathMsg() {
		
		String name = player.getDisplayName();
		EntityDamageEvent event = player.getLastDamageCause();
		if (event == null) return name + " died. (event null)";
		EntityDamageEvent.DamageCause cause = event.getCause();
		
		switch (cause) {
			case ENTITY_ATTACK:
			case ENTITY_SWEEP_ATTACK:
				return name + " was slain by " + getLastDamager().getDisplayName() + ".";
			case PROJECTILE:
				return name + " was shot by " + getLastDamager().getDisplayName() + ".";
				
			case CUSTOM:
				return name + " died to custom damage " + getLastDamageType() +  " from " + getLastDamager().getDisplayName() + ".";
				
			case POISON:
			case WITHER:
				return name + " withered away.";
				
			case CONTACT:
				return name + " was pricked to death.";
			case DROWNING:
				return name + " drowned.";
			case FALL:
				return name + " fell to their doom.";
			case VOID:
				return name + " was swallowed by the abyss.";
			case HOT_FLOOR:
				return name + " burnt their feet.";
			case CRAMMING:
				return name + " was crushed.";
			case FALLING_BLOCK:
				return name + " was squished.";
			case SUICIDE:
				return name + " committed sudoku.";
			case LIGHTNING:
				return name + " angered the gods.";
			case LAVA:
				return name + " tried to swim in lava.";
			case FIRE:
			case FIRE_TICK:
				return name + " couldn't find water.";
			default:
				return name + " died. (unknown: "+cause+")";
		}
		/*
		if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
			
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
			GameListener.DamageTriplet triplet = new GameListener.DamageTriplet(edbee);
			
			GameEntity killer = Game.getGame().getGameEntity(triplet.damager);
			if (killer == null)
				return name + " died. (killer null)";
			String killerName = "bob";//killer.getDisplayName();
			
			String killMsg;
			//if (triplet.type) {
				killMsg = "was shot by";
			//} else  {
			//	killMsg = "was slain by";
			//}
			
			String itemName = "";
			if (killer instanceof GamePlayer) {
				ItemStack item = ((GamePlayer) killer).getHeldItem();
				if (item != null && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
					itemName = " using " + item.getItemMeta().getDisplayName();
				}
			}
			return name + " " + killMsg + " " + killerName + itemName;
		}
		
		
		*/
		
		
	}
	
	public boolean isBlocking() {
		return player.isBlocking();
	}
	public Block getTargetBlock(Set<Material> materials, int i) {
		return player.getTargetBlock(materials, i);
	}
	
	
	
	// Abstract methods
	public abstract void onUse(Action action, Block clickedBlock, BlockFace blockFace);
	public abstract void onShift(boolean sneaking);
	public abstract Projectile onBowFire(Arrow arrow, float force);
	public abstract void onArrowLand(Arrow arrow, Block hitBlock);
	
}
