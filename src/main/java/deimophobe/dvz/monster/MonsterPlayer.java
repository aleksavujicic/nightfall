package deimophobe.dvz.monster;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import deimophobe.dvz.*;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.menu.SessionData;
import deimophobe.dvz.monster.mob.Bopen;
import deimophobe.dvz.monster.mob.Mob;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.monster.upgrade.MobUpgrades;
import deimophobe.dvz.shrine.ShrineManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
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
		player.sendMessage("You are monster now. Deimo make this cool.");
		
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
		player.setFoodLevel(20);
		player.setSaturation(20);
		
		updateSeppuku();
		
		if (mob != null) {
			mob.update(quartSec, halfSec, sec, doubleSec, quadSec);
			
			if (seppukuCD > 0) {
				player.setExp(1 - (float)seppukuCD/MAX_SEPPUKU_CD);
			} else if (mob != null) {// Update could kill mob so need to do another null check
				player.setExp(mob.getCooldown());
			}
		}
		
		if (sec && isAlive())
			gainXP(10);
	}
	
	
	// ------ SPAWN AND DEATH ------
	public boolean isAlive() {
		return (player.getGameMode() == GameMode.SURVIVAL && mob != null);
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
		if (mob != null)
			mob.onDeath();
		
		cancelFreeze();
		
		Disguise disguise = DisguiseAPI.getDisguise(player);
		if (disguise != null) {
			EntityType entityType = disguise.getType().getEntityType();
			if (entityType.isAlive() && entityType != EntityType.PLAYER) {
				LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), entityType);
				entity.teleport(player);
				entity.setVelocity(player.getVelocity());
				entity.setCustomName(disguise.getWatcher().getCustomName());
				entity.getEquipment().setArmorContents(player.getInventory().getArmorContents());
				entity.getEquipment().setItemInMainHand(getHeldItem());
				entity.damage(10000);
			}
		}
		DisguiseAPI.undisguiseToAll(player);
		
		if (isAlive()) {
			ActionBarAPI.sendActionBarToAllPlayers(generateDeathMsg(), 60);
			Bukkit.broadcastMessage(generateDeathMsg());
			player.playSound(player.getLocation(), "proc", 1f, 0.7f);
		}
		
		setTitle(ChatColor.GRAY, null, false);
		
		mob = null; // TODO don't set to null?
		player.setGameMode(GameMode.SPECTATOR);
		clearInventory();
		clearEffects();
	}
	
	public void spawnAs(MobType type) {
		spawnMob(type.createMob(this));
	}
	
	public void spawnMob(Mob mob) {
		this.mob = mob;
		mob.spawn();
		player.setAllowFlight(false);
		player.getInventory().setItem(9, seppuku);
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
	
	private void updateXPDisplay() {
		player.setLevel(experience);
		Game.getGame().setMana(player, experience);
	}
	
	
	
	// ------ SPAWN/UPGRADE MENUS ------
	private final Map<MobType, MobUpgrades> upgrades = new HashMap<>();
	public void showMobMenu() {
		MonsterManager.getManager().showMobMenu(this);
	}
	
	public MobUpgrades getUpgrades(MobType type) {
		if (upgrades.containsKey(type))
			return upgrades.get(type);
		else {
			MobUpgrades emptyUpgrades = new MobUpgrades();
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
				((Dwarf) gamePlayer).damageArmour(mob.getArmourShred());
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
		if (player.hasPotionEffect(PotionEffectType.LUCK)) {
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
		
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed(0);
		player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
		player.setVelocity(new Vector(0,0,0));
		
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
		player.removePotionEffect(PotionEffectType.LEVITATION);
		player.removePotionEffect(PotionEffectType.GLOWING);
		
		player.setFlySpeed(0.1f);
		player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
		
		if (isAlive()) {
			player.setFlying(false);
			
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
		if (player.hasPotionEffect(PotionEffectType.LUCK))
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
}
