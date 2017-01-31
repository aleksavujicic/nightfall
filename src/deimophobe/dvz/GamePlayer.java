package deimophobe.dvz;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
	
	public String generateDeathMsg() {
		
		String name = getDisplayName();
		
		DamageType type = getLastDamageType();
		EntityDamageEvent.DamageCause cause = type.getCause();
		
		String killMsg = null;
		
		switch (type) {
			case HAMMER_AOE:
			case REGULAR_MELEE:
				killMsg = "slain";
				break;
			case REGULAR_RANGED:
				killMsg = "shot";
				break;
			case EBOW:
				killMsg = "pierced";
				break;
			case EVISCERATE:
				killMsg = "eviscerated";
				break;
				
			case POISON:
				return name + " withered away.";
				
			case NATURAL:
			case INSTA_KILL:
				switch (type.getCause()) {
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
						return name + " died. (unknown? : "+cause+")";
				}
		}
		
		String damagerName = getLastDamager().getDisplayName();
		String itemName = getLastItemName();
		if (itemName != null)
			return name + " was " + killMsg + " by " + damagerName + " using " + itemName + ".";
		else
			return name + " was " + killMsg + " by " + damagerName + ".";
		
	}
	
	public boolean isBlocking() {
		return player.isBlocking();
	}
	public Block getTargetBlock(Set<Material> materials, int i) {
		return player.getTargetBlock(materials, i);
	}
	
	
	public Dwarf getLookingAt(double epsilon, double range) {
		Location playerLoc = player.getLocation();
		Vector lookDir = playerLoc.getDirection();
		
		Dwarf closestDwarf = null;
		double closestRange = range;
		double closestOffset = epsilon;
		for (Dwarf testDwarf : DwarfManager.getManager().getDwarves()) {
			if (testDwarf == this) continue;
			//if (testDwarf.isMaxArmour()) continue;
			
			Location testLoc = testDwarf.getLocation();
			Vector offsetDir = testLoc.subtract(playerLoc).toVector();
			double distance = offsetDir.length();
			
			if (distance > range) continue;
			
			double eyeOffset = distance * Math.acos(offsetDir.dot(lookDir) / distance);
			
			if (eyeOffset > epsilon) continue;
			
			if (distance <= closestRange - 1 || (distance <= closestRange + 1 && eyeOffset <= closestOffset)) {
				closestDwarf = testDwarf;
				closestRange = distance;
				closestOffset = eyeOffset;
			}
		}
		return closestDwarf;
	}
	
	// Abstract methods
	public abstract void onUse(Action action, Block clickedBlock, BlockFace blockFace);
	public abstract void onShift(boolean sneaking);
	public abstract Projectile onBowFire(Arrow arrow, float force);
	public abstract void onArrowLand(Arrow arrow, Block hitBlock);
	
}
