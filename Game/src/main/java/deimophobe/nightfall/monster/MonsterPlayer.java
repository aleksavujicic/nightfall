package deimophobe.nightfall.monster;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.WhoEntry;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.menu.SessionData;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.event.MobSpawnEvent;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Sidebar;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.mob.Bopen;
import deimophobe.nightfall.monster.mob.FloatyMob;
import deimophobe.nightfall.monster.mob.Mob;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.upgrades.MonsterUpgrades;
import deimophobe.nightfall.util.AFKChecker;
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
	private final AFKChecker afkChecker;
	
	private Mob mob;
	public Mob getMob() { return mob; }
	
	public MonsterPlayer(Player player, boolean spectator) {
		super(player);
		this.afkChecker = new AFKChecker(this, 30);
		addUpdateable(afkChecker);
		
		setTitle(ChatColor.GRAY, null, false);
		if (spectator) {
			kill(true);
		}
		
		int xpCount = MonsterManager.getManager().getCurrentXPCount();
		forceGiveExperience(xpCount);
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
		Sidebar.getGameSidebar().hideEntry(Sidebar.Entry.MONSTER_EXPERIENCE, player);
		Sidebar.getGameSidebar().hideEntry(Sidebar.Entry.DOOM, player);
		super.onRemove();
	}
	
	public void update() {
		super.update();
		updateSeppuku();
		updateUnglower();
		
		if (mob != null) {
			mob.update();
			
			if (seppukuCD > 0) {
				setExp((float)seppukuCD/MAX_SEPPUKU_CD);
			} else if (mob != null) {// Update could kill mob so need to do another null check
				setExp(mob.getCooldown());
			}
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
	
	@Override
	public boolean isBowInstaKillable() {
		return false;
	}
	
	@Override
	public WhoEntry getWhoEntry() {
		WhoEntry entry = super.getWhoEntry();
		entry.setType(WhoEntry.Type.MONSTER);
		return entry;
	}
	
	@Override
	public void giveCompass() {
		if (mob == null) return;
		
		mob.giveCompass();
	}
	
	public AFKChecker getAfkChecker() {
		return afkChecker;
	}
	
	// ------ SPAWN AND DEATH ------
	public boolean isMobAlive() {
		return (mob != null);
	}
	
	public void kill(boolean silent) {
		if (!silent && isMobAlive()) {
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
		clearWarning();
		removeAllPoisons();
		removeFire();
		player.setRemainingAir(300);
		mobMenuShower.reset();
		cancelSeppuku();
		removeAllShields();
		resetLastMainDamage();
		
		player.setAllowFlight(true);
		player.setGameMode(GameMode.SPECTATOR);
	}
	
	public void spawnPrimaryMob(SpawnMethod spawnMethod) {
		spawnMob(upgrades.createPrimaryMob(), spawnMethod);
	}
	
	public boolean spawnMob(MobCreator<?> type) {
		return spawnMob(type, SpawnMethod.SPAWN);
	}
	
	public boolean spawnMob(MobCreator<?> type, SpawnMethod spawnMethod) {
		try {
			Mob mob = type.createMob(this);
			return spawnMob(mob, spawnMethod);
		} catch (Exception e) {
			sendMessage(ChatColor.RED + "Failed to create mob. (Internal server error). Please notify a dev about this.");
			NightfallPlugin.logger().severe("Failed to create mob of type " + type.getName() + " for " + getName());
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
			logger.info("Spawning " + getName() + " as mob " + mob.getType() + " (via " + spawnMethod + ")");
			
			return true;
		} catch (Exception e) {
			sendMessage(ChatColor.RED + "Failed to spawn mob. (Internal server error). Please notify a dev about this.");
			logger.severe("Failed to spawn " + getName() + " as mob " + mob.getType() + " with method " + spawnMethod);
			logger.severe(e.getMessage());
			e.printStackTrace();
			kill(true);
			return false;
		}
	}
	
	public boolean isMobType(MobType type) {
		return mob != null && mob.getType() == type;
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
	private int experienceRate = 10;
	private static final int MAX_EXPERIENCE = 10000;
	
	private final MonsterUpgrades upgrades = new MonsterUpgrades(this);
	
	public MonsterUpgrades getUpgrades() {
		return upgrades;
	}
	
	public void forceGiveExperience(int amount) {
		experience += amount;
		updateExperienceDisplay();
	}
	
	public void giveExperience(int amount) {
		// If already above max, let it stay there
		int currentMax = Math.max(experience, MAX_EXPERIENCE);
		// Add amount, without going over currentMax
		experience = Math.min(currentMax, experience + amount);
		updateExperienceDisplay();
	}
	
	public boolean useExperience(int cost) {
		return useExperience(cost, true);
	}
	
	public boolean useExperience(int cost, boolean increaseAmountSpent) {
		if (experience < cost) {
			return false;
		} else {
			experience -= cost;
			if (increaseAmountSpent) upgrades.increaseAmountSpent(cost);
			
			updateExperienceDisplay();
			return true;
		}
	}
	
	public void setExperienceRate(int rate) {
		experienceRate = rate;
	}
	
	public int getExperienceRate() {
		return experienceRate;
	}
	
	public int getExperience() {
		return experience;
	}
	
	public boolean hasExperience(int amount) {
		return experience >= amount;
	}
	

	private void updateExperienceDisplay() {
		player.setLevel(experience);
		game.getSidebar().setEntryValue(Sidebar.Entry.MONSTER_EXPERIENCE, player, experience);
	}
	
	public void sendInsufficientExperienceMessage(int requiredExperience) {
		player.sendMessage(
				ChatColor.RED + "Not enough exp! " + "You have "
				+ ChatColor.LIGHT_PURPLE + experience
				+ ChatColor.RED + " exp (need "
				+ ChatColor.YELLOW + requiredExperience
				+ ChatColor.RED + ")."
		);
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
		afkChecker.resetAFK();
		
		if (isFrozen()) return;
		
		if (mob != null) {
			mob.onShift(sneaking);
		}
	}
	
	@Override
	public void onSwim(boolean swimming) {
	
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
		
		afkChecker.resetAFK();
		
		if (!isMobAlive()) {
			mobMenuShower.tryUse();
			if (player.getGameMode() == GameMode.SPECTATOR) {
				player.setSpectatorTarget(null);
			}
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
		shieldDamage(damage);
		
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
			kill(true);
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (isFrozen()) return null;
		
		if (mob != null) {
			return mob.onBowFire(arrow, force);
		}
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile projectile, Block hitBlock, BlockFace hitFace, GameEntity<?> hitEntity) {
		if (mob != null) {
			mob.onProjectileLand(projectile, hitBlock, hitFace);
		}
	}
	
	@Override
	public Location getRespawnLocation() {
		return GameMap.getCurrentMap().getCurrentMobspawn();
	}
	
	@Override
	public void onRespawn() {
		super.onRespawn();
		kill(true);
		initialiseWarnings();
	}
	
	// ------ GLOWING ------
	
	private int unglower = 0;
	
	private void updateUnglower() {
		if (unglower == 0) return;
		unglower--;
		if (unglower == 0) setGlow(false);
	}
	
	@Override
	public boolean givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean showAbove, boolean colourBlue, boolean force) {
		boolean success = super.givePotionEffect(type, duration, amplifier, showAbove, colourBlue, force);
		if (!success) return false;
		
		if (type == PotionEffectType.GLOWING) {
			setGlow(true);
			unglower = Math.max(unglower, duration);
		}
		
		return true;
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {
		super.removePotionEffect(type);
		unglower = 0;
		setGlow(false);
	}
	
	private void setGlow(boolean glowing) {
		if (mob != null) {
			mob.changeDisguiseWatcher(dw -> dw.setGlowing(glowing));
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
		
		if (isMobAlive()) {
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
