package deimophobe.dvz.monster;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import deimophobe.dvz.*;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.mob.Mob;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.monster.spawnmenu.SpawnManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
		Disguise disguise = DisguiseAPI.getDisguise(player);
		if (disguise != null) {
			EntityType entityType = disguise.getType().getEntityType();
			if (entityType.isAlive()) {
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
			ActionBarAPI.sendActionBarToAllPlayers(generateDeathMsg(), 40);
			player.playSound(player.getLocation(), "proc", 1f, 0.7f);
		}
		
		setTitle(ChatColor.GRAY + player.getName() + ChatColor.RESET);
		
		mob = null; // TODO don't set to null?
		player.setGameMode(GameMode.SPECTATOR);
		clearInventory();
		clearEffects();
	}
	
	public void showMobMenu() {
		if (Game.getGame().getPhase().canMobSpawn())
			player.openInventory(SpawnManager.getManager().getMobMenu());
	}
	
	public void spawnAs(MobType type) {
		mob = Mob.createAndSpawnMob(this, type);
		
		//teleportTo(Game.getGame().getCurrentMobspawn());
		//player.setGameMode(GameMode.SURVIVAL);
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
	
	private static final EntityDamageEvent.DamageCause[] IMMUNE_CAUSES = {
			EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
			EntityDamageEvent.DamageCause.STARVATION,
			EntityDamageEvent.DamageCause.DROWNING,
			EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
			EntityDamageEvent.DamageCause.FALL,
			EntityDamageEvent.DamageCause.SUFFOCATION,
			EntityDamageEvent.DamageCause.LAVA,
			EntityDamageEvent.DamageCause.HOT_FLOOR,
			EntityDamageEvent.DamageCause.FIRE_TICK,
			EntityDamageEvent.DamageCause.FIRE,
	};
	@Override
	public double onNaturalHit(EntityDamageEvent.DamageCause cause, double baseDmg) {
		// Ignore certain damages (see above)
		for (EntityDamageEvent.DamageCause immunity : IMMUNE_CAUSES) {
			if (cause == immunity) {
				return -1;
			}
		}
		// x4 dagger poison damage
		if (cause == EntityDamageEvent.DamageCause.WITHER) {
			return baseDmg*4;
		}
		return baseDmg;
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (mob != null)
			return mob.onBowFire(arrow, force);
		return arrow;
	}
	
	@Override
	public void onArrowLand(Arrow arrow, Block hitBlock) {
		if (mob != null)
			mob.onArrowLand(arrow, hitBlock);
	}
}
