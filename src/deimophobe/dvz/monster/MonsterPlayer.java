package deimophobe.dvz.monster;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import deimophobe.dvz.*;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.mob.Mob;
import deimophobe.dvz.monster.mob.MobType;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class MonsterPlayer extends GamePlayer {
	
	private Mob mob;
	
	public Mob getMob() { return mob; }
	
	public MonsterPlayer(Player player) {
		super(player);
		player.sendMessage("You are monster now. Deimo make this cool.");
		
		mob = null;
		
		upgrades.put(MobType.ZOMBIE, new HashSet<>());
		
		killLater();
	}
	
	@Override
	public void remove() {
		super.remove();
		DisguiseAPI.undisguiseToAll(player);
	}
	
	@Override
	public void goOnline(Player player) {
		super.goOnline(player);
		
		teleportTo(Game.getGame().getCurrentMobspawn());
		givePotionEffect(PotionEffectType.SLOW, 70, 20, true, false, true);
		givePotionEffect(PotionEffectType.JUMP, 70, -20, true, false, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				customDamage(null, DamageType.RELOG, 10000);
			}
		}.runTaskLater(Game.getGame().getPlugin(), 70);
	}
	
	public void update() {
		player.setFoodLevel(20);
		player.setSaturation(20);
		
		if (mob != null) {
			mob.update();
			
			// Update could kill mob
			if (mob != null)
				player.setExp(mob.getCooldown());
		} else {
			player.setFlySpeed(0.1f);
		}
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
		mob = Mob.createAndSpawnMob(this, type);
		
		//teleportTo(Game.getGame().getCurrentMobspawn());
		//player.setGameMode(GameMode.SURVIVAL);
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
	
	public void updateXP() {
		if (isAlive())
			gainXP(10);
	}
	
	private void updateXPDisplay() {
		player.setLevel(experience);
	}
	
	
	
	// ------ SPAWN/UPGRADE MENUS ------
	private final Map<MobType, Set<String>> upgrades = new HashMap<>();
	public void showMobMenu() {
		MonsterManager.getManager().showMobMenu(this);
	}
	
	public void addUpgrade(MobType type, String upgrade) {
		upgrades.get(type).add(upgrade);
	}
	
	public boolean hasUpgrade(MobType type, String upgrade) {
		return upgrades.get(type).contains(upgrade);
	}
	
	
	
	
	// ------ EVENT METHODS ------
	@Override
	public void onShift(boolean sneaking) {
		if (mob != null)
			mob.onShift(sneaking);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (mob != null)
			mob.onUse(action, clickedBlock);
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
		
		if (type.isMobImmune())
			return -1;
		
		if (type.isPoison())
			damage *= 4;
		
		if (type.isRanged())
			damage *= (1 - mob.getArrowRes());
		
		
		if (mob != null) {
			if (gameEntity instanceof Dwarf) {
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
}
