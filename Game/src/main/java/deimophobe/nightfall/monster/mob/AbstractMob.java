package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.PlayerSkin;
import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.region.Region;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 13/04/17.
 */
public abstract class AbstractMob implements Mob {
	
	protected final MonsterPlayer monster;
	
	private final Map<String, CustomItem> items;
	private final MobData mobData;
	
	private final MobType type;
	@Override public MobType getType() { return type; }
	
	
	protected AbstractMob(MonsterPlayer monster, MobType type) {
		this.monster = monster;
		this.type = type;
		this.mobData = type.getMobData();
		this.items = mobData.getItems();
	}
	
	protected AbstractMob(MonsterPlayer monster, MobType type, MobData data) {
		this.monster = monster;
		this.type = type;
		this.mobData = data;
		this.items = data.getItems();
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		setTitle(mobData.forceTitle, mobData.title);
		setupItems();
		
		setupDisguise();
		
		checkForAnnotations();
		
		monster.clearEffects();
		if (mobData.immuneTime != 0 && spawnMethod != SpawnMethod.REBIRTH) {
			giveSpawnProtection(mobData.immuneTime*20, true, true);
		}
		
		
		Player player = monster.getPlayer();
		if (mobData.canRun) {
			player.setFoodLevel(20);
		} else {
			player.setFoodLevel(0);
		}
		player.setSaturation(1000000);
		
		monster.teleportTo(spawnMethod.getSpawnPoint(monster));
		monster.givePotionEffect(PotionEffectType.NIGHT_VISION, 10*60*60*20,1, false, false, true);
		playSound("spawn");
	}
	
	protected void setTitle(boolean force, String title) {
		if (force) {
			monster.setTitle(ChatColor.RED, title, true);
		} else {
			monster.setTitle(ChatColor.DARK_RED, null, false);
		}
	}
	
	protected String getTitledName() {
		if (mobData.forceTitle) {
			return ChatColor.RED + mobData.title + ChatColor.RESET;
		} else {
			return ChatColor.DARK_RED + mobData.title + " " + monster.getName() + ChatColor.RESET;
		}
	}
	
	
	// ~~~~~ ANNOTATIONS ~~~~~
	private final CooldownHolder cooldownHolder = new CooldownHolder();
	private Displayable displayable = Displayable.DISPLAY_NOTHING;
	
	private void checkForAnnotations() {
		Class<?> processingClass = this.getClass();
		while (processingClass != AbstractMob.class) {
			Field[] fields = processingClass.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				
				try {
					checkFieldForAnnotation(field, Update.class, Updateable.class, this::addUpdateable);
					checkFieldForAnnotation(field, Display.class, Displayable.class, this::setDisplayable);
				} catch (InvalidFieldAnnotationException e) {
					throw new InvalidFieldAnnotationException("Invalid field in class " + processingClass.getName(), e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			processingClass = processingClass.getSuperclass();
		}
	}
	
	
	private <T> void checkFieldForAnnotation(Field field, Class<? extends Annotation> annotationClass, Class<T> fieldType, Consumer<T> fieldApplier)
			throws IllegalAccessException, InvalidFieldAnnotationException
	{
		if (field.isAnnotationPresent(annotationClass)) {
			Object value = field.get(this);
			if (fieldType.isInstance(value)) {
				fieldApplier.accept(fieldType.cast(value));
			} else {
				throw new InvalidFieldAnnotationException(
						"Field " + field.getName()
						+ " has @" + annotationClass.getName() + " annotation "
						+ " but does not implement" +  fieldType.getName()
				);
			}
		}
	}
	
	private static class InvalidFieldAnnotationException extends RuntimeException {
		public InvalidFieldAnnotationException(String s) { super(s); }
		public InvalidFieldAnnotationException(String s, Throwable throwable) { super(s, throwable); }
	}
	
	
	protected final void addUpdateable(Updateable updateable) { cooldownHolder.addUpdateable(updateable); }
	protected final void setDisplayable(Displayable displayable) { this.displayable = displayable; }
	
	
	// ~~~~~ DISGUISES ~~~~~
	protected void setupDisguise() {
		DisguiseType type = mobData.disguiseType;
		if (type != null) {
			if (hasPlayerDisguise()) {
				setupPlayerDisguise();
			} else {
				setupMobDisguise(type);
			}
		}
	}
	
	protected void setupPlayerDisguise() {
		SkinManager.getManager().addSkinChange(monster, new PlayerSkin(getTitledName(), mobData.skinName));
		MonsterManager.getManager().addToTeam(getTitledName());
	}
	
	protected void removePlayerDisguise() {
		SkinManager.getManager().removeSkinChange(monster);
	}
	
	protected void setupMobDisguise(DisguiseType type) {
		Player player = monster.getPlayer();
		
		MobDisguise disguise = new MobDisguise(type);
		disguise.getWatcher().setCustomNameVisible(false);
		disguise.getWatcher().setCustomName(getTitledName());
		//TODO add more sounds so this isn't weird
		//disguise.setHearSelfDisguise(false);
		//disguise.setReplaceSounds(false);
		disguise.setViewSelfDisguise(false);
		DisguiseAPI.disguiseEntity(player, disguise);
		
		MonsterManager.getManager().addToTeam(disguise.getEntity().getUniqueId().toString());
	}
	
	private boolean hasPlayerDisguise() {
		return (mobData.disguiseType == DisguiseType.PLAYER);
	}
	
	@Override
	@Deprecated
	public Disguise getDisguise() {
		return DisguiseAPI.getDisguise(monster.getPlayer());
	}
	
	@Override
	public final <T extends Disguise> void changeDisguise(Class<T> disguiseClass, Consumer<T> changer) {
		for (Disguise disguise : DisguiseAPI.getDisguises(monster.getPlayer())) {
			if (disguiseClass.isInstance(disguise)) {
				changer.accept(disguiseClass.cast(disguise));
			} else {
				NightfallPlugin.logger().severe("Mob '" + monster.getName() + "' (type: " + type + ") has Disguise not of type " + disguiseClass.getName());
			}
		}
	}
	
	@Override
	public final void changeDisguiseWatcher(Consumer<FlagWatcher> changer) {
		changeDisguiseWatcher(FlagWatcher.class, changer);
	}
	
	@Override
	public final <T extends FlagWatcher> void changeDisguiseWatcher(Class<T> watcherClass, Consumer<T> changer) {
		for (Disguise disguise : DisguiseAPI.getDisguises(monster.getPlayer())) {
			FlagWatcher watcher = disguise.getWatcher();
			
			if (watcherClass.isInstance(watcher)) {
				changer.accept(watcherClass.cast(watcher));
			} else {
				NightfallPlugin.logger().severe("Mob '" + monster.getName() + "' (type: " + type + ") has FlagWatcher not of type " + watcherClass.getName());
			}
		}
	}
	
	// ~~~~~ ITEMS ~~~~~
	private static final String WEAPON_NAME = "weapon";
	private static final String ARMOUR_NAME = "armour";
	protected void setupItems() {
		monster.clearInventory();
		
		if (mobData.hasWeapon()) {
			giveItem(WEAPON_NAME);
		}
		if (mobData.hasArmour()) {
			setArmour();
		}
		monster.delayedHealMax();
	}
	
	protected void setArmour() {
		PlayerInventory inv = monster.getPlayer().getInventory();
		mobData.armourSlot.equipArmour(inv, getArmour().createItemStack());
	}
	
	
	public final CustomItem getWeapon() {
		return getItem(WEAPON_NAME);
	}
	protected final CustomItem getArmour() {
		return getItem(ARMOUR_NAME);
	}
	
	protected final CustomItem setWeapon(String newWepName) {
		return items.put(WEAPON_NAME, mobData.getAsWeapon(newWepName));
	}
	
	public final boolean doesWeaponExist() {
		return doesItemExist(WEAPON_NAME);
	}
	
	protected final void makeItemMutable(String itemName) {
		CustomItem item = getItem(itemName);
		items.put(itemName, item.clone());
	}
	
	
	protected final CustomItem getItem(String name) {
		checkArgument(items.containsKey(name), "No item with name '%s' for mob of type '%s'", name, type);
		return items.get(name);
	}
	
	protected final boolean doesItemExist(String name) {
		return items.containsKey(name);
	}
	
	protected final void giveItem(String name) {
		giveItem(name, 1);
	}
	
	protected final void giveItem(String name, int quantity) {
		monster.giveItem(items.get(name), quantity);
	}
	
	protected final boolean isPlayerHoldingItem(String name) {
		CustomItem item = items.get(name);
		if (item == null) throw new IllegalArgumentException("No monster item found with name: " + name);
		
		return item.isSimilar(monster.getHeldItem());
	}
	
	protected final boolean isPlayerHoldingWeapon() {
		return isPlayerHoldingItem(WEAPON_NAME);
	}
	
	protected final boolean hasItem(String name) {
		CustomItem item = getItem(name);
		return monster.hasItem(item);
	}
	
	protected final boolean hasItem(String name, int amount) {
		CustomItem item = getItem(name);
		return monster.hasItem(item, amount);
	}
	
	protected final boolean removeItem(String name) {
		CustomItem item = getItem(name);
		return monster.removeItem(item);
	}
	
	protected final boolean removeItem(String name, int amount) {
		CustomItem item = getItem(name);
		return monster.removeItems(item, amount);
	}
	
	protected final void dropFakeItem(String name) {
		ItemStack itemStack = getItem(name).createItemStack();
		Item item = monster.getWorld().dropItemNaturally(monster.getEyeLocation(), itemStack);
		item.setPickupDelay(32767); // Never
		item.setTicksLived(6000 - 60*20);
	}
	
	protected final void dropFakeWeapon() { dropFakeItem(WEAPON_NAME); }
	
	
	// ~~~~~ Events/Overriding methods ~~~~~
	@Override
	public String getDeathMessageName() {
		return getTitledName();
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		int xpGain;
		switch (damage.getType()) {
			// Melee type damages
			case MELEE:
			case MINOTAUR_CHARGE:
			case WRAITH_CHARGE:
				damage.addPostDamageHandler(() -> playSound("melee"));
				damage.setArmourShred(mobData.armourShred);
				xpGain = 3;
				break;
			// Ranged type damages
			case RANGED:
			case WITHER_SKULL:
				xpGain = 10;
				break;
			// Explosion type damages
			case GOBO_KABOOM:
			case GOBO_BOX_EXPLOSION:
			case BLAZE_EXPLOSION:
			case HUSK_STOMP:
			case IMPACT_AOE:
				xpGain = 5;
				break;
			default:
				xpGain = 0;
				break;
		}
		int finalXpGain = xpGain;
		damage.addPostDamageHandler(() -> monster.gainExp(finalXpGain));
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		if (hasSpawnProtection()) {
			damage.setProc(false);
			damage.cancel();
			return;
		}
		
		if (!mobData.proccable) damage.setProc(false);
		damage.getMultiPartDamage().timesMult(1 - mobData.damageRes);
		damage.getArrowRes().setBase(mobData.arrowRes);
		
		damage.addPostDamageHandler(() -> {
			playSound("hurt");
			if (hasDisguise())
				monster.playSound("entity.generic.hurt", 1f, 1f, true);
		});
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		if (block.getType() == Material.TORCH && didBreak)
			monster.gainExp(mobData.torchXP);
		return didBreak;
	}
	
	public final boolean everyNthTick(int n) {
		return monster.everyNthTick(n);
	}
	
	@Override
	public void update() {
		if (everyNthTick(40)) {
			playSound("idle");
		}
		
		cooldownHolder.update();
		shrineProtTick();
		
		
		if (everyNthTick(20) && Game.getGame().getPhase() == Phase.GAME) {
			monster.gainExp(monster.getExpRate());
		}
		if (everyNthTick(5)) {
			Region shrineRegion = GameMap.getCurrentMap().getCurrentShrineRegion();
			if (shrineRegion.containsPlayer(monster) && getShrineWeight() != 0) {
				monster.gainExp(2);
			}
		}
	}
	
	@Override
	public float getCooldown() {
		return displayable.getCooldown();
	}
	
	
	@Override public int getCharmTime() { return mobData.charmTime; }
	@Override public double getShrineWeight() { return mobData.shrineWeight; }
	
	
	@Override public void onShift(boolean sneaking) {}
	@Override public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {}
	@Override public Projectile onBowFire(Arrow arrow, float force) { return null; }
	@Override public void onProjectileLand(Projectile proj, Block hitBlock, Entity hitEntity) {}
	
	
	// ~~~~~ Misc ~~~~~
	protected final void playSound(String soundName) {
		mobData.playSound(soundName, monster);
	}
	
	
	protected final void giveSpawnProtection(int time, boolean invisible, boolean strength) {
		monster.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, time, 10, true, false, true);
		if (invisible) monster.givePotionEffect(PotionEffectType.INVISIBILITY, time, 1, true, false, true);
		if (strength) monster.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, time, 5, true, false, true);
	}
	
	protected final void givePermanentSpawnProtection(boolean invisible) {
		monster.givePermanentPotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10);
		if (invisible) monster.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
	}
	
	@Override
	public final boolean hasSpawnProtection() {
		return monster.getPotionEffectLevel(PotionEffectType.DAMAGE_RESISTANCE) == 10;
	}
	
	protected final void removeSpawnProtection() {
		if (!hasSpawnProtection()) return;
		
		monster.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		monster.removePotionEffect(PotionEffectType.INVISIBILITY);
		monster.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
	}
	
	
	// ~~~~~ Shrine Prot ~~~~~
	private static final int MAX_SHRINE_PROT_TIME = 50;
	private int shrineProtCounter = MAX_SHRINE_PROT_TIME;
	
	private void shrineProtTick() {
		if (isShrineImmune()) return;
		
		boolean inShrine = GameMap.getCurrentMap().getCurrentShrineProtection().containsPlayer(monster);
		if (inShrine) { // Is stopped
			monster.getPlayer().spawnParticle(Particle.VILLAGER_ANGRY, monster.getEyeLocation().subtract(0, 0.5, 0), 15, 1.5, 1, 1.5);
			
			if (shrineProtCounter > 0)
				shrineProtCounter--;
			
			if (shrineProtCounter == 0) {
				shrineProtectionDamage();
				shrineProtCounter = MAX_SHRINE_PROT_TIME;
			}
		} else {
			if (everyNthTick(10)) {
				if (shrineProtCounter < MAX_SHRINE_PROT_TIME)
					shrineProtCounter++;
			}
		}
	}
	
	protected void shrineProtectionDamage() {
		if (isShrineImmune()) return;
		
		double damage = mobData.shrineProtDamage;
		if (damage == -1) {
			monster.instaKill(null, GameDamageType.SHRINE_PROTECTION);
		} else {
			monster.doDamage(null, GameDamageType.SHRINE_PROTECTION, damage, true);
		}
		
		Location loc = monster.getLocation();
		loc.getWorld().strikeLightningEffect(loc);
	}
	
	private boolean isShrineImmune() {
		return (mobData.shrineProtDamage == 0);
	}
	
	
	// ~~~~~ Death ~~~~~
	
	@Override
	public void onDeath(boolean silent) {
		if (!silent) {
			playSound("death");
			displayDeathAnimation();
			
			if (mobData.forceTitle)
				Bukkit.spigot().broadcast(monster.getDeathMessage());
		}
		
		DisguiseAPI.undisguiseToAll(monster.getPlayer());
		if (hasPlayerDisguise()) {
			removePlayerDisguise();
		}
	}
	
	protected void displayDeathAnimation() {
		DeadEntitySpawner spawner = getDeadEntitySpawner();
		if (spawner != null) {
			spawner.spawn(monster.getLocation());
		}
	}
	
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		Disguise disguise = getDisguise();
		if (disguise == null) return null;
		EntityType type = disguise.getType().getEntityType();
		if (type.isAlive() && type.isSpawnable() && type != EntityType.PLAYER) {
			Class<? extends Entity> disguiseClass = getDisguise().getType().getEntityClass();
			return new DeadEntitySpawner(disguiseClass, entity -> {});
		}
		return null;
	}
	
	protected final void setupDyingEntity(LivingEntity dyingEntity) {
		dyingEntity.teleport(monster.getLocation());
		dyingEntity.setVelocity(monster.getVelocity());
		dyingEntity.setFireTicks(0);
		dyingEntity.setInvulnerable(true);
		dyingEntity.setSilent(true);
		dyingEntity.setCanPickupItems(false);
		dyingEntity.setCollidable(false);
		dyingEntity.setCustomName(getTitledName());
		dyingEntity.getEquipment().setArmorContents(monster.getPlayer().getInventory().getArmorContents());
		dyingEntity.getEquipment().setItemInMainHand(monster.getHeldItem());
		
		dyingEntity.setHealth(0);
	}
	
	protected class DeadEntitySpawner<T extends LivingEntity> {
		private final Class<T> entityClass;
		private final Consumer<T> entityModifier;
		
		protected DeadEntitySpawner(Class<T> entityClass, Consumer<T> entityModifier) {
			this.entityClass = entityClass;
			this.entityModifier = t -> {
				entityModifier.accept(t);
				setupDyingEntity(t);
			};
		}
		
		private T spawn(Location location) {
			return location.getWorld().spawn(location, entityClass, entityModifier::accept);
		}
	}
}
