package deimophobe.nightfall.map;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.map.region.Region;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 21/01/17.
 */
public class Shrine {
	protected final GameMap map;
	
	private final String name;
	private final String fallName;
	
	private final Location mobSpawn;
	
	private final Region mobProtection;
	private final Region shrineProtection;
	private final Region shrineRegion;
	
	private final Location shrineCenter;
	
	private int shrinePower;
	private final int maxShrinePower;
	
	private final double goldWeight;
	private final int shrineNum;
	
	private final int swapoverDelay;

	public String getName() {
		return name;
	}
	public Location getMobSpawn() {
		return mobSpawn;
	}
	
	public Region getMobProtection() {
		return mobProtection;
	}
	public Region getShrineProtection() {
		return shrineProtection;
	}
	public Region getShrineRegion() {
		return shrineRegion;
	}
	
	public Location getShrineCenter() {
		return shrineCenter;
	}
	
	public double getGoldWeight() { return goldWeight; }
	
	public Shrine(GameMap map, ConfigurationSection section, int shrineNum) throws InvalidMapConfigException {
		this.map = map;
		
		if (!section.contains("name"))
			throw new InvalidMapConfigException("Shrine must have a name", section, "name");
		this.name = section.getString("name");
		
		if (!section.contains("fallname")) {
			this.fallName = "THE " + name.toUpperCase();
			Bukkit.getLogger().warning("No fallname for shrine '" + name + "' specified.");
		} else {
			this.fallName = section.getString("fallname");
		}
		
		
		this.mobSpawn = map.getLocation(section, "mobspawn");
		
		this.mobProtection = Region.createRegion(map, section.getConfigurationSection("mobprot"));
		this.shrineProtection = Region.createRegion(map, section.getConfigurationSection("shrineprot"));
		this.shrineRegion = Region.createRegion(map, section.getConfigurationSection("shrine"));
		
		this.shrineCenter = map.getLocation(section, "shrine.center");
		
		
		this.maxShrinePower = section.getInt("power");
		shrinePower = maxShrinePower;
		this.goldWeight = section.getDouble("goldweight");
		
		this.shrineNum = shrineNum;
		
		this.swapoverDelay = section.getInt("delay", 15);
		
		map.addUnbreakableRegion(shrineRegion);
		
		// TODO sanitise inputs
	}
	
	public int getSwapoverDelay() {
		return swapoverDelay;
	}
	
	public void onActive() {
		Game.getGame().setShrineBarPower(1);
		Game.getGame().setShrineBarName(name, shrineNum);
	}
	
	public void update() {
		int mobsOnShrine = 0;
		int dwarvesOnShrine = 0;
		for (MonsterPlayer monster : MonsterManager.getManager().getAlivePlayerMobs()) {
			if (shrineRegion.containsPlayer(monster)) {
				mobsOnShrine++;
			} else if (shrineProtection.containsPlayer(monster)) {
				if (!monster.getMob().isShrineImmune()) {
					monster.doDamage(null, CustomDamageType.SHRINE_PROTECTION, 10000, true, true);
					Location loc = monster.getLocation();
					loc.getWorld().strikeLightningEffect(loc);
				}
			}
			
		}
		for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
			if (shrineRegion.containsPlayer(dwarf)) {
				dwarvesOnShrine++;
				if (!dwarf.getArmour().isAtMax())
					if (map.tryUseGold(2)) {
						dwarf.getArmour().repair(Math.min(shrineNum * 2 + 8, 16)); // shrineNum starts at 1
					}
					else {
						map.tryUseGold(1);
						int repairAmount = Math.max(Math.min(shrineNum * 2 - 1, 5), 0);
						dwarf.getArmour().repair(repairAmount);
					}
			}
		}
		
		//map.stealGold(Math.max(3*mobsOnShrine - 3*dwarvesOnShrine, 0));
		doUpdateDamage(mobsOnShrine, dwarvesOnShrine);
	}
	
	private void doUpdateDamage(int mobNum, int dwarfNum) {
		int damage = 0;
		int recovery = 0;
		// Making shrines a bit stronger
		if (shrineNum == map.getNumShrines()) {
			// Final shrine should not fall until most dwarves are dead
			dwarfNum *= 3;
		}
		if (mobNum == 0) {
			// Regen when no mobs around, first shrine has slower regen
			if (shrineNum == 1) {
				if (shrinePower < (maxShrinePower / 4)) {
					recovery = dwarfNum * maxShrinePower / 50;
				}
				else {
					recovery = dwarfNum * maxShrinePower / 1000;
				}
			}
			else {
				recovery = dwarfNum * maxShrinePower / 30;
			}
		}
		else {
			damage += mobNum * maxShrinePower / 50;
			damage -= dwarfNum * maxShrinePower / 30;
			if (damage < (maxShrinePower / 200)) damage = (maxShrinePower / 200);
		}
		// 2 times or above as much dwarves will prevent shrine from losing power
		if (damage > 0 && (dwarfNum >= (mobNum * 2))) {
			damage = 0;
		}
		// At 500 gold shrine regen drops off linearly until at 200 gold to 40% regen speed, also recovery is always nonnegative
		if (recovery > 0) {
			recovery = recovery * Math.max(200, Math.min(500, map.getGold())) / 500;
		}
		else
		{
			recovery = 0;
		}
		// Shrine damage and recovery are capped at 20%
		damage = Math.min((maxShrinePower / 5), damage);
		recovery = Math.min((maxShrinePower / 5), recovery);
		
		if (map.hasGold()) {
			int recovered = recoverShrine(recovery);
			map.stealGold(recovered/20);
		}
		else {
			if (dwarfNum == 0) {
				damage += maxShrinePower / 50;
			}
			else {
				recoverShrine(recovery/2);
			}
		}

		damageShrine(damage);

		// Shrine Power capped at max shrine power
		if (shrinePower > maxShrinePower){
			shrinePower = maxShrinePower;
		}
	}

	public void damageShrine(int damage) {
		shrinePower -= damage;
		
		if (shrinePower <= 0)
			killShrine();
		else
			updateShrineHealth();
	}
	
	public int recoverShrine(int recovery) {
		int newShrinePower = Math.min(shrinePower + recovery, maxShrinePower);
		int recovered = newShrinePower - shrinePower;
		shrinePower = newShrinePower;
		updateShrineHealth();
		return recovered;
	}
	
	private void updateShrineHealth() {
		Game.getGame().setShrineBarPower((double) shrinePower/maxShrinePower);
	}
	
	
	protected void killShrine() {
		if (MapManager.getManager().isEnabled())
			explodeShrine();
		Bukkit.broadcastMessage(ChatColor.GOLD + "==================================================");
		Bukkit.broadcastMessage(ChatColor.YELLOW + fallName + " HAS FALLEN!");
		Bukkit.broadcastMessage(ChatColor.GOLD + "==================================================");
		AIManager.getManager().removeAllAIs();
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			dwarf.giveProc(ProcType.SHRINE_FALL);
			dwarf.getArmour().repair(1000);
			dwarf.regenMana(200);
		}
		for (MonsterPlayer monster : MonsterManager.getManager().getAlivePlayerMobs()) {
			monster.givePotionEffect(PotionEffectType.SLOW, 180, 3, true, false, true);
			monster.givePotionEffect(PotionEffectType.CONFUSION, 180, 1, true, false, true);
		}
		for (MonsterPlayer monster : MonsterManager.getManager().getGamePlayers()) {
			monster.forceGainXP(1000);
		}
		MonsterManager.getManager().giveFutureXP(1000);
		
		map.changeShrine();
	}
	
	
	protected void explodeShrine() {
		World world = shrineCenter.getWorld();
		
		world.spawnParticle(Particle.EXPLOSION_LARGE, shrineCenter, 4, 5, 2, 5);
		world.playSound(shrineCenter, Sound.ENTITY_GENERIC_EXPLODE, 3f, 0.6f);
		world.playSound(shrineCenter, "horn", 100f, 1f);
		
		if (!MapManager.getManager().isEnabled()) return;
		
		Set<Block> blocks = new HashSet<>();
		
		int radius = 3;
		
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					Block block = shrineCenter.clone().add(x,y,z).getBlock();
					
					if (block.getType() == Material.ENDER_PORTAL_FRAME) {
						blocks.add(block);
						continue;
					}
					
					if (block.getType() == Material.BEACON) {
						block.setType(Material.AIR);
						continue;
					}
					
					if (block.getType() == Material.BEDROCK) {
						continue;
					}
					
					if (Math.random() <= 0.65) {
						if (Math.random() <= 0.4) {
							blocks.add(block);
						} else {
							block.setType(Material.AIR);
						}
					}
				}
			}
		}
		
		for (Block block : blocks) {
			FallingBlock falling = world.spawnFallingBlock(block.getLocation(), block.getState().getData());
			falling.setDropItem(false);
			
			double vx = 1*Math.random() - 0.5;
			double vy = 1*Math.random() + 1;
			double vz = 1*Math.random() - 0.5;
			
			falling.setVelocity(new Vector(vx, vy, vz));
			
			block.setType(Material.AIR);
		}
	}
}
