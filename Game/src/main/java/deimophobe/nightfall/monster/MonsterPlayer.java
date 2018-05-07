package deimophobe.nightfall.monster;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.menu.SessionData;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.event.MobSpawnEvent;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.mob.Bopen;
import deimophobe.nightfall.monster.mob.FloatyMob;
import deimophobe.nightfall.monster.mob.Mob;
import deimophobe.nightfall.monster.mob.MobType;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.apache.commons.lang.math.NumberUtils;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

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
		teleportTo(GameMap.getCurrentMap().getCurrentMobspawn());
	}
	
	@Override
	public void goOffline() {
		kill(true);
		super.goOffline();
	}
	
	@Override
	public void onRemove() {
		kill(true);
		player.setGameMode(GameMode.ADVENTURE);
		super.onRemove();
	}
	
	public void update() {
		super.update();
		updateSeppuku();
		
		if (mob != null) {
			mob.update();
			
			if (seppukuCD > 0) {
				player.setExp(1 - (float)seppukuCD/MAX_SEPPUKU_CD);
			} else if (mob != null) {// Update could kill mob so need to do another null check
				player.setExp(mob.getCooldown());
			}
		}
		
		if (everySec() && isAlive() && Game.getGame().getPhase() == Phase.GAME) {
			gainXP(expRate);
		}
		if (everyNthTick(5) && isAlive() && isInShrine()) {
			if (mob.getShrineWeight() != 0) gainXP(2);
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
		return (mob != null);
	}
	
	public void kill(boolean silent) {
		if (!silent && isAlive()) {
			MonsterManager.getManager().queueDeathMessage(getDeathMessage().toPlainText());
			player.playSound(player.getLocation(), "proc", 1f, 0.7f);
			sendTitleMessage(ChatColor.DARK_RED + "You died!");
		}
		
		boolean frozenDeath = isFrozen();
		
		cancelFreeze();
		
		if (mob != null) {
			mob.onDeath(silent);
			mob = null;
		}
		if (frozenDeath) removeRebirth();
		
		setTitle(ChatColor.GRAY, null, false);
		clearInventory();
		clearEffects();
		mobMenuShower.reset();
		cancelSeppuku();
		
		player.setAllowFlight(true);
		player.setGameMode(GameMode.SPECTATOR);
	}
	
	public boolean spawnMob(MobType type) {
		return spawnMob(type, SpawnMethod.SPAWN);
	}
	
	public boolean spawnMob(MobType type, SpawnMethod spawnMethod) {
		try {
			Mob mob = type.createMob(this);
			return spawnMob(mob, spawnMethod);
		} catch (Exception e) {
			sendMessage(ChatColor.RED + "Failed to create mob. (Internal server error). Please notify a dev about this.");
			NightfallPlugin.logger().severe("Failed to create mob of type " + type + " for " + getName());
			NightfallPlugin.logger().severe(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean spawnMob(Mob mob, SpawnMethod spawnMethod) {
		Logger logger = NightfallPlugin.logger();
		
		if (this.mob != null) kill(false);
		if (player.isDead()) {
			logger.warning("Could not spawn " + getName() + " as mob " + mob.getType() + " with method " + spawnMethod + " as player is dead.");
			return false;
		}
		
		MobSpawnEvent event = new MobSpawnEvent(this, mob, spawnMethod);
		Bukkit.getPluginManager().callEvent(event);
		
		this.mob = mob;
		try {
			mob.onSpawn(spawnMethod);
			
			if (spawnMethod == SpawnMethod.REBIRTH) {
				rebirthCount++;
			} else {
				removeRebirth();
			}
			
			player.getInventory().setItem(9, seppuku);
			player.setGameMode(GameMode.SURVIVAL);
			player.setAllowFlight(false);
			logger.info("Spawning " + getName() + " as mob " + mob.getType() + " (via " + spawnMethod + ")");
			
			return true;
		} catch (Exception e) {
			sendMessage(ChatColor.RED + "Failed to spawn mob. (Internal server error). Please notify a dev about this.");
			logger.severe("Failed to spawn " + getName() + " as mob " + mob.getType() + " with method " + spawnMethod);
			logger.severe(e.getMessage());
			e.printStackTrace();
			this.mob = null;
			return false;
		}
	}
	
	private static final Set<MobType> UPGRADEABLE_MOBS = EnumSet.of(MobType.ZOMBIE, MobType.SKELETON, MobType.GOBO);
	/** Chooses the mob with the most upgrades upgraded. */
	public MobType getPrimaryMob() {
		return Misc.getArgMax(UPGRADEABLE_MOBS, mobType -> {
			int total = 0;
			for (int level : getUpgrades(mobType).values()) {
				total += level;
			}
			return total;
		});
	}
	
	// ----- REBIRTH -----
	private final static int REBIRTH_TIME = 6*20;
	private int lastRebirthSetTime = 0;
	private Location lastRebirth = null;
	private int rebirthCount = 0;
	
	public boolean canRebirth() {
		return (lastRebirth != null) && (Game.getGame().getCurrentTick() < lastRebirthSetTime + REBIRTH_TIME);
	}
	
	public void removeRebirth() {
		sendDebugMsg("Removing rebirth");
		lastRebirth = null;
		rebirthCount = 0;
	}
	
	public void setRebirthSpot(Location location, Function<Integer, Double> chanceFunction) {
		double chance = chanceFunction.apply(rebirthCount);
		if (Math.random() < chance) {
			sendDebugMsg("Successfully set rebirth; Chance: " + chance);
			lastRebirth = location;
			lastRebirthSetTime = Game.getGame().getCurrentTick();
		} else {
			sendDebugMsg("Failed to set rebirth; Chance: " + chance);
			lastRebirth = null;
		}
	}
	
	Location getRebirthLocation() {
		return lastRebirth;
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
	private int expRate = 10;
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
	
	public void setXPRate(int rate) {
		expRate = rate;
	}
	
	public int getXP() {
		return experience;
	}

	public int getSpent() {
		return amountSpent;
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
			for (String upgrade : upgradeSet) {
				mobUpgrades.put(upgrade, 0);
			}
			
			upgrades.put(type, mobUpgrades);
		}
		return upgrades.get(type);
	}

	public void resetUpgrades(double refundRate) {
		upgrades.clear();
		forceGainXP((int) (refundRate * amountSpent));
		amountSpent = 0;
	}
	
	// ------ DAMAGE ------
	@Override
	public MonsterDamage createDamage(GameEntity attacker, GameDamageType type, double damage, Projectile projectile) {
		return new MonsterDamage(attacker, this, type, damage, projectile);
	}
	
	@Override
	public String getDeathMessageName() {
		if (mob != null) return mob.getDeathMessageName();
		else return super.getDeathMessageName();
	}
	
	// ------ EVENT METHODS ------
	@Override
	public void updateHotbarSlot(ItemStack heldItem, int slot) {
		
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (isFrozen()) return;
		
		if (mob != null) {
			mob.onShift(sneaking);
		}
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
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
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
		
		if (isFrozen()) return;
		
		if (mob != null) {
			mob.onUse(click, clickedBlock, blockFace);
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		if (mob != null) {
			mob.onDamageAttack(damage);
		}
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		if (mob != null) {
			switch (damage.getType()) {
				case CONTACT:
				case DROWNING:
				case FIRE:
				case LAVA:
				case MAGMA_BLOCK:
				case FALL:
					damage.cancel();
					return;
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
		if (mob != null) {
			return mob.onBowFire(arrow, force);
		}
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile projectile, Block hitBlock, Entity hitEntity) {
		if (mob != null) {
			mob.onProjectileLand(projectile, hitBlock, hitEntity);
		}
	}
	
	
	
	// ------ FREEZE/UNFREEZE ------
	private Location freezeLocation;
	private int freezeTime = 0;
	
	public void freeze(int time) {
		freezeTime = NumberUtils.max(time, freezeTime, 0);
		if (freezeTime <= 0) {
			cancelFreeze();
			return;
		}
		
		if (!isFreezable()) return;
		if (isFrozen()) return;
		
		givePotionEffect(PotionEffectType.LEVITATION, freezeTime, 0, true, false, true);
		givePotionEffect(PotionEffectType.GLOWING, freezeTime, 1, true, false, true);
		givePotionEffect(PotionEffectType.BLINDNESS, freezeTime, 1, true, false, true);

		if (mob != null) {
			Disguise dis = mob.getDisguise();
			if (dis != null) {
				dis.getWatcher().setGlowing(true);
			}
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
		if (mob instanceof FloatyMob) {
			((FloatyMob) mob).resetFloatiness();
		} else {
			player.removePotionEffect(PotionEffectType.LEVITATION);
		}

		player.setFlySpeed(0.1f);
		player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
		
		if (isAlive()) {
			player.setFlying(false);
			player.setAllowFlight(false);
			
			Disguise dis = mob.getDisguise();
			if (dis != null) {
				dis.getWatcher().setGlowing(false);
			}
		}
			
		teleportTo(freezeLocation, true);
		
		freezeLocation = null;
	}
	
	public void resetFrozen() {
		if (isFrozen()) {
			teleportTo(freezeLocation, true);
		}
	}
	
	public boolean isFrozen() {
		return (freezeLocation != null);
	}
	
	private boolean isFreezable() {
		return !mob.hasSpawnProtection();
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
		
		if (!isFrozen()) {
			super.setVelocity(vel);
		}
	}
}
