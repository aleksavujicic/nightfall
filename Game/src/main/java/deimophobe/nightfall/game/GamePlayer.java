package deimophobe.nightfall.game;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.ItemMatcher;
import deimophobe.nightfall.cooldown.CooldownHolder;
import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.death.DeathMessageMaker;
import deimophobe.nightfall.damage.death.LastMainDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.util.NMSUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
public abstract class GamePlayer extends AbstractGameEntity<Player> {
	protected Player player;
	protected GamePlayer(Player player) {
		super(player);
		
		this.player = player;
		//player.spigot().respawn();
		
		player.setFoodLevel(20);
		player.closeInventory();
		
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(10000);
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100000);
		
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
	
	public void debugObject(Object object) {
		if (isDebugMode()) {
			sendMessage(object.toString());
		}
	}
	
	public void sendDebugMsg(String message) {
		if (isDebugMode()) {
			sendMessage(ChatColor.GREEN + message);
		}
	}
	
	// ------ ONLINE/OFFLINE ------
	private boolean online = true;
	
	public boolean isOnline() { return online; }
	
	public void goOnline(Player newPlayer) {
		online = true;
		this.player = newPlayer;
		this.entity = newPlayer;
		resetTitle();
		loadHealth();
	}
	public void goOffline() {
		online = false;
		saveHealth();
		this.player = null;
		this.entity = null;
	}
	
	
	// ------ HEALTH SAVING ------
	private double health;
	private void saveHealth() {
		health = player.getHealth();
	}
	private void loadHealth() {
		new BukkitRunnable() {
			@Override public void run() {
				health = Math.max(health, 0);
				health = Math.min(health, getMaxHealth());
				player.setHealth(health);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 10);
	}
	
	
	// ------ TITLE ------
	private ChatColor colour;
	private String title;
	private boolean forcedTitle;
	
	private String forcedDisplayName = null;
	
	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}
	
	public void setTitle(ChatColor colour, String title, boolean force) {
		if (forcedDisplayName != null) return;
		
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
	
	public void forceDisplayName(String name) {
		forcedDisplayName = name;
		forcedTitle = true;
		resetTitle();
	}
	
	// Used for relogging
	private void resetTitle() {
		if (forcedDisplayName != null) {
			player.setDisplayName(forcedDisplayName + ChatColor.RESET);
		} else {
			setTitle(colour, title, forcedTitle);
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
	
	public void stopSounds() {
		player.stopSound("");
	}
	
	
	
	// ------ INVENTORY ------
	public ItemStack getHeldItem() {
		return player.getInventory().getItemInMainHand();
	}
	
	public int getHeldItemCount() {
		ItemStack item = getHeldItem();
		if (item == null) return 0;
		return item.getAmount();
	}
	
	public boolean isHolding(ItemStack item) {
		ItemStack held = getHeldItem();
		return (held != null && held.isSimilar(item));
	}
	
	public void useHeldItem() {
		ItemStack held = getHeldItem();
		if (held != null) {
			held.setAmount(held.getAmount() - 1);
		}
	}
	
	public void useHeldItemStack() {
		ItemStack held = getHeldItem();
		if (held != null) {
			held.setAmount(0);
		}
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
		clearCraftingInvetory();
	}
	
	public void clearCraftingInvetory() {
		InventoryView view = player.getOpenInventory();
		if (view.getType() == InventoryType.CRAFTING) {
			view.getTopInventory().clear();
		}
	}
	
	@Deprecated
	public int replaceItem(ItemStack oldItem, ItemStack newItem) {
		return replaceItem(oldItem::isSimilar, newItem);
	}
	
	@Deprecated
	public int replaceItem(CustomItem oldItem, ItemStack newItem) {
		return replaceItem(oldItem::isSimilar, newItem);
	}
	
	public int replaceItem(Predicate<ItemStack> matcher, ItemStack newItem) {
		PlayerInventory inv = player.getInventory();
		ListIterator<ItemStack> iterator = inv.iterator();
		
		int replaced = 0;
		while (iterator.hasNext()) {
			ItemStack item = iterator.next();
			if (matcher.test(item)) {
				iterator.set(newItem);
				replaced++;
			}
		}
		
		// Replace cursor item too
		if (matcher.test(player.getItemOnCursor())) {
			player.setItemOnCursor(newItem);
			replaced++;
		}
		
		return replaced;
	}
	
	@Deprecated
	public boolean hasItem(Material material) {
		return player.getInventory().contains(material);
	}
	
	@Deprecated
	public boolean hasItem(Material material, int amt) {
		return player.getInventory().contains(material, amt);
	}
	
	public boolean hasItem(CustomItem customItem) {
		PlayerInventory inv = player.getInventory();
		ListIterator<ItemStack> iterator = inv.iterator();
		
		while (iterator.hasNext()) {
			ItemStack item = iterator.next();
			if (customItem.isSimilar(item)) return true;
		}
		return false;
	}
	
	@Deprecated
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
	
	@Deprecated
	public boolean useItemReverse(Material material) {
		if (material == null) throw new NullPointerException("Cannot force use null item.");
		
		PlayerInventory inv = player.getInventory();
		ListIterator<ItemStack> iterator = inv.iterator(inv.getSize());
		
		while (iterator.hasPrevious()) {
			ItemStack invItem = iterator.previous();
			if (invItem != null && invItem.getType() == material) {
				invItem.setAmount(invItem.getAmount() - 1);
				return true;
			}
		}
		return false;
	}
	
	@Deprecated
	public boolean useItem(Material material, int amt) {
		for (int i=0; i<amt; i++) {
			boolean used = useItem(material);
			if (!used) return false;
		}
		return true;
	}
	
	public int getItemCount(ItemMatcher matcher) {
		int count = 0;
		for (ItemStack item : player.getInventory()) {
			if (item == null) continue;
			if (!matcher.doesItemMatch(item)) continue;
			
			count += item.getAmount();
		}
		return count;
	}
	
	public void removeItems(ItemMatcher matcher) {
		for (ItemStack item : player.getInventory()) {
			if (item == null) continue;
			if (!matcher.doesItemMatch(item)) continue;
			
			item.setAmount(0);
		}
	}
	
	
	// ------ MESSAGING ------
	public void sendMessage(String message) {
		player.sendMessage(message);
	}
	public void sendLargeTitleMessage(String title, String message) {
		player.sendTitle(title, message, 5, 30, 5);
	}
	public void sendTitleMessage(String message) {
		player.sendTitle("", message, 5, 30, 5);
	}
	
	
	// ------ DAMAGE ------
	private LastMainDamage lastMainDamage;
	private DeathMessageMaker deathMessageMaker = (playerName, lastMainDamage1) -> new TextComponent(""); // Has default just in case
	
	public void saveDamageInfo(DeathMessageMaker deathMessageMaker, LastMainDamage damage) {
		this.deathMessageMaker = deathMessageMaker;
		if (damage.shouldReplace(lastMainDamage)) {
			lastMainDamage = damage;
		}
	}
	
	public BaseComponent getDeathMessage() {
		return deathMessageMaker.getDeathMessage(Misc.textComponentFromString(getDeathMessageName()), lastMainDamage);
	}
	
	public String getDeathMessageName() {
		return getDisplayName();
	}
	
	
	// ------ MISC ------
	public boolean isBlocking() {
		return player.isBlocking();
	}
	public boolean isSneaking() { return player.isSneaking(); }
	public Block getTargetBlock(Set<Material> materials, int i) {
		return player.getTargetBlock(materials, i);
	}
	public Block getPrevTargetBlock(Set<Material> materials, int i) {
		return player.getLastTwoTargetBlocks(materials, i).get(0);
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
	
	/**
	 * @deprecated Guess is based on a very crude velocity calculation.
	 */
	@Deprecated
	public Location guessClientSideLocation() {
		int ping = NMSUtil.getPingOfPlayer(player);
		int ticksLagging = ping/50;
		return getLocation().add(getVelocity().multiply(ticksLagging));
	}
	
	
	public void onRemove() {}
	
	// Abstract methods
	public abstract void updateHotbarSlot(ItemStack heldItem, int slot);
	public abstract boolean onBlockBreak(Block block, boolean didBreak);
	public abstract void onUse(ClickType click, Block clickedBlock, BlockFace blockFace); // TODO: tidyup
	public abstract void onShift(boolean sneaking);
	public abstract Projectile onBowFire(Arrow arrow, float force); // TODO: bowfire event
	public abstract void onProjectileLand(Projectile arrow, Block hitBlock, Entity hitEntity);
	
	// ----- UPDATES -----
	private final CooldownHolder cooldownHolder = new CooldownHolder();
	
	// Upper limit is 2520 which divides all numbers less than 10 (so is uniform mod n for lots of small n)
	private final int offset = Misc.randomInt(0, 2519);
	
	public void update() {
		cooldownHolder.update();
	}
	
	public void addUpdateable(Updateable updateable) {
		cooldownHolder.addUpdateable(updateable);
	}
	
	public boolean everySec() {
		return everyNthTick(20);
	}
	
	public boolean everyNthTick(int n) {
		return (Game.getGame().getCurrentTick() + offset) % n == 0;
	}
	
	
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
	
	
	// ------ HITSCAN CLASSES ------
	public abstract class SingleEntityConsumer<P extends GameEntity> implements Consumer<P> {
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
		
		public abstract void onHit(P entity);
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
	
	public class GameEntityDamager<E extends GameEntity> extends SingleEntityConsumer<E> {
		private final Consumer<E> damager;
		
		public GameEntityDamager(GameDamageType type, double damage) {
			super(0);
			this.damager = entity -> entity.doDamage(GamePlayer.this, type, damage);
		}
		
		public GameEntityDamager(GameDamageType type, Function<E, Double> damageFunction) {
			super(0);
			this.damager = entity -> entity.doDamage(GamePlayer.this, type, damageFunction.apply(entity));
		}
		
		public GameEntityDamager(Consumer<E> damager) {
			super(0);
			this.damager = damager;
		}
		
		public GameEntityDamager(GameDamageType type, double damage, boolean force, Consumer<GameDamage<?,?>> damageModifier) {
			super(0);
			this.damager = entity -> {
				GameDamage<?,?> gameDamage = entity.createDamage(GamePlayer.this, type, damage);
				damageModifier.accept(gameDamage);
				gameDamage.fire(force);
			};
		}
		
		@Override
		public void onHit(E entity) {
			damager.accept(entity);
		}
	}
}
