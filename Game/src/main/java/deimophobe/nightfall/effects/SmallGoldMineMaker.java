package deimophobe.nightfall.effects;

import com.google.common.collect.Sets;
import deimophobe.nightfall.effects.sound.PlayerSound;
import deimophobe.nightfall.game.player.GamePlayer;
import deimophobe.nightfall.util.Colour;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;

import java.util.Set;

/**
 * Created by Deimophobe on 27/10/17.
 */
class SmallGoldMineMaker implements PlayerBlockEffectMaker {
	
	private final Colour colour;
	private final PlayerSound sound;
	
	SmallGoldMineMaker(Colour colour, PlayerSound sound) {
		this.colour = colour;
		this.sound = sound;
	}
	
	@Override
	public void playEffect(GamePlayer player, Block block) {
		sound.playSound(player);
		spawnGoldParticle(block.getLocation().add(0.5,0.5,0.5));
		
		Set<Integer> offsets = Sets.newHashSet(-1, 1);
		
		offsets.forEach(ix -> offsets.forEach(iy -> offsets.forEach( iz -> {
			double x = ix*0.25;
			double y = iy*0.25;
			double z = iz*0.25;
			spawnGoldParticle(block.getLocation().add(0.5+x,0.5+y,0.5+z));
		})));
	}
	
	protected void spawnGoldParticle(Location loc) {
		loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, colour.getRed(), colour.getGreen(), colour.getBlue(), 1);
	}
}
