package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.RepeatingCooldown;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 26/01/17.
 */
class Krungor extends AbstractMob {
	
	@Display @Update private final ComplexCooldown launchCD = new ComplexCooldown(30*20, this::launch);
	         @Update private final ComplexCooldown buffer = new RepeatingCooldown(4*20, this::buffNearbyMobs);
	
	Krungor(MonsterPlayer monster) {
		super(monster, MobType.KRUNGOR);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getReceiver() instanceof Hero)
			damage.getMulitPartDamage().addBoost(20);
	}

	
	private static final int MAX_BLOCKS = 6;
	private static final double RANGE = 7;
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
			launchCD.tryUse();
		}
	}
	
	private void launch() {
		Set<Dwarf> launched = new HashSet<>();
		for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
			double distance = monster.distanceTo(dwarf);
			if (distance <= RANGE) {
				dwarf.givePotionEffect(PotionEffectType.LEVITATION, 30, 100, true, false, true);
				launched.add(dwarf);
			}
		}
		
		// Particles from launched dwarves
		new BukkitRunnable() {
			private int lifetime = 15;
			private World world = GameMap.getCurrentMap().getWorld();
			@Override
			public void run() {
				for (Dwarf dwarf : launched) {
					Player player = dwarf.getPlayer();
					if (player != null)
						world.spawnParticle(Particle.END_ROD, player.getLocation(), 0, 0, -1, 0, 1);
				}
				
				lifetime--;
				if (lifetime == 0)
					this.cancel();
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 2);
		
		// Play effect around krungor
		monster.getLocation().getWorld().playSound(monster.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.7f);
		new BukkitRunnable() {
			Location center = monster.getLocation();
			World world = center.getWorld();
			int radius = 1;
			
			@Override public void run() {
				int numParticles = radius*6;
				
				for (int i=0; i<numParticles; i++) {
					double theta = i * (Math.PI/3) * 1/radius;
					Location particleLoc = center.clone().add(radius*Math.cos(theta), 0, radius*Math.sin(theta));
					
					world.spawnParticle(Particle.END_ROD, particleLoc, 0, 0, 0.05, 0, 1);
					world.spawnParticle(Particle.END_ROD, particleLoc, 0, 0, 2, 0, 1);
					world.spawnParticle(Particle.LAVA, particleLoc, 0, 0, 2, 0, 1);
					
					particleLoc.add(0, 1, 0);
					world.spawnParticle(Particle.SMOKE_LARGE, particleLoc, 2, 0.3, 0.15, 0.3, 0);
					
				}
				
				if (radius % 2 == 0)
					world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 1, 1.5f + (float)radius/20);
				
				radius++;
				if (radius > MAX_BLOCKS) this.cancel();
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 2);
	}
	
	private void buffNearbyMobs() {
		for (MonsterPlayer mp : MonsterManager.getManager().getAlivePlayerMobs()) {
			if (mp == monster) continue;
			if (monster.distanceTo(mp) <= 10) {
				mp.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, 10*20, 2, true, false, true);
			}
		}
	}
}
