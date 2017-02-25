package deimophobe.dvz;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * Created by Deimophobe on 17/01/17.
 */
public abstract class GamePlayer extends GameEntity {
	protected Player player;
	private final String name;
	
	protected GamePlayer(Player player) {
		super(player);
		this.player = player;
		this.name = player.getName();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	
	// ------ TITLE ------
	private ChatColor colour;
	private String title;
	private boolean forcedTitle;
	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}
	
	public void setTitle(ChatColor colour, String title, boolean force) {
		if (force) {
			player.setDisplayName(colour + title + ChatColor.RESET);
		} else {
			if (title != null)
				player.setDisplayName(colour + title + " " + player.getName() + ChatColor.RESET);
			else
				player.setDisplayName(colour + player.getName() + ChatColor.RESET);
		}
		this.colour = colour;
		this.title = title;
		this.forcedTitle = force;
	}
	
	public String getWhoDisplay() {
		if (forcedTitle)
			return getDisplayName() + ChatColor.RESET + "(" + player.getName() + ")";
		else
			return getDisplayName() + ChatColor.RESET;
	}
	
	// Used for relogging
	private void resetTitle() {
		setTitle(colour, title, forcedTitle);
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
	
	public boolean isHolding(ItemStack item) {
		ItemStack held = getHeldItem();
		return (held != null && held.isSimilar(item));
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
	
	public  void showInventory(Inventory inventory) {
		player.openInventory(inventory);
	}
	
	
	
	// ------ MESSAGING ------
	public void sendMessage(String message) {
		player.sendMessage(message);
	}
	
	
	// ------ ONLINE/OFFLINE ------
	public void goOnline(Player newPlayer) {
		super.resetEntity(newPlayer);
		player = newPlayer;
		resetTitle();
	}
	public void goOffline() {}
	
	
	// ------ FREEZE/UNFREEZE ------
	private Location freezeLocation;
	public void freeze(int time) {
		
		givePotionEffect(PotionEffectType.LEVITATION, time, 0, true, true, true);
		givePotionEffect(PotionEffectType.GLOWING, time, 1, true, true, true);
		
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed(0);
		
		freezeLocation = getLocation();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				player.setAllowFlight(false);
				player.setFlying(false);
				player.setFlySpeed(0.1f);
				
				teleportTo(freezeLocation);
				freezeLocation = null;
			}
		}.runTaskLater(Game.getGame().getPlugin(), time);
	}
	
	public void resetFrozen() {
		if (isFrozen())
			teleportTo(freezeLocation);
	}
	
	public boolean isFrozen() {
		return (freezeLocation != null);
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
		if (type == null) return name + " died.";
		
		String killMsg;
		
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
			
			
			case CONTACT:
				return name + " was pricked to death.";
			case DROWNING:
				return name + " drowned.";
			case FALL:
				return name + " fell to their doom.";
			case HOT_FLOOR:
				return name + " burnt their feet.";
			case CRAMMING:
				return name + " was crushed.";
			case FALLING_BLOCK:
				return name + " was squished.";
			case LIGHTNING:
				return name + " angered the gods.";
			case LAVA:
				return name + " tried to swim in lava.";
			case FIRE:
				return name + " couldn't find water.";
				
			case NOT_HOLDING_GHOSTBLADE:
				return name + " was a bit of a klutz and dropped their blade.";
			
			case VOID:
				return name + " was swallowed by the abyss.";
			case SEPPUKU:
				return name + " committed sudoku.";
			case SHRINE_PROTECTION:
				return name + " was zapped by lightning.";
			case RELOG:
				return name + " failed at interdimensional travel.";
				
			default:
				return name + " died.";
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
		for (Dwarf testDwarf : DwarfManager.getManager().getGamePlayers()) {
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
	public abstract void onProjectileLand(Projectile arrow, Block hitBlock);
}
