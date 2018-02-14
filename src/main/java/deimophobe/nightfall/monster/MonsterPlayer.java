package deimophobe.nightfall.monster;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.menu.SessionData;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.mob.*;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class MonsterPlayer extends GamePlayer implements SessionData, MonsterEntity<Player> {
	
	private final ComplexCooldown mobMenuShower = new ComplexCooldown(10, () -> MonsterManager.getManager().showMobMenu(this));
	
	private Mob mob;
	public Mob getMob() { return mob; }
	
	public MonsterPlayer(Player player) {
		super(player);
		kill(true);
	}
	
	@Override
	public void goOnline(Player player) {
		super.goOnline(player);
		resetToMobspawn();
	}
	
	@Override
	public void resetPlayer() {
		super.resetPlayer();
		resetToMobspawn();
	}
	
	public void resetToMobspawn() {
		teleportTo(GameMap.getCurrentMap().getCurrentMobspawn());
		kill(true);
	}
	
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		updateSeppuku();
		
		if (mob != null) {
			mob.update(quartSec, halfSec, sec, doubleSec, quadSec);
			
			if (seppukuCD > 0) {
				player.setExp(1 - (float)seppukuCD/MAX_SEPPUKU_CD);
			} else if (mob != null) {// Update could kill mob so need to do another null check
				player.setExp(mob.getCooldown());
			}
		}
		
		if (sec && isAlive()) {
			gainXP(10);
		}
		if (quartSec && isAlive() && isInShrine()) {
			if (mob.getType() != MobType.TICKER) gainXP(2);
		}
		
		usedThisTick = false;
		mobMenuShower.update();
		
		if (freezeTime > 0) {
			freezeTime--;
			if (freezeTime == 0) cancelFreeze();
		}
	}
	
	@Override
	public boolean isAI() {
		return false;
	}
	
	// ------ SPAWN AND DEATH ------
	public boolean isAlive() {
		return (player.getGameMode() == GameMode.SURVIVAL && mob != null);
	}
	
	public void kill(boolean silent) {
		if (!silent && isAlive()) {
			MonsterManager.getManager().queueDeathMessage(getDeathMessage());
			player.playSound(player.getLocation(), "proc", 1f, 0.7f);
			sendTitleMessage(ChatColor.DARK_RED + "You died!");
		}
		
		
		cancelFreeze();
		
		player.setAllowFlight(true);
		player.setGameMode(GameMode.SPECTATOR);
		
		killMob(silent);
		
		setTitle(ChatColor.GRAY, null, false);
		clearInventory();
		clearEffects();
		mobMenuShower.reset();
		cancelSeppuku();
	}
	
	private void killMob(boolean silent) {
		if (mob == null) return;
		
		mob.onDeath(silent);
		DisguiseAPI.undisguiseToAll(player);
		
		mob = null;
	}
	
	public void spawnMob(MobType type) {
		Mob mob;
		try {
			mob = type.createMob(this);
		} catch (Exception e) {
			sendMessage(ChatColor.RED + "Failed to spawn mob. (Internal server error)");
			NightfallPlugin.getPlugin().getLogger().severe(e.getMessage());
			e.printStackTrace();
			return;
		}
		spawnMob(mob);
	}
	
	public void spawnMob(Mob mob) {
		if (this.mob != null)
			kill(false);
		
		this.mob = mob;
		try {
			mob.onSpawn();
		} catch (Exception e) {
			sendMessage(ChatColor.RED + "Failed to spawn mob. (Internal server error)");
			NightfallPlugin.getPlugin().getLogger().severe(e.getMessage());
			e.printStackTrace();
			this.mob = null;
			return;
		}
		
		player.setAllowFlight(false);
		player.getInventory().setItem(9, seppuku);
		player.setGameMode(GameMode.SURVIVAL);
		Bukkit.getLogger().info("Spawning " + getName() + " as mob: " + mob.getType());
	}
	
	public boolean hasSpawnProtection() {
		return (mob != null && mob.hasSpawnProtection());
	}
	
	// ----- REBIRTH -----
	private final static int REBIRTH_TIME = 6*20;
	private Location lastRebirth = null;
	private BukkitRunnable rebirthKiller;
	private int rebirthCount;
	
	public boolean canRebirth() {
		return lastRebirth != null;
	}
	
	public void removeRebirth() {
		sendDebugMsg("Removing rebirth");
		lastRebirth = null;
		resetRebirthCount();
	}
	
	public Location getRebirthLocation() {
		return lastRebirth;
	}
	
	public void setRebirthSpot(Location location) {
		if (location == null) {
			removeRebirth();
			Bukkit.getLogger().warning("Setting a null rebirth location for " + getName() + ". Use removeRebirth() instead.");
			return;
		}
		lastRebirth = location;
		
		// Remove rebirth after amt of time - not sure about this.
		rebirthKiller = new BukkitRunnable() {
			@Override public void run() {removeRebirth();}
		};
		
		// Running a task while disabling throws an exception and causes badness
		if (!NightfallPlugin.getPlugin().isDisabling())
			rebirthKiller.runTaskLater(NightfallPlugin.getPlugin(), REBIRTH_TIME);
	}
	
	public void rebirth() {
		if (!canRebirth()) {
			Bukkit.getLogger().warning("Trying to rebirth for " + getName() + " but rebirth not active?!");
			return;
		}


		Mob zombie;
		if (this.getUpgrades(MobType.ZOMBIE).computeIfAbsent("husk", (k) -> 0) == 1) {
			zombie = new ZombieHusk(this, lastRebirth);
		}
		else if (this.getUpgrades(MobType.ZOMBIE).computeIfAbsent("fury", (k) -> 0) == 1) {
			zombie = new ZombieFury(this, lastRebirth);
		}
		else {
			return;
		}
		spawnMob(zombie);
		rebirthKiller.cancel();
	}
	
	public void incrementRebirthCount() {
		rebirthCount++;
	}
	
	public void resetRebirthCount() {
		rebirthCount = 0;
	}
	
	public int getRebirthCount() {
		return rebirthCount;
	}
	
	
	// ------ SEPPUKU ------
	private static final ItemStack seppuku = ItemManager.getMiscItem("seppuku").createItemStack();
	private static final ItemStack lightnigSeppuku = ItemManager.getMiscItem("lightning-seppuku").createItemStack();
	private final int MAX_SEPPUKU_CD = 100;
	private int seppukuCD;
	private void seppukuClick() {
		if (seppukuCD == 0) {
			seppukuCD = MAX_SEPPUKU_CD;
		} else if (seppukuCD < MAX_SEPPUKU_CD - 4) { // Prevents 'double clicking' the seppuku item
			cancelSeppuku();
		}
	}
	private void updateSeppuku() {
		if (seppukuCD == 0) return;
		
		seppukuCD--;
		
		if (seppukuCD == 0) {
			seppukuKill();
		}
	}
	private void instaSeppuku() {
		seppukuKill();
		getWorld().strikeLightningEffect(getLocation());
		seppukuCD = 0;
	}
	private void seppukuKill() {
		GameDamage damage = createDamage(null, GameDamageType.SEPPUKU, 10000);
		damage.instaKill();
		damage.fire(true);
	}
	public void replaceSeppuku() {
		if (DoomManager.getManager().isDoom()) {
			replaceItem(seppuku, lightnigSeppuku);
		} else {
			replaceItem(lightnigSeppuku, seppuku);
		}
	}
	private void cancelSeppuku() {
		seppukuCD = 0;
	}
	
	
	
	// ------ EXPERIENCE ------
	private int experience = 0;
	private int amountSpent = 0;
	private static final int MAX_XP = 10000;
	
	public void forceGainXP(int amt) {
		experience += amt;
		updateXPDisplay();
	}
	
	public void gainXP(int amt) {
		experience = Math.min(Math.max(experience, MAX_XP), experience + amt);
		updateXPDisplay();
	}
	
	public boolean useXP(int xpCost) {
		if (experience < xpCost) {
			return false;
		} else {
			experience -= xpCost;
			amountSpent += xpCost;
			updateXPDisplay();
			return true;
		}
	}
	
	public int getXP() {
		return experience;
	}

	public int getSpent() {
		return amountSpent;
	}

	public void resetSpent() {
		amountSpent = 0;
	}

	private void updateXPDisplay() {
		player.setLevel(experience);
		Game.getGame().setMana(player, experience);
	}
	
	
	public boolean isInShrine() {return GameMap.getCurrentMap().getCurrentShrineRegion().containsPlayer(this);}
	
	
	// ------ SPAWN/UPGRADE MENUS ------
	private Map<MobType, Map<String, Integer>> upgrades = new HashMap<>();
	
	public Map<String, Integer> getUpgrades(MobType type) {
		if (!upgrades.containsKey(type)) {
			Set<String> upgradeSet = MonsterManager.getManager().getUpgradeSet(type);
			
			Map<String, Integer> mobUpgrades = new HashMap<>();
			for (String upgrade : upgradeSet)
				mobUpgrades.put(upgrade, 0);
			
			upgrades.put(type, mobUpgrades);
		}
		return upgrades.get(type);
	}

	public void clearUpgrades() {
		upgrades = new HashMap<>();
	}
	
	// ------ DAMAGE ------
	@Override
	public MonsterDamage createDamage(GameEntity attacker, GameDamageType type, double damage) {
		return new MonsterDamage(attacker, this, type, damage);
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
	public boolean onBlockBreak(Block block, boolean didBreak) {
		if (mob != null) {
			return mob.onBlockBreak(block, didBreak);
		}
		return false;
	}
	
	private boolean usedThisTick = false;
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (usedThisTick) return;
		usedThisTick = true;
		
		if (!isAlive()) {
			mobMenuShower.tryUse();
			return;
		}
		
		if (isHolding(seppuku) || isHolding(lightnigSeppuku)) {
			if (DoomManager.getManager().isDoom()) {
				instaSeppuku();
			} else {
				seppukuClick();
			}
			return;
		}
		
		if (isFrozen())
			return;
		
		if (mob != null)
			mob.onUse(action, clickedBlock, blockFace);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		if (mob != null)
			mob.onDamageAttack(damage);
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		if (mob != null) {
			if (damage.getType() instanceof GameDamageType) {
				switch ((GameDamageType) damage.getType()) {
					case CONTACT:
					case DROWNING:
					case FIRE:
					case LAVA:
					case MAGMA_BLOCK:
					case FALL:
						damage.cancel();
						return;
				}
			}
			
			GameEntity attacker = damage.getAttacker();
			if (attacker instanceof AIEntity) {
				((AIEntity) attacker).forceUpdateTarget();
				damage.cancel();
			} else {
				mob.onDamageReceive(damage);
			}
		} else {
			damage.cancel();
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (mob != null)
			return mob.onBowFire(arrow, force);
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile projectile, Block hitBlock, Entity hitEntity) {
		if (mob != null)
			mob.onProjectileLand(projectile, hitBlock, hitEntity);
	}
	
	
	
	// ------ FREEZE/UNFREEZE ------
	private Location freezeLocation;
	private int freezeTime = -1;
	
	public void freeze(int time) {
		freezeTime = Math.max(time, freezeTime);
		
		givePotionEffect(PotionEffectType.LEVITATION, freezeTime, 0, true, false, true);
		givePotionEffect(PotionEffectType.GLOWING, freezeTime, 1, true, false, true);
		givePotionEffect(PotionEffectType.BLINDNESS, freezeTime, 1, true, false, true);
		
		if (!isFreezable()) return;
		if (isFrozen()) return;

		if (mob != null) {
			Disguise dis = mob.getDisguise();
			if (dis != null)
				dis.getWatcher().setGlowing(true);
		}
		
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed(0);
		player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
		player.setVelocity(new Vector(0,0,0));
		
		freezeLocation = getLocation();
		
		// Snap back into place if fast moving and lag changed position.
		new BukkitRunnable() {
			@Override
			public void run() {
				resetFrozen();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 5);
	}
	
	private void cancelFreeze() {
		if (!isFrozen()) return;
		player.removePotionEffect(PotionEffectType.GLOWING);
		player.removePotionEffect(PotionEffectType.BLINDNESS);
		if (this.getMob() instanceof EmberSprite) {
			this.givePermanentPotionEffect(PotionEffectType.LEVITATION, -2);
		} else if (this.getMob() instanceof Wraith) {
			this.givePermanentPotionEffect(PotionEffectType.LEVITATION, -1);
		} else {
			player.removePotionEffect(PotionEffectType.LEVITATION);
		}

		player.setFlySpeed(0.1f);
		player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
		
		if (isAlive()) {
			player.setFlying(false);
			player.setAllowFlight(false);
			
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
		if (mob != null && mob.getType() == MobType.TICKER)
			return false;
		
		return !player.hasPotionEffect(PotionEffectType.LUCK);
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
}
