package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.PlayerSkin;
import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.upgrade.GlobalUpgrade;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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
		if (mobData.immuneTime != 0) {
			giveSpawnProtection(mobData.immuneTime*20);
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
	private final Set<Updateable> updateables = new HashSet<>();
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
	
	protected void addUpdateable(Updateable updateable) {
		this.updateables.add(updateable);
	}
	protected void setDisplayable(Displayable displayable) { this.displayable = displayable; }
	
	
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
	public Disguise getDisguise() {
		return DisguiseAPI.getDisguise(monster.getPlayer());
	}
	
	protected <T extends Disguise> void changeDisguise(Class<T> disguiseClass, Consumer<T> changer) {
		for (Disguise disguise : DisguiseAPI.getDisguises(monster.getPlayer())) {
			if (disguiseClass.isInstance(disguise)) {
				changer.accept(disguiseClass.cast(disguise));
			} else {
				Bukkit.getLogger().severe("Mob '" + monster.getName() + "' (type: " + type + ") has Disguise not of type " + disguiseClass.getName());
			}
		}
	}
	
	protected void changeDisguiseWatcher(Consumer<FlagWatcher> changer) {
		changeDisguiseWatcher(FlagWatcher.class, changer);
	}
	
	protected <T extends FlagWatcher> void changeDisguiseWatcher(Class<T> watcherClass, Consumer<T> changer) {
		for (Disguise disguise : DisguiseAPI.getDisguises(monster.getPlayer())) {
			FlagWatcher watcher = disguise.getWatcher();
			
			if (watcherClass.isInstance(watcher)) {
				changer.accept(watcherClass.cast(watcher));
			} else {
				Bukkit.getLogger().severe("Mob '" + monster.getName() + "' (type: " + type + ") has FlagWatcher not of type " + watcherClass.getName());
			}
		}
	}
	
	// ~~~~~ ITEMS ~~~~~
	protected void setupItems() {
		monster.clearInventory();
		
		if (mobData.hasWeapon()) {
			if (GlobalUpgrade.KRUNGOR.isUnlocked() && type != MobType.KRUNGOR) {
				getWeapon().addModifier(ItemModifierType.ATTACK, 5, "Torus Doom");
			}
			
			giveItem("weapon");
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
	
	
	protected CustomItem getWeapon() {
		return getItem("weapon");
	}
	protected CustomItem getArmour() {
		return getItem("armour");
	}
	
	protected CustomItem setWeapon(String newWepName) {
		return items.put("weapon", mobData.getAsWeapon(newWepName));
	}
	
	protected void makeItemMutable(String itemName) {
		CustomItem item = getItem(itemName);
		items.put(itemName, item.clone());
	}
	
	
	protected CustomItem getItem(String name) {
		return items.get(name);
	}
	
	protected void giveItem(String name) {
		giveItem(name, 1);
	}
	
	protected void giveItem(String name, int quantity) {
		monster.giveItem(items.get(name), quantity);
	}
	
	protected boolean isPlayerHoldingItem(String name) {
		CustomItem item = items.get(name);
		if (item == null)
			throw new IllegalArgumentException("No monster item found with name: " + name);
		return item.isSimilar(monster.getHeldItem());
	}
	
	protected boolean isPlayerHoldingWeapon() {
		return isPlayerHoldingItem("weapon");
	}
	
	protected void dropFakeItem(String name) {
		ItemStack itemStack = getItem(name).createItemStack();
		Item item = monster.getWorld().dropItemNaturally(monster.getEyeLocation(), itemStack);
		item.setPickupDelay(32767); // Never
		item.setTicksLived(6000 - 60*20);
	}
	
	protected void dropFakeWeapon() { dropFakeItem("weapon"); }
	
	
	// ~~~~~ Events/Overriding methods ~~~~~
	@Override
	public String getDeathMessageName() {
		return getTitledName();
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		int xpGain = 0;
		switch (damage.getType()) {
			case MELEE:
				damage.addPostDamageHandler(() -> playSound("melee"));
				damage.setArmourShred(mobData.armourShred);
				xpGain = 3;
				break;
			case RANGED:
				xpGain = 10;
				break;
				
			default:
				xpGain = 5;
				break;
		}
		int finalXpGain = xpGain;
		damage.addPostDamageHandler(() -> {
			monster.gainXP(finalXpGain);
		});
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
			monster.gainXP(mobData.torchXP);
		return didBreak;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (doubleSec)
			playSound("idle");
		
		for (Updateable updateable : updateables) {
			updateable.update();
			
			if (updateable instanceof Expirable) {
				Expirable expirable = (Expirable) updateable;
				if (expirable.hasExpired()) {
					updateables.remove(updateable);
				}
			}
		}
		
		shrineProtTick(halfSec);
	}
	
	@Override
	public float getCooldown() {
		return displayable.getCooldown();
	}
	
	
	@Override public int getCharmTime() { return mobData.charmTime; }
	@Override public double getShrineWeight() { return mobData.shrineWeight; }
	
	
	@Override public void onShift(boolean sneaking) {}
	@Override public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {}
	@Override public Projectile onBowFire(Arrow arrow, float force) { return null; }
	@Override public void onProjectileLand(Projectile proj, Block hitBlock, Entity hitEntity) {}
	
	
	// ~~~~~ Misc ~~~~~
	protected void playSound(String soundName) {
		mobData.playSound(soundName, monster);
	}
	
	
	protected void giveSpawnProtection(int time) {
		monster.givePotionEffect(PotionEffectType.LUCK, time, 1, true, false, true);
	}
	
	protected boolean hasSpawnProtection() {
		return monster.hasPotionEffect(PotionEffectType.LUCK);
	}
	
	
	// ~~~~~ Shrine Prot ~~~~~
	private static final int MAX_SHRINE_PROT_TIME = 50;
	private int shrineProtCounter = MAX_SHRINE_PROT_TIME;
	
	private void shrineProtTick(boolean halfSec) {
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
			if (halfSec) {
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
		
		if (hasPlayerDisguise())
			removePlayerDisguise();
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
		dyingEntity.getEquipment().setItemInMainHand(monster.getHeldItem());;
		
		dyingEntity.setHealth(0);
	}
	
	protected class DeadEntitySpawner<T extends LivingEntity> {
		private final Class<T> entityClass;
		private final Consumer<T> entityModifier;
		
		protected DeadEntitySpawner(Class<T> entityClass, Consumer<T> entityModifier) {
			this.entityClass = entityClass;
			this.entityModifier = t -> {
				setupDyingEntity(t);
				entityModifier.accept(t);
			};
		}
		
		private T spawn(Location location) {
			return location.getWorld().spawn(location, entityClass, entityModifier::accept);
		}
	}
}
