package deimophobe.dvz.monster;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import deimophobe.dvz.*;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.menu.SessionData;
import deimophobe.dvz.monster.mob.Bopen;
import deimophobe.dvz.monster.mob.Mob;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.monster.upgrade.MobUpgrade;
import deimophobe.dvz.shrine.ShrineManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.watchers.LivingWatcher;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class MonsterPlayer extends GamePlayer implements SessionData {
	
	private Mob mob;
	
	public Mob getMob() { return mob; }
	
	public MonsterPlayer(Player player) {
		super(player);
		entity.sendMessage("You are monster now. Deimo make this cool.");
		
		mob = null;
	}
	
	@Override
	public void goOnline(Player player) {
		super.goOnline(player);
		resetToMobspawn();
	}
	
	public void resetToMobspawn() {
		teleportTo(ShrineManager.getManager().getCurrentMobspawn());
		kill();
	}
	
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		entity.setFoodLevel(20);
		entity.setSaturation(20);
		
		updateSeppuku();
		
		if (mob != null) {
			mob.update(quartSec, halfSec, sec, doubleSec, quadSec);
			
			if (seppukuCD > 0) {
				entity.setExp(1 - (float)seppukuCD/MAX_SEPPUKU_CD);
			} else if (mob != null) {// Update could kill mob so need to do another null check
				entity.setExp(mob.getCooldown());
			}
		}
		
		if (sec && isAlive()) {
			gainXP(isInShrine() ? 2 : 1);
		}
	}
	
	
	// ------ SPAWN AND DEATH ------
	public boolean isAlive() {
		return (entity.getGameMode() == GameMode.SURVIVAL && mob != null);
	}
	
	private void killLater() {
		new BukkitRunnable() {
			@Override
			public void run() {
				kill();
			}
		}.runTaskLater(Game.getGame().getPlugin(), 1);
	}
	
	public void kill() {
		if (isAlive()) {
			ActionBarAPI.sendActionBarToAllPlayers(generateDeathMessage(), 60);
			Bukkit.broadcastMessage(generateDeathMessage());
			entity.playSound(entity.getLocation(), "proc", 1f, 0.7f);
		}
		
		killMob();
		cancelFreeze();
		
		setTitle(ChatColor.GRAY, null, false);
		
		entity.setAllowFlight(true);
		entity.setGameMode(GameMode.SPECTATOR);
		clearInventory();
		clearEffects();
	}
	
	private void killMob() {
		if (mob == null) return;
		
		mob.onDeath();
		
		Disguise disguise = DisguiseAPI.getDisguise(entity);
		if (disguise != null) {
			EntityType entityType = disguise.getType().getEntityType();
			if (entityType.isAlive() && entityType != EntityType.PLAYER) {
				LivingEntity dyingEntity = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), entityType);
				dyingEntity.teleport(dyingEntity);
				dyingEntity.setVelocity(dyingEntity.getVelocity());
				dyingEntity.setCustomName(disguise.getWatcher().getCustomName());
				dyingEntity.getEquipment().setArmorContents(entity.getInventory().getArmorContents());
				dyingEntity.getEquipment().setItemInMainHand(getHeldItem());
				dyingEntity.damage(10000);
			}
		}
		DisguiseAPI.undisguiseToAll(entity);
		
		mob = null;
	}
	
	public void spawnMobType(MobType type) {
		spawnMob(type.createMob(this));
	}
	
	public void spawnMob(Mob mob) {
		spawnMobAt(mob, ShrineManager.getManager().getCurrentMobspawn());
	}
	
	public void spawnMobAt(Mob mob, Location location) {
		this.mob = mob;
		mob.spawn();
		entity.setAllowFlight(false);
		entity.getInventory().setItem(9, seppuku);
		if (location != null)
			teleportTo(location);
		entity.setGameMode(GameMode.SURVIVAL);
	}
	
	// ----- REBIRTH -----
	private Location lastRebirth = null;
	
	public boolean canRebirth() {
		return lastRebirth != null;
	}
	
	public void removeRebirth() {
		lastRebirth = null;
	}
	
	public void setRebirthSpot(Location location) {
		if (location == null) {
			removeRebirth();
			Bukkit.getLogger().warning("Setting a null rebirth location for " + getName() + ". Use removeRebirth() instead.");
			return;
		}
		lastRebirth = location;
	}
	
	public void rebirth() {
		if (!canRebirth()) {
			Bukkit.getLogger().warning("Trying to rebirth for " + getName() + " but rebirth not active?!");
			return;
		}
		
		this.mob = MobType.ZOMBIE.createMob(this);
		spawnMobAt(mob, lastRebirth);
	}
	
	
	// ------ SEPPUKU ------
	private final int MAX_SEPPUKU_CD = 100;
	private int seppukuCD;
	private void seppukuClick() {
		if (seppukuCD == 0)
			seppukuCD = MAX_SEPPUKU_CD;
		else if (seppukuCD > 0)
			seppukuCD = 0;
	}
	private void updateSeppuku() {
		if (seppukuCD == 0) return;
		
		seppukuCD--;
		
		if (seppukuCD == 0) {
			customDamage(null, DamageType.SEPPUKU, 10000);
		}
	}
	
	
	
	// ------ EXPERIENCE ------
	private int experience = 0;
	private static final int MAX_XP = 1000;
	
	public void gainXP(int amt) {
		experience += amt;
		if (experience > MAX_XP) experience = MAX_XP;
		updateXPDisplay();
	}
	
	public boolean useXP(int xpCost) {
		if (experience < xpCost) {
			return false;
		} else {
			experience -= xpCost;
			updateXPDisplay();
			return true;
		}
	}
	
	public int getXP() {
		return experience;
	}
	
	private void updateXPDisplay() {
		entity.setLevel(experience);
		Game.getGame().setMana(entity, experience);
	}
	
	
	
	// ------ SPAWN/UPGRADE MENUS ------
	private final Map<MobType, MobUpgrade> upgrades = new HashMap<>();
	public void showMobMenu() {
		MonsterManager.getManager().showMobMenu(this);
	}
	
	public MobUpgrade getUpgrades(MobType type) {
		if (upgrades.containsKey(type))
			return upgrades.get(type);
		else {
			MobUpgrade emptyUpgrades = new MobUpgrade();
			upgrades.put(type, emptyUpgrades);
			return emptyUpgrades;
		}
	}
	
	
	
	
	// ------ EVENT METHODS ------
	@Override
	public void updateHotbarSlot(ItemStack heldItem, int slot) {
		
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (mob != null)
			mob.onShift(sneaking);
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (mob != null) {
			if (block.getType() == Material.TORCH)
				gainXP(mob.getTorchXP());
			
			mob.onBlockBreak(block);
		}
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (isHolding(seppuku)) {
			seppukuClick();
			return;
		}
		
		if (mob != null)
			mob.onUse(action, clickedBlock, blockFace);
	}
	
	@Override
	public double onHit(GameEntity gamePlayer, DamageType type, double damage) {
		if (mob != null) {
			if (gamePlayer instanceof Dwarf) {
				((Dwarf) gamePlayer).getArmour().damage(mob.getArmourShred());
				gainXP(isInShrine() ? 2 : 1);
				return mob.onHit((Dwarf) gamePlayer, type, damage);
			} else {
				Bukkit.getLogger().warning("GameEntity in onGotHit should be a Dwarf");
				return damage;
			}
		} else {
			return damage;
		}
	}
	
	@Override
	public double onGotHit(GameEntity gameEntity, DamageType type, double damage) {
		// Spawn protection
		if (entity.hasPotionEffect(PotionEffectType.LUCK)) {
			return -1;
		}
		
		damage = type.getMobDamage(damage);
		if (damage == -1)
			return -1;
		
		if (mob != null) {
			damage *= (1 - mob.getResistance());
			
			if (type.isArrow())
				damage *= (1 - mob.getArrowRes());
			
			if (gameEntity instanceof Dwarf || gameEntity == null) {
				return mob.onGotHit((Dwarf) gameEntity, type, damage);
			} else {
				Bukkit.getLogger().warning("GameEntity in onGotHit should be a Dwarf");
				return damage;
			}
		} else {
			return damage;
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (mob != null)
			return mob.onBowFire(arrow, force);
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
		if (mob != null)
			mob.onProjectileLand(arrow, hitBlock);
	}
	
	
	
	// ------ FREEZE/UNFREEZE ------
	private Location freezeLocation;
	public void freeze(int time) {
		if (!isFreezable()) return;
		if (isFrozen()) return;
		
		givePotionEffect(PotionEffectType.LEVITATION, time, 0, true, true, true);
		givePotionEffect(PotionEffectType.GLOWING, time, 1, true, true, true);
		
		if (mob != null) {
			Disguise dis = mob.getDisguise();
			if (dis != null)
				dis.getWatcher().setGlowing(true);
		}
		
		entity.setAllowFlight(true);
		entity.setFlying(true);
		entity.setFlySpeed(0);
		entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
		entity.setVelocity(new Vector(0,0,0));
		
		freezeLocation = getLocation();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				cancelFreeze();
			}
		}.runTaskLater(Game.getGame().getPlugin(), time);
		
		// Snap back into place if fast moving and lag changed position.
		new BukkitRunnable() {
			@Override
			public void run() {
				resetFrozen();
			}
		}.runTaskLater(Game.getGame().getPlugin(), 5);
	}
	
	private void cancelFreeze() {
		if (!isFrozen()) return;
		entity.removePotionEffect(PotionEffectType.LEVITATION);
		entity.removePotionEffect(PotionEffectType.GLOWING);
		
		entity.setFlySpeed(0.1f);
		entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
		
		if (isAlive()) {
			entity.setFlying(false);
			entity.setAllowFlight(false);
			
			Disguise dis = mob.getDisguise();
			if (dis != null)
				dis.getWatcher().setGlowing(false);
		}
			
		teleportTo(freezeLocation, true);
		
		freezeLocation = null;
	}
	
	public void resetFrozen() {
		if (isFrozen())
			teleportTo(freezeLocation, true);
	}
	
	public boolean isFrozen() {
		return (freezeLocation != null);
	}
	
	private boolean isFreezable() {
		if (entity.hasPotionEffect(PotionEffectType.LUCK))
			return false;
		
		return true;
	}
	
	@Override
	public void setVelocity(Vector vel) {
		if (mob instanceof Bopen) {
			((Bopen) mob).dismountHorse();
			/*
			Bopen bopen = (Bopen) mob;
			if (bopen.isRidingHorse()) {
				bopen.getHorse().setVelocity(vel.clone().multiply(10));
			}
			*/
		}
		
		if (!isFrozen())
			super.setVelocity(vel);
	}
	
	
	// ------ MISC ------
	
	private static final ItemStack seppuku;
	static {
		seppuku = new ItemStack(Material.GHAST_TEAR, 1);
		ItemMeta meta = seppuku.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Seppuku");
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_PURPLE + "What a failure of a monster");
		lore.add(ChatColor.DARK_PURPLE + "you have become.");
		meta.setLore(lore);
		
		seppuku.setItemMeta(meta);
	}
	
	private boolean isInShrine() {
		return ShrineManager.getManager().getShrine().getShrineRegion().containsPlayer(this);
	}
}
