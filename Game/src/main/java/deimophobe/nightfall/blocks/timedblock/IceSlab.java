package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class IceSlab extends DataTimedBlock {
	private final int maxLifetime;
	private final ComplexCooldown hitter = new ComplexCooldown(4, this::hit);
	
	private IceSlab(Block block, GameEntity placer, int lifetime) {
		super(lifetime, block, placer, Material.FROSTED_ICE);
		this.maxLifetime = lifetime;
	}
	
	public IceSlab(Block block, GameEntity placer) {
		this(block, placer, getNewLifetime());
	}
	
	@Override
	public void update() {
		super.update();
		updateAge();
		hitter.update();
	}
	
	@Override
	public void placeBlock() {
		super.placeBlock();
		World world = block.getWorld();
		Location location = block.getLocation().add(0.5, 0.5, 0.5);
		
		world.spawnParticle(Particle.SNOW_SHOVEL, location, 5, 0.5, 0.5, 0.5, 0);
	}
	
	@Override
	public void unplaceBlock(boolean cancelled) {
		super.unplaceBlock(cancelled);
		if (cancelled) return;
		
		World world = block.getWorld();
		Location location = block.getLocation().add(0.5, 0.5, 0.5);
		
		block.setType(Material.AIR);
		world.spawnParticle(Particle.BLOCK_CRACK, location, 25, 0.5, 0.5, 0.5, 0, new MaterialData(Material.FROSTED_ICE));
		world.playSound(location, "block.glass.break", 1, 1);
	}
	
	@Override
	public void onHit(GamePlayer player, ClickType click, BlockFace blockFace) {
		if (!click.isLeftClick()) return;
		
		if (maxLifetime - getLifetime() < 40) return;
		hitter.tryUse();
	}
	
	private void hit() {
		reduceLifetime(30);
		
		World world = block.getWorld();
		world.playSound(block.getLocation(), "block.note.chime", 0.5f, 2f - fracLeft()*0.75f);
		world.playSound(block.getLocation(), "block.glass.place", 1f, 1f);
	}
	
	private void updateAge() {
		float fracLeft = fracLeft();
		
		byte age;
		if (fracLeft <= 0.1) {
			age = 3;
		} else if (fracLeft <= 0.2) {
			age = 2;
		} else if (fracLeft <= 0.3) {
			age = 1;
		} else {
			age = 0;
		}
		
		if (block.getData() != age) {
			block.setData(age);
		}
	}
	
	private float fracLeft() {
		return (float) getLifetime()/maxLifetime;
	}
	
	private static int getNewLifetime() {
		return Misc.randomInt(30*2, 40*2)*10;
	}
}
