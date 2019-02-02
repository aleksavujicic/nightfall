package deimophobe.nightfall.effects;

import deimophobe.nightfall.effects.sound.PlayerSound;
import deimophobe.nightfall.game.entity.GamePlayer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 27/10/17.
 */
class SmallGoldMineMaker implements PlayerBlockEffectMaker {
	
	private final Particle.DustOptions dustOptions;
	private final PlayerSound sound;
	
	SmallGoldMineMaker(Color color, PlayerSound sound) {
		this.dustOptions = new Particle.DustOptions(color, 1);
		this.sound = sound;
	}
	
	@Override
	public void playEffect(GamePlayer player, Block block) {
		sound.playSound(player);
		Location center = block.getLocation().add(0.5,0.5,0.5);
		spawnMultipleGoldParticle(center);
	}
	
	
	protected void spawnSingleGoldParticle(Location loc) {
		loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, dustOptions);
	}
	
	protected void spawnMultipleGoldParticle(Location loc) {
		loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 10, 0.3, 0.3, 0.3, dustOptions);
	}
}
