package deimophobe.nightfall.dwarf.kit.spell;

import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.util.Hitscan;
import deimophobe.nightfall.util.HitscanProjectile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 14/06/18.
 */
class MeteorBlast implements Spell {
	@Override
	public String getName() {
		return ChatColor.RED + "Meteor Strike";
	}
	
	@Override
	public int getCost() {
		return 6;
	}
	
	@Override
	public int getCooldown() {
		return 40;
	}
	
	private static final double MAX_RANGE = 30;
	private static final double THICKNESS = 1.25;
	private static final double PARTICLE_OFFSET = THICKNESS / 10;
	
	private static final Consumer<Location> PARTICLE_PLACER =
			(location) -> location.getWorld().spawnParticle(Particle.SPELL_WITCH, location, 3, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0.05);
	
	@Override
	public void castSpell(Dwarf dwarf) {
		final Consumer<MonsterEntity> mobDamager = dwarf.new GameEntityDamager<MonsterEntity>(GameDamageType.TEMPORARY, 10);
		dwarf.addUpdateable(new LifetimeExpireable(20) {
			@Override
			public void update() {
				if (everyNTicks(5)) {
					Hitscan hitscan = new Hitscan(THICKNESS, PARTICLE_PLACER, null, mobDamager);
					HitscanProjectile.fireProjectile(dwarf, 1.5, MAX_RANGE, hitscan);
				}
				super.update();
			}
		});
	}
}
