package deimophobe.nightfall.game.entity;

import com.comphenix.packetwrapper.WrapperPlayServerWorldBorder;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.WhoEntry;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.ItemMatcher;
import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.common.player.settings.PlayerSettings;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.cooldown.CooldownHolder;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.PreDamagePriority;
import deimophobe.nightfall.damage.death.DeathMessageMaker;
import deimophobe.nightfall.damage.death.LastMainDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.game.AbstractGameEntity;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.upgrades.Upgrade;
import deimophobe.nightfall.util.PacketUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Deimophobe on 17/01/17.
 */
public abstract class GamePlayer extends AbstractGameEntity<Player> implements GameEntityShooter<Player> {
	protected final Game game;
	
	protected Player player;
	protected GamePlayer(Player player) {
		super(player);
		//TODO Dependency injection
		this.game = Game.getGame();
		
		this.player = player;
		//player.spigot().respawn();
		
		player.setFoodLevel(20);
		player.setRemainingAir(300);
		player.closeInventory();
		
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(10000);
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100000);
		
		// To clear out any fake hearts
		NMSUtil.setNumberAbsorptionHearts(player, 0);
		
		initialiseWarnings();
		
		// Initialise shields
		shields = new EnumMap<>(ShieldSource.class);
		for (ShieldSource source : ShieldSource.values()) {
			shields.put(source, 0);
		}
	}
	
	@Override
	public Player getEntity() {
		return player;
	}
	
	public Player getPlayer() {
		checkState(player != null, "GamePlayer must be online.");
		return player;
	}
	
	public UUID getUniqueId() {
		return player.getUniqueId();
	}
	
	public Entity getVisibleEntity() {
		return player;
	}
	
	public PlayerSettings getSettings() {
		return PlayerManager.getManager().getSettings(player);
	}
	
	
	public void onRemove() {
		clearInventory();
		clearWarning();
		cooldownHolder.removeAll();
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
	
	@Override
	public boolean isOnline() { return online; }
	
	public void goOnline(Player newPlayer) {
		online = true;
		this.player = newPlayer;
		this.entity = newPlayer;
		resetTitle();
		loadHealth();
		
		doLater(this::initialiseWarnings, 20);
	}
	public void goOffline() {
		online = false;
		saveHealth();
		this.player = null;
		this.entity = null;
	}
	
	
	// ------ SET EXP ------
	private float previousExpValue = -1;
	protected final void setExp(float value) {
		value = Misc.boundValue(value, 0, 1);
		if (value == previousExpValue) return;
		
		player.setExp(value);
		previousExpValue = value;
	}
	
	
	// ------ HEALTH SAVING ------
	private double health;
	private void saveHealth() {
		health = player.getHealth();
	}
	private void loadHealth() {
		new BukkitRunnable() {
			@Override public void run() {
				if (!player.isOnline()) return;
				
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
	
	public WhoEntry getWhoEntry() {
		String displayName = getDisplayName();
		displayName = ChatColor.stripColor(displayName);
		
		WhoEntry entry = new WhoEntry(getName(), displayName, forcedTitle);
		entry.setDefaultColour(colour.asBungee());
		return entry;
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
	public final void playSound(Sound sound, float vol, float pitch, boolean toAll) {
		World world = player.getWorld();
		Location loc = player.getLocation();
		
		if (toAll) {
			world.playSound(loc, sound, vol, pitch);
		} else {
			player.playSound(loc, sound, vol, pitch);
		}
	}
	
	public final void playSound(Sound sound) {
		playSound(sound, 20, 1, false);
	}
	
	public final void playSound(String sound, float vol, float pitch, boolean toAll) {
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
	private void iterateThroughInventory(Consumer<ItemStack> itemConsumer) {
		iterateThroughInventory(item -> {
			itemConsumer.accept(item);
			return null;
		}, false);
	}
	
	private void iterateThroughInventory(Function<ItemStack, ItemStack> itemChanger, boolean reversed) {
		InventoryIterator iterator = new PlayerInventoryIterator(player, reversed);
		
		while (iterator.hasNext()) {
			ItemStack item = iterator.next();
			ItemStack newItem = itemChanger.apply(item);
			if (newItem != null) iterator.replace(newItem);
		}
	}
	
	// Clearing
	public void clearInventory() {
		player.getInventory().clear();
		player.setItemOnCursor(null);
		
		InventoryView view = player.getOpenInventory();
		if (view.getType() == InventoryType.CRAFTING) {
			view.getTopInventory().clear();
		}
	}
	
	// Held Items
	public ItemStack getHeldItem() {
		return player.getInventory().getItemInMainHand();
	}
	
	public int getHeldItemCount() {
		ItemStack item = getHeldItem();
		if (item == null) return 0;
		return item.getAmount();
	}
	
	public boolean isHolding(ItemStack item) {
		return isHolding(item::isSimilar);
	}
	
	public boolean isHolding(ItemMatcher matcher) {
		ItemStack held = getHeldItem();
		return matcher.doesItemMatch(held);
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
	
	// Inventory manipulation
	public void giveItem(ItemStack item) {giveItem(item,1, false);}
	
	public void giveItem(CustomItem item) {
		giveItem(item.createItemStack());
	}
	
	public void giveItem(CustomItem item, int quantity) {
		giveItem(item.createItemStack(), quantity, false);
	}
	
	public void giveItem(ItemStack item, int quantity, boolean dropRemaining) {
		if (item == null) return;
		ItemStack copy = item.clone();
		copy.setAmount(quantity);
		HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(copy);
		if (dropRemaining) {
			ItemStack drop = remaining.get(0);
			if (drop != null) {
				player.getWorld().dropItemNaturally(player.getLocation(), drop);
			}
		}
	}
	
	public boolean hasItem(ItemMatcher matcher) {
		return hasItem(matcher, 1);
	}
	
	public boolean hasItem(ItemMatcher matcher, int count) {
		return getItemCount(matcher) >= count;
	}
	
	public int getItemCount(ItemMatcher matcher) {
		MutableInt count = new MutableInt(0);
		iterateThroughInventory(item -> {
			if (matcher.doesItemMatch(item)) {
				int amt = item.getAmount();
				count.add(amt);
			}
		});
		
		return count.getValue();
	}
	
	public int replaceItem(ItemStack old, ItemStack newItem) {
		return replaceItem(old::isSimilar, newItem);
	}
	
	public int replaceItem(ItemMatcher matcher, ItemStack newItem) {
		return replaceItem(matcher, item -> newItem);
	}
	
	public int replaceItem(ItemMatcher matcher, Function<ItemStack, ItemStack> itemChanger) {
		MutableInt replaced = new MutableInt(0);
		iterateThroughInventory(item -> {
			if (matcher.doesItemMatch(item)) {
				ItemStack newItem = itemChanger.apply(item);
				replaced.add(1);
				return newItem;
			}
			return item;
		}, false);
		
		return replaced.getValue();
	}
	
	public boolean removeItem(ItemMatcher matcher) {
		return removeItems(matcher, 1);
	}
	
	public boolean removeItems(ItemMatcher matcher, int amountToRemove) {
		return removeItems(matcher, amountToRemove, false);
	}
	
	public boolean removeItems(ItemMatcher matcher, int amountToRemove, boolean reversed) {
		MutableInt toRemove = new MutableInt(amountToRemove);
		iterateThroughInventory(item -> {
			if (toRemove.getValue() == 0) return null;
			
			if (matcher.doesItemMatch(item)) {
				// Get item amt
				int amount = item.getAmount();
				// Remove as much as you can
				int toRemoveFromItem = Math.min(amount, toRemove.getValue());
				// Update amounts appropriately
				item.setAmount(amount - toRemoveFromItem);
				toRemove.subtract(toRemoveFromItem);
				return item;
			}
			return null;
		}, reversed);
		
		return toRemove.getValue() == 0;
	}
	
	public void removeAllItems(ItemMatcher matcher) {
		replaceItem(matcher, item -> {
			item.setAmount(0);
			return item;
		});
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
	public void sendTitleMessage(String title, String message) {
		player.sendTitle(title, message, 5, 30, 5);
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
	
	protected void resetLastMainDamage() {
		lastMainDamage = null;
		deathMessageMaker = DeathMessageMaker.DIED_MESSAGE;
	}
	
	
	// ------ SHIELDS ------
	private final Map<ShieldSource, Integer> shields;
	
	public void addShields(ShieldSource source, int number) {
		checkArgument(number > 0, "Number of shields to add must be positive (got %s).", number);
		int max = source.getMaxHearts();
		shields.merge(source, number, (old, _new) -> Math.min(old + _new, max));
		updateShieldCount();
	}
	
	public void addMaxShields(ShieldSource source) {
		addShields(source, source.getMaxHearts());
	}
	
	public void removeShields(int number) {
		checkArgument(number > 0, "Number of shields to remove must be positive (got %s).", number);
		
		MutableInt toRemoveBox = new MutableInt(number);
		shields.replaceAll((source, old) -> {
			int toRemoveTotal = toRemoveBox.toInteger();
			if (toRemoveTotal == 0) return old;
			
			int toRemoveFromSource = Math.min(toRemoveTotal, old);
			toRemoveBox.subtract(toRemoveFromSource);
			return old - toRemoveFromSource;
		});
		updateShieldCount();
	}
	
	public void removeAllShields() {
		shields.replaceAll((source, integer) -> 0);
		updateShieldCount();
	}
	
	public boolean isShieldSourceMaxed(ShieldSource source) {
		return shields.get(source) == source.getMaxHearts();
	}
	
	private int getNumberOfShields() {
		return shields.values()
				.stream()
				.mapToInt(Number::intValue)
				.sum();
	}
	
	private static final int SHIELD_IMMUNE_DURATION = 60;
	private static final double SHIELD_VALUE = 8;
	protected boolean shieldDamage(GameDamage<?,?> damage) {
		int numShields = getNumberOfShields();
		if (numShields == 0) return false;
		
		
		damage.addPreDamageHandler(PreDamagePriority.SHIELDS, () -> {
			if (damage.isShieldbreaker()) {
				double damageAmt = damage.getFinalDamage();
				
				int shieldsToRemove = (int) (damageAmt/SHIELD_VALUE) + 1;
				if (shieldsToRemove > numShields) {
					damage.getMultiPartDamage().addPostBoost(-SHIELD_VALUE * numShields);
					removeAllShields();
				} else {
					damage.softCancel();
					removeShields(shieldsToRemove);
				}
			} else {
				if (damage.getType() == GameDamageType.FALL && damage.getFinalDamage() < 4) {
					damage.cancel();
				} else {
					damage.softCancel();
					removeShields(1);
				}
			}
			damage.setNoDamageTicks(SHIELD_IMMUNE_DURATION);
		});
		damage.addPostDamageHandler(() -> {
			if (!damage.isShieldbreaker()) {
				removeAllPoisons();
				removeFire();
			}
			
			givePotionEffect(PotionEffectType.GLOWING, SHIELD_IMMUNE_DURATION, 1, true, false, false);
			playSound(Sound.ENTITY_EVOKER_PREPARE_SUMMON, 10f, 2f, false);
		});
		return true;
	}
	
	private void updateShieldCount() {
		NMSUtil.setNumberAbsorptionHearts(player, getNumberOfShields()*2);
	}
	
	public void resetShieldCount() {
		NMSUtil.setNumberAbsorptionHearts(player, getNumberOfShields()*2 + 1);
		updateShieldCount();
	}
	
	public String shieldInfo() {
		StringBuilder sb = new StringBuilder("Shields: [");
		for (Map.Entry<ShieldSource, Integer> entry : shields.entrySet()) {
			sb.append(entry.getKey())
					.append("=")
					.append(entry.getValue())
					.append(", ");
		}
		sb.setLength(sb.length() - 2);
		sb.append("]");
		return sb.toString();
	}
	
	
	// ------ WARNING ------
	private static final double WORLD_BORDER_RADIUS = 100000;
	private double lastWarningLevel = 0;
	
	public void safelySetWarningLevel(double warning) {
		warning = Math.max(warning, 0);
		warning = Math.min(warning, 1);
		setWarningLevel(warning);
	}
	
	public void setWarningLevel(double warning) {
		setWarningLevel(warning, false);
	}
	
	private void setWarningLevel(double warning, boolean force) {
		checkArgument(warning >= 0, "Warning level must be positive (got %s)", warning);
		checkArgument(warning <= 1, "Warning level must be strictly less than 1 (got %s)", warning);
		if (warning == 1) warning = 0.999999;
		if (warning == lastWarningLevel) return;
		int blockWarning = (int) (WORLD_BORDER_RADIUS/(2 - 2*warning));

		WrapperPlayServerWorldBorder packet = new WrapperPlayServerWorldBorder();
		packet.setAction(EnumWrappers.WorldBorderAction.SET_WARNING_BLOCKS);
		packet.setWarningDistance(blockWarning);
		packet.sendPacket(player);

		lastWarningLevel = warning;
		sendDebugMsg("Set warning to: " + warning);
	}
	
	protected final void initialiseWarnings() {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();

		Location center = GameMap.getCurrentMap().getDwarfSpawn();
		double x = center.getX();
		double z = center.getZ();

		WrapperPlayServerWorldBorder packet1 = new WrapperPlayServerWorldBorder();
		packet1.setAction(EnumWrappers.WorldBorderAction.SET_CENTER);
		packet1.setCenterX(x);
		packet1.setCenterZ(z);
		packet1.sendPacket(player);
		
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.WORLD_BORDER);
		packet.getWorldBorderActions().write(0, EnumWrappers.WorldBorderAction.SET_SIZE);
		packet.getDoubles().write(2, WORLD_BORDER_RADIUS);
		try {
			pm.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			NightfallPlugin.logger().warning("Failed to update border radius");
		}
		
		setWarningLevel(lastWarningLevel, true);
	}
	
	public void clearWarning() {
		setWarningLevel(0);
	}
	
	
	// ------ MISC ------
	public boolean isSneaking() { return player.isSneaking(); }
	public boolean isSwimming() { return player.isSwimming(); }
	
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
	
	public void setEntityStatus(byte status) {
		PacketUtil.sendStatusPacket(player, status);
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
	
	// Abstract methods
	public abstract void updateHotbarSlot(ItemStack heldItem, int slot);
	public abstract boolean onBlockBreak(Block block, boolean didBreak);
	public abstract void onUse(ClickType click, Block clickedBlock, BlockFace blockFace); // TODO: tidyup
	public abstract void onShift(boolean sneaking);
	public abstract void onSwim(boolean swimming);
	public abstract void giveCompass();
	public abstract Location getRespawnLocation();
	public void onRespawn() {}
	
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
	
	public void doLater(Runnable task, int delay) {
		Updateable updateable = new LifetimeExpireable(delay) {
			@Override
			public void onExpiry() {
				super.onExpiry();
				task.run();
			}
		};
		addUpdateable(updateable);
	}
	
	public void removeUpdateable(Updateable updateable) {
		cooldownHolder.removeUpdateable(updateable);
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
		
		public GameEntityDamager(GameDamageType type, double damage, boolean force, Consumer<GameDamage<?,E>> damageModifier) {
			super(0);
			this.damager = entity -> {
				GameDamage<?,E> gameDamage = entity.createDamage(GamePlayer.this, type, damage);
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
