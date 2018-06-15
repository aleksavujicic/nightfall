package deimophobe.nightfall.dwarf.kit.spell;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.util.Hitscan;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 14/06/18.
 */
class SoulStreamSpell implements Spell {
	@Override public String getName() { return ChatColor.DARK_AQUA + "Soul Stream";	}
	@Override public int getCost() { return 5;	}
	@Override public int getCooldown() { return 4 * 20;	}
	
	private static final double MAX_RANGE = 10;
	private static final double THICKNESS = 1.25;
	private static final double PARTICLE_OFFSET = THICKNESS / 10;
	
	private static final Consumer<Location> PARTICLE_PLACER = location -> {
		location.getWorld().spawnParticle(Particle.CRIT_MAGIC, location, 2, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0.02);
	};
	
	
	private final CustomItem spellbook;
	
	SoulStreamSpell(CustomItem spellbook) {
		this.spellbook = spellbook;
	}
	
	@Override
	public void castSpell(Dwarf dwarf) {
		dwarf.givePotionEffect(PotionEffectType.SLOW, 50, 3, false, false, true);
		dwarf.givePotionEffect(PotionEffectType.JUMP, 50, -2, false, false, true);
		dwarf.addUpdateable(new LifetimeExpireable(50) {
			@Override
			public void update() {
				super.update();
				Consumer<MonsterEntity> mobDamager = dwarf.new GameEntityDamager<MonsterEntity>(
						GameDamageType.SOUL_STREAM, 1, true, damage -> damage.setNoDamageTicks(1)
				);
				Hitscan hitscan = new Hitscan(THICKNESS, PARTICLE_PLACER, null, mobDamager);
				hitscan.fire(dwarf, MAX_RANGE);
				
				float pitch = 2f - 1.5f * getLifetime()/50f;
				dwarf.playSound("block.note.bass", 1f, pitch, true);
			}
			
			@Override
			public boolean hasExpired() {
				// Expire if dwarf not holding book
				return super.hasExpired() || !spellbook.isSimilar(dwarf.getHeldItem());
			}
			
			@Override
			public void onExpiry() {
				super.onExpiry();
				dwarf.removePotionEffect(PotionEffectType.SLOW);
				dwarf.removePotionEffect(PotionEffectType.JUMP);
			}
		});
	}
}
