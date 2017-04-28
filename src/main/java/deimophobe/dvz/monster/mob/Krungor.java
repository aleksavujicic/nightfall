package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 26/01/17.
 */
class Krungor extends AbstractTypedMob {
	
	@Override protected MobType getType() {return MobType.KRUNGOR;}
	
	private int cooldown = 0;
	private final static int MAX_CD = 30*20;
	
	Krungor(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		return damage/2;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float)cooldown/MAX_CD;
	}
	
	private static final int MAX_BLOCKS = 6;
	private static final double RANGE = 7;
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && cooldown == 0 && isPlayerHoldingWeapon()) {
			/*
			
			Location loc = monster.getLocation();
			loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1,0, 0, 0, 0);
			*/
			
			//Set<Launcher> launchers = new HashSet<>();
			for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
				double distance = monster.distanceTo(dwarf);
				if (distance <= RANGE) {
					new Launcher(dwarf, distance).launch();
				}
			}
			
			
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
						//world.spawnParticle(Particle.DRAGON_BREATH, particleLoc, 3, 0.2, 0.2, 0.2, 0);
						
					}
					
					if (radius % 2 == 0)
						world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 1, 1.5f + (float)radius/20);
					
					radius++;
					if (radius > MAX_BLOCKS) this.cancel();
				}
			}.runTaskTimer(Game.getGame().getPlugin(), 0, 2);
			
			
			cooldown = MAX_CD;
		}
	}
	
	private static class Launcher {
		private final Dwarf dwarf;
		private final double range;
		
		private Launcher(Dwarf dwarf, double range) {
			this.dwarf = dwarf;
			this.range = range;
		}
		
		private void launch() {
			dwarf.givePotionEffect(PotionEffectType.GLOWING, 30, 1, true, true, false);
			World world = dwarf.getLocation().getWorld();
			
			new BukkitRunnable() {
				private int lifetime = 15;
				@Override
				public void run() {
					Vector vel = dwarf.getPlayer().getVelocity().clone();
					vel.setY(10);
					dwarf.getPlayer().setVelocity(vel);
					
					world.spawnParticle(Particle.END_ROD, dwarf.getLocation(), 0, 0, -1, 0, 1);
					
					lifetime--;
					if (lifetime == 0)
						this.cancel();
				}
			}.runTaskTimer(Game.getGame().getPlugin(), 0, 2);
		}
	}
	
}
