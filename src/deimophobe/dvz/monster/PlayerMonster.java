package deimophobe.dvz.monster;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.PlayerOrAI;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.mob.Mob;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class PlayerMonster extends GamePlayer {
	
	private Mob mob;
	
	public Mob getMob() { return mob; }
	
	public PlayerMonster(Player player) {
		super(player);
		player.sendMessage("You are monster now. Deimo make this cool.");
		
		mob = null;
	}
	
	public void update() {
		player.setFoodLevel(20);
		player.setSaturation(20);
		
		if (mob != null) {
			mob.update();
			
			player.setExp(mob.getCooldown());
		}
	}
	
	public boolean isAlive() {
		return (player.getGameMode() == GameMode.SURVIVAL && mob != null);
	}
	
	public void kill() {
		if (!isAlive()) {
			
			mob = null; // TODO don't set to null?
			player.setGameMode(GameMode.SPECTATOR);
			player.setDisplayName(ChatColor.GRAY + player.getName() + ChatColor.RESET);
			
			showMobMenu();
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					ActionBarAPI.sendActionBarToAllPlayers(generateDeathMsg(), 60);
					player.setDisplayName(ChatColor.GRAY + player.getName() + ChatColor.RESET);
					
				}
			}.runTaskLater(Game.getGame().getPlugin(), 1);
			
			Disguise disguise = DisguiseAPI.getDisguise(player);
			EntityType entityType = disguise.getType().getEntityType();
			if (entityType.isAlive()) {
				LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), entityType);
				entity.teleport(player);
				entity.setVelocity(player.getVelocity());
				entity.setCustomName(disguise.getWatcher().getCustomName());
				entity.damage(10000);
			}
			
			player.playSound(player.getLocation(), "proc", 1f, 0.7f);
			
			mob = null;
			player.setGameMode(GameMode.SPECTATOR);
			
			showMobMenu();
		}
	}
	
	public void showMobMenu() {
		if (Game.getGame().getPhase().canMobSpawn())
			player.openInventory(MobManager.getManager().getMobMenu());
	}
	
	public void spawnAs(Mob mob) {
		this.mob = mob.clone(this);
		this.mob.spawn();
		
		teleportTo(Game.getGame().getCurrentMobspawn());
		player.setGameMode(GameMode.SURVIVAL);
		player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 160, 0), true);
	}
	
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
	public double onHit(PlayerOrAI gamePlayer, DamageType type, double damage) {
		if (mob != null) {
			if (gamePlayer instanceof Dwarf) {
				((Dwarf) gamePlayer).damageArmour(mob.getArmourShred());
				return mob.onHit((Dwarf) gamePlayer, type, damage);
			} else {
				Bukkit.getLogger().warning("PlayerOrAI in onHit should be a Dwarf");
				return damage;
			}
		} else {
			return damage;
		}
	}
	
	@Override
	public double onGotHit(PlayerOrAI gamePlayer, DamageType type, double damage) {
		// Spawn protection
		if (player.hasPotionEffect(PotionEffectType.LUCK)) {
			return -1;
		}
		
		if (type == DamageType.BOW) {
			damage = damage * (1 - mob.getArrowRes());
		}
		
		if (mob != null) {
			if (gamePlayer instanceof Dwarf) {
				return mob.onGotHit((Dwarf) gamePlayer, type, damage);
			} else {
				Bukkit.getLogger().warning("PlayerOrAI in onGotHit should be a Dwarf");
				return damage;
			}
		} else {
			return damage;
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		return null;
	}
	
	@Override
	public void onArrowLand(Arrow arrow, Block hitBlock) {}
}
