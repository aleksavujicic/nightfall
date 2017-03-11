package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by Deimophobe on 26/01/17.
 */
class Krungor extends Mob {
	private int cooldown = 0;
	private final static int MAX_CD = 200;
	
	Krungor(MonsterPlayer monster) {
		super(monster, MobType.KRUNGOR);
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		return damage/2;
	}
	
	@Override
	public void update() {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float)cooldown/MAX_CD;
	}
	
	private static final double RANGE = 5;
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && cooldown == 0 && isPlayerHoldingItem(0)) {
			Set<Dwarf> launchDwarves = new HashSet<>();
			for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
				if (monster.distanceTo(dwarf) <= RANGE) {
					launchDwarves.add(dwarf);
				}
			}
			new BukkitRunnable() {
				private int lifetime = 20;
				@Override
				public void run() {
					for (Dwarf dwarf : launchDwarves) {
						Vector vel = dwarf.getPlayer().getVelocity().clone();
						vel.setY(10);
						dwarf.getPlayer().setVelocity(vel);
					}
					lifetime--;
					if (lifetime == 0)
						this.cancel();
				}
			}.runTaskTimer(Game.getGame().getPlugin(), 0, 1);
			
			Location loc = monster.getLocation();
			loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1,0, 0, 0, 0);
			
			cooldown = MAX_CD;
		}
	}
}
