package deimophobe.nightfall.dwarf.kit.spell;

import deimophobe.nightfall.common.items.CustomItem;
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
class MagicMissile implements Spell {
	@Override public String getName() { return ChatColor.DARK_PURPLE + "Magic Missile"; }
	@Override public int getCost() { return 2; }
	@Override public int getCooldown() { return 50;	}
	
	private static final double MAX_RANGE = 30;
	private static final double THICKNESS = 1.25;
	private static final double PARTICLE_OFFSET = THICKNESS / 10;
	
	private static final Consumer<Location> PARTICLE_PLACER = location -> {
		location.getWorld().spawnParticle(Particle.SPELL_WITCH, location, 3, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0.03);
		location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, location, 1, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0);
	};
	
	private final CustomItem spellbook;
	
	MagicMissile(CustomItem spellbook) {
		this.spellbook = spellbook;
	}
	
	@Override
	public void castSpell(Dwarf dwarf) {
		dwarf.addUpdateable(new LifetimeExpireable(30) {
			@Override
			public void update() {
				if (everyNTicks(10)) {
					Consumer<MonsterEntity> mobDamager = dwarf.new GameEntityDamager<MonsterEntity>(
							GameDamageType.MAGIC_MISSILE, 25, true, damage -> {
								damage.setNoDamageTicks(3);
								if (damage.getReceiver().isBowInstaKillable()) damage.instaKill();
							}
					);
					Hitscan hitscan = new Hitscan(THICKNESS, PARTICLE_PLACER, null, mobDamager);
					HitscanProjectile.fireProjectile(dwarf, 1.5, MAX_RANGE, hitscan, true);
					dwarf.playSound("entity.elder_guardian.curse", 1f, 2f, true);
				}
				super.update();
			}
			
			@Override
			public boolean hasExpired() {
				// Expire if dwarf not holding book
				return super.hasExpired() || !spellbook.isSimilar(dwarf.getHeldItem());
			}
		});
	}
}
