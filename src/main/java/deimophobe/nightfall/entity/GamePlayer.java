package deimophobe.nightfall.entity;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.DamageOccurance;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.util.HitscanProjectile;
import deimophobe.nightfall.util.Util;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Deimophobe on 17/01/17.
 */
public abstract class GamePlayer implements GameEntity<Player> {
	protected Player player;
	protected GamePlayer(Player player) {
		this.player = player;
		player.spigot().respawn();
		
		player.setFoodLevel(20);
		
		// To clear out any fake hearts
		new BukkitRunnable() {
			@Override
			public void run() {
				givePotionEffect(PotionEffectType.ABSORPTION, 5, 1, false, false, true);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 20);
	}
	
	@Override
	public Player getEntity() {
		return player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public UUID getUniqueId() {
		return player.getUniqueId();
	}
	
	public Entity getVisibleEntity() {
		return player;
	}
	
	// ------ DEBUG ------
	public boolean isDebugMode() { return Game.getGame().isDebug(player); }
	
	public void debugDamage(GameDamage damage) {
		if (isDebugMode()) {
			sendMessage(damage.toString());
		}
	}
	
	public void sendDebugMsg(String message) {
		if (isDebugMode()) {
			sendMessage(ChatColor.GREEN + message);
		}
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
	
	public void stopSounds() {
		player.stopSound("");
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
	
	public void giveItem(ItemStack item, int quantity, boolean dropRemaining) {
		if (item == null) return;
		ItemStack copy = item.clone();
		copy.setAmount(quantity);
		HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(copy);
		if (dropRemaining) {
			ItemStack drop = remaining.get(0);
			if (drop != null)
				player.getWorld().dropItemNaturally(player.getLocation(), drop);
		}
	}
	public void giveItem(ItemStack item) {giveItem(item,1, false);}
	
	public void giveItem(CustomItem item) {
		giveItem(item.createItemStack());
	}
	
	public void giveItem(CustomItem item, int quantity) {
		giveItem(item.createItemStack(), quantity, false);
	}
	
	public void clearInventory() {
		player.getInventory().clear();
		player.setItemOnCursor(null);
	}
	
	public void showInventory(Inventory inventory) {
		player.openInventory(inventory);
	}
	
	public int replaceItem(ItemStack oldItem, ItemStack newItem) {
		return replaceItem(oldItem::isSimilar, newItem);
	}
	
	public int replaceItem(CustomItem oldItem, ItemStack newItem) {
		return replaceItem(oldItem::isSimilar, newItem);
	}
	
	public int replaceItem(Predicate<ItemStack> matcher, ItemStack newItem) {
		ListIterator<ItemStack> iterator = player.getInventory().iterator();
		int replaced = 0;
		while (iterator.hasNext()) {
			ItemStack item = iterator.next();
			if (matcher.test(item)) {
				iterator.set(newItem);
				replaced++;
			}
		}
		return replaced;
	}
	
	public boolean hasItem(Material material) {
		return player.getInventory().contains(material);
	}
	
	public boolean hasItem(Material material, int amt) {
		return player.getInventory().contains(material, amt);
	}
	
	public boolean useItem(Material material) {
		if (material == null) throw new NullPointerException("Cannot force use null item.");
		
		for (ItemStack invItem : player.getInventory()) {
			if (invItem != null && invItem.getType() == material) {
				invItem.setAmount(invItem.getAmount() - 1);
				return true;
			}
		}
		return false;
	}
	
	public boolean useItem(Material material, int amt) {
		for (int i=0; i<amt; i++) {
			boolean used = useItem(material);
			if (!used) return false;
		}
		return true;
	}
	
	
	// ------ MESSAGING ------
	public void sendMessage(String message) {
		player.sendMessage(message);
	}
	public void sendTitleMessage(String message) {
		player.sendTitle("", message, 5, 30, 5);
	}
	
	// ------ ONLINE/OFFLINE ------
	private boolean online = true;
	
	public boolean isOnline() { return online; }
	
	public void goOnline(Player newPlayer) {
		online = true;
		this.player = newPlayer;
		resetTitle();
	}
	public void goOffline() {
		online = false;
	}
	
	
	
	// ------ DAMAGE ------
	private DamageOccurance lastDamage;
	public DamageOccurance getLastDamage() { return lastDamage; }
	
	public boolean notifyDamage(DamageOccurance occur) {
		if (occur.shoulReplace(lastDamage)) {
			lastDamage = occur;
			return true;
		} else {
			return false;
		}
	}
	
	public String getDeathMessage() {
		if (lastDamage == null)
			return getDisplayName() + ChatColor.RESET + " has died.";
		else
			return lastDamage.getDeathMessage();
	}
	
	
	// ------ MISC ------
	public void resetPlayer() {
		clearEffects();
		clearInventory();
		player.setDisplayName(player.getName());
		DisguiseAPI.undisguiseToAll(player);
	}
	
	public boolean isBlocking() {
		return player.isBlocking();
	}
	public boolean isSneaking() { return player.isSneaking(); }
	public Block getTargetBlock(Set<Material> materials, int i) {
		return player.getTargetBlock(materials, i);
	}
	
	public <P extends GameEntity> P getLookingAt(double range, double offset, Collection<P> targets) {
		return getLookingAt(range, offset, targets, (p) -> true);
	}
	
	public <P extends GameEntity> P getLookingAt(double range, double offset, Collection<P> targets, Predicate<P> requirement) {
		Location playerLoc = player.getLocation();
		Vector lookDir = playerLoc.getDirection();
		
		P closestPlayer = null;
		double closestRange = range;
		double closestOffset = offset;
		for (P testPlayer : targets) {
			if (testPlayer == this) continue;
			if (!requirement.test(testPlayer)) continue;
			
			Location testLoc = testPlayer.getLocation();
			Vector offsetDir = testLoc.subtract(playerLoc).toVector();
			double distance = offsetDir.length();
			
			if (distance > range) continue;
			
			double eyeOffset = distance * Math.acos(offsetDir.dot(lookDir) / distance);
			
			if (eyeOffset > offset) continue;
			
			if (distance <= closestRange - 1 || (distance <= closestRange + 1 && eyeOffset <= closestOffset)) {
				closestPlayer = testPlayer;
				closestRange = distance;
				closestOffset = eyeOffset;
			}
		}
		return closestPlayer;
	}
	
	@Deprecated
	public void forceKill() {
		GameDamage damage = createDamage(null, CustomDamageType.TEMPORARY, 10000);
		damage.instaKill();
		damage.fire();
	}
	
	public void onRemove() {}
	
	// Abstract methods
	public abstract void updateHotbarSlot(ItemStack heldItem, int slot);
	public abstract void onBlockBreak(Block block, boolean didBreak); // TODO: boolean for if broken
	public abstract void onUse(Action action, Block clickedBlock, BlockFace blockFace); // TODO: tidyup
	public abstract void onShift(boolean sneaking);
	public abstract Projectile onBowFire(Arrow arrow, float force); // TODO: bowfire event
	public abstract void onProjectileLand(Projectile arrow, Block hitBlock);
	
	@Deprecated
	public abstract void update(boolean b, boolean b1, boolean b2, boolean b3, boolean b4);
	
	
	
	// ------ PLAYER BEAMING ------
	public boolean canConnectToPlayer(GamePlayer player, double particlePeriod, Consumer<Location> particlePlacer) {
		return canConnectToLocation(player.getEyeLocation(), particlePeriod, particlePlacer);
	}
	
	public boolean canConnectToLocation(Location location, double particlePeriod, Consumer<Location> particlePlacer) {
		
		Location currentLocation = getEyeLocation();
		
		Vector direction = location.clone().subtract(currentLocation).toVector();
		double distance = direction.length();
		Vector delta = direction.multiply(particlePeriod / distance);
		Set<Location> particleLocations = new HashSet<>();
		
		int times = (int) (distance / particlePeriod);
		for (int i = 0; i <= times; i++) {
			Location newLoc = currentLocation.add(delta);
			particleLocations.add(newLoc.clone());
			if (newLoc.getBlock().getType().isSolid()) return false;
		}
		
		for (Location particleLocation : particleLocations) {
			particlePlacer.accept(particleLocation);
		}
		
		return true;
	}
	
	
	// ------ BEAM FIRING ------
	public void fireHitscan(
			double range, double thickness,
			double particlePeriod, Consumer<Location> particlePlacer,
			Consumer<Dwarf> dwarfConsumer, Consumer<MonsterEntity> mobConsumber
	) {
		fireHitscan(range, thickness, 0.3, particlePeriod, particlePlacer, dwarfConsumer, mobConsumber);
	}
	
	public void fireHitscan(
			double range, double thickness, double offset,
			double particlePeriod, Consumer<Location> particlePlacer,
			Consumer<Dwarf> dwarfConsumer, Consumer<MonsterEntity> mobConsumber
	) {
		// Offset the start of the beam so it doesnt come from the middle of the screen
		Location location = getEyeLocation();
		Misc.moveLocation(location, 0, offset, -offset);
		Vector direction = location.getDirection();
		
		// Offset the looking direction, so that the beam ends at the crosshairs
		double yaw = location.getYaw() * Math.PI/180;
		double sin = Math.sin(yaw);
		double cos = Math.cos(yaw);
		direction.add(new Vector(0.3*cos , 0.3, 0.3*sin).multiply(1/range));
		
		Util.fireHitscan(location, direction, range, thickness, particlePeriod, particlePlacer, dwarfConsumer, mobConsumber);
	}
	
	public void fireParticle(
			double velocity,
			double range,
			double radius,
			double particlePeriod,
			Consumer<Location> particlePlacer,
			Consumer<Dwarf> dwarfConsumer,
			Consumer<MonsterEntity> mobConsumber
	) {
		// Offset the start of the beam so it doesnt come from the middle of the screen
		Location location = getEyeLocation();
		Misc.moveLocation(location, 0, 0.3, -0.3);
		Vector direction = location.getDirection();
		
		// Offset the looking direction, so that the beam ends at the crosshairs
		double yaw = location.getYaw() * Math.PI/180;
		double sin = Math.sin(yaw);
		double cos = Math.cos(yaw);
		direction.add(new Vector(0.3*cos , 0.3, 0.3*sin).multiply(1/range));
		
		direction.normalize().multiply(velocity);
		
		new HitscanProjectile(location, direction, radius, range, particlePeriod, particlePlacer, dwarfConsumer, mobConsumber);
	}
	
	
	// ------ HITSCAN CLASSES ------
	private abstract class SingleEntityConsumer<P extends GameEntity> implements Consumer<P> {
		private final Set<P> hitPlayers = new HashSet<>();
		private final double minDistance;
		
		protected SingleEntityConsumer(double minDistance) {
			this.minDistance = minDistance;
		}
		
		@Override
		public void accept(P entity) {
			if (entity == GamePlayer.this) return;
			if (hitPlayers.contains(entity)) return;
			
			if (entity.distanceTo(GamePlayer.this) >= minDistance) {
				onHit(entity);
				hitPlayers.add(entity);
			}
		}
		
		abstract void onHit(P entity);
	}
	
	public class ProcGiver extends SingleEntityConsumer<Dwarf> {
		private final ProcType type;
		
		public ProcGiver(ProcType type, double minDistance) {
			super(minDistance);
			this.type = type;
		}
		
		@Override
		public void onHit(Dwarf dwarf) {
			dwarf.giveProc(type);
			
			Sounds.DWARF_ITEM_EBOW_GIVE_PROC.playSound(GamePlayer.this);
		}
	}
	
	public class GameEntityDamager<P extends GameEntity> extends SingleEntityConsumer<P> {
		private final CustomDamageType type;
		private final Function<P,Double> damage;
		
		public GameEntityDamager(CustomDamageType type, double damage, double minDistance) {
			super(minDistance);
			this.type = type;
			this.damage = (m) -> damage;
		}
		
		public GameEntityDamager(CustomDamageType type, double damage) {
			super(0);
			this.type = type;
			this.damage = (m) -> damage;
		}
		
		public GameEntityDamager(CustomDamageType type, Function<P, Double> damage) {
			super(0);
			this.type = type;
			this.damage = damage;
		}
		
		@Override
		public void onHit(P entity) {
			entity.doDamage(GamePlayer.this, type, damage.apply(entity), true);
			if (entity instanceof GamePlayer) playSound("entity.arrow.hit_player", 0.8f, 0.5f, false);
		}
	}
}
