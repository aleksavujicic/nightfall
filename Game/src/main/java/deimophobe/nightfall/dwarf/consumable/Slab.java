package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.blocktype.BlockMatcher;
import deimophobe.nightfall.blocks.blocktype.BlockSet;
import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockIterator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Slab extends Consumable {
	private static final int LENGTH = 15;
	private static final int HEIGHT = 6;
	private static final int THICKNESS = 4;
	
	private static final int BUILD_STAGES = 7;
	private static final int STAGE_DELAY = 10;
	
	private static final int MIN_BLOCKS = 10;
	
	private static final ConsumeResult TOO_CLOSE = ConsumeResult.failedResultWithMessage("That block is too close to slab");
	private static final ConsumeResult NOT_ENOUGH_BLOCKS = ConsumeResult.failedResultWithMessage("Cannot slab there");
	private static final ConsumeResult SLAB_PLACED = ConsumeResult.successfulWithDuration(40);
	
	private static final Particle.DustOptions DUST_OPTIONS = new Particle.DustOptions(Color.fromRGB(20, 160, 240), 2f);
	
	private static final BlockMatcher SLABBABLE = new BlockSet(
			NFBlocks.NORMAL_WALL,
			NFBlocks.CRACKED_WALL,
			NFBlocks.DAMAGED_WALL,
			NFBlocks.BROKEN_WALL,
			NFBlocks.IGNORABLE,
			NFBlocks.LEAVES)
			.orOfMaterial(Material.FIRE)
			.andAlso(block -> GameMap.getCurrentMap().isBlockPlaceable(block));
	
	Slab(String item) {
		super(item);
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isRightClick()) return ConsumeResult.FAILURE;
		ConsumeResult phaseCheck = checkPhase();
		if (phaseCheck != null) return phaseCheck;
		
		// Get selected block, aim up
		Location playerLoc = dwarf.getEyeLocation();
		float pitch = playerLoc.getPitch();
		playerLoc.setPitch(Math.min(pitch,0));
		BlockIterator iterator = new BlockIterator(playerLoc, 0, 7);
		
		Block selectedBlock = null;
		while(iterator.hasNext()) {
			selectedBlock = iterator.next();
			
			if (!NFBlocks.AIR.matchesBlock(selectedBlock)) {
				break;
			}
		}
		
		if (selectedBlock == null) {
			NightfallPlugin.logger().warning("Failed to place slab as selected block was null (player " + dwarf.getName() +").");
			return ConsumeResult.FAILURE;
		}
		
		Location center = selectedBlock.getLocation();
		if (dwarf.distanceTo(center) <= 4) return TOO_CLOSE;
		
		
		double facing = dwarf.getLocation().getYaw() % 360;
		if (facing < 0) facing += 360;
		boolean invertXZ = (135 < facing && facing <= 215) || facing > 315 || facing <= 45;
		
		int dx = (invertXZ ? LENGTH : THICKNESS);
		int dy = HEIGHT;
		int dz = (invertXZ ? THICKNESS : LENGTH);
		
		double halfDX = (double) dx/2;
		double halfDY = (double) dy/2;
		double halfDZ = (double) dz/2;
		
		double bottomX = center.getX() - halfDX;
		double bottomY = center.getY() - halfDY;
		double bottomZ = center.getZ() - halfDZ;
		
		int minX = (int) Math.round(bottomX);
		int minY = (int) Math.round(bottomY);
		int minZ = (int) Math.round(bottomZ);
		
		int maxX = minX + dx;
		int maxY = minY + dy;
		int maxZ = minZ + dz;
		
		Set<Block>[] blockSets = new Set[BUILD_STAGES];
		for (int i = 0; i<BUILD_STAGES; i++) blockSets[i] = new HashSet<>();
		
		int totalConvertedBlocks = 0;
		
		GameMap map = GameMap.getCurrentMap();
		World world = map.getWorld();
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = minZ; z < maxZ; z++) {
					Block block = world.getBlockAt(x,y,z);
					if (SLABBABLE.matchesBlock(block)) {
						int blockSetIndex = Misc.randomInt(0, BUILD_STAGES - 1, a -> 1 - Math.pow(a, 2.2));
						Set<Block> blockSet = blockSets[blockSetIndex];
						blockSet.add(block);
						totalConvertedBlocks++;
					}
				}
			}
		}
		
		if (totalConvertedBlocks < MIN_BLOCKS) return NOT_ENOUGH_BLOCKS;
		
		world.playSound(center, "dwarf.consumable.slab.place", 1, 1.2f);
		world.playSound(center, "block.anvil.use", 1, 0.5f);
		world.spawnParticle(Particle.REDSTONE, center, 100, halfDX/2, halfDY/2, halfDZ/2, DUST_OPTIONS);
		Game.getGame().addUpdateable(new LifetimeExpireable(BUILD_STAGES*STAGE_DELAY) {
			int i = 0;
			@Override
			public void update() {
				if (everyNTicks(STAGE_DELAY)) {
					Set<Block> blocksToReplace = blockSets[i];
					for (Block block : blocksToReplace) {
						boolean placed = NFBlocks.tryConvertBlock(block, SLABBABLE, NFBlocks.ENCHANTED_WALL);
						if (!placed) continue;
						
						Location center = block.getLocation().add(0.5, 0.5, 0.5);
					}
					i++;
					world.spawnParticle(Particle.REDSTONE, center, 100, halfDX/2, halfDY/2, halfDZ/2, DUST_OPTIONS);
					if (i == BUILD_STAGES) {
						world.spawnParticle(Particle.CLOUD, center, 200, halfDX/2, halfDY/2, halfDZ/2, 0.1);
					}
				}
				super.update();
			}
		});
		
		return SLAB_PLACED;
	}
}
