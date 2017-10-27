package deimophobe.nightfall.effects;

import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 27/10/17.
 */
class SmallGoldMineMaker implements PlayerBlockEffectMaker {
	
	@Override
	public void playEffect(GamePlayer player, Block block) {
		Sounds.DWARF_MINE_GOLD.playSound(player);
		block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().add(0.5,0.5,0.5), 0, 0, 0, 0, 1);
	}
}
