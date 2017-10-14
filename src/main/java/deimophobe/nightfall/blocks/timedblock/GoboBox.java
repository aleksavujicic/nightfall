package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.damage.DamageManager;
import deimophobe.nightfall.damage.DamageModifier;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 28/02/17.
 */
public class GoboBox extends TimedBlock {
	private final double power;
	private final double damage;
	private final double kb;

	public GoboBox(Block block, int lifeTime, double damage, double power, double kb, GameEntity placer) {
		super(block, Material.ENDER_STONE, lifeTime, placer);
		this.damage = damage;
		this.power = power;
		this.kb = kb;
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		if (!cancelled) {
			Location centerLoc = block.getLocation().add(0.5, 0.5, 0.5);
			World world = centerLoc.getWorld();
			
			BlockConverter.convert(BlockConverter.Type.EXPLOSION, centerLoc, power);
			world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 3, 1, 1, 1);
			world.playSound(centerLoc, "entity.generic.explode", 2, 1);

			for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
				Vector offset = dwarf.getLocation().subtract(centerLoc).toVector();
				if (offset.length() > 5) continue;

				DamageModifier modifier = new DamageModifier();

				Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(1.5, offset.length())) );
				knockback.setY(knockback.getY() / 2 + 0.5);
				modifier.addKnockback(knockback);

				DwarfDamage aoeDamage = dwarf.createDamage(getPlacer(), CustomDamageType.GOBO_BOX_EXPLOSION, damage);
				modifier.applyToDamage(aoeDamage);
				aoeDamage.setArmourShred(25);
				aoeDamage.fire();
			}
		}
	}
	
	@Override
	void onHit(GamePlayer player) {
		if (player instanceof Dwarf)
			this.cancel();
	}
}
