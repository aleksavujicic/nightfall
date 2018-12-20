package deimophobe.nightfall.blocks;

import deimophobe.nightfall.blocks.blocktype.BlockMatcher;
import deimophobe.nightfall.blocks.blocktype.BlockSet;
import deimophobe.nightfall.blocks.blocktype.MaterialSet;
import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.dwarf.kit.hero.Trident;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 25/04/18.
 */
public class BlockListener implements Listener {
	private final BlockManager manager;
	
	public BlockListener(BlockManager manager) {
		this.manager = manager;
	}
	
	@EventHandler
	public void preventFireSpread(BlockSpreadEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void preventBlockBurn(BlockBurnEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void preventIceMelt(BlockFadeEvent event) {
		switch (event.getNewState().getType()) {
			case WATER:
			case FROSTED_ICE:
				event.setCancelled(true);
		}
		
		// Prevent snow melt too
		if (event.getBlock().getType() == Material.SNOW)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void preventObsidian(BlockFormEvent event) {
		BlockState newState = event.getNewState();
		switch (newState.getType()) {
			case OBSIDIAN:
			case SPONGE:
			case WET_SPONGE:
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventWaterFlow(BlockFromToEvent event) {
		Block toBlock = event.getToBlock();
		Block fromBlock = event.getBlock();
		
		if (Trident.isTridentWaterBlock(toBlock) || Trident.isTridentWaterBlock(fromBlock)) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getBlock().getType() == Material.WATER) {
			if (!toBlock.getRelative(0,-1,0).getType().isSolid()) return;
			
			int numFaceWaterBlocks = 0;
			if (toBlock.getRelative(1,0,0).getType() == Material.WATER)
				numFaceWaterBlocks++;
			if (toBlock.getRelative(-1,0,0).getType() == Material.WATER)
				numFaceWaterBlocks++;
			if (toBlock.getRelative(0,0,1).getType() == Material.WATER)
				numFaceWaterBlocks++;
			if (toBlock.getRelative(0,0,-1).getType() == Material.WATER)
				numFaceWaterBlocks++;
			
			if (numFaceWaterBlocks >= 2) return;
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void disablePortalTravel(PlayerPortalEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void blockLandEvent(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType() != EntityType.FALLING_BLOCK) return;
		
		Block block = event.getBlock();
		boolean placeable = GameMap.getCurrentMap().isBlockPlaceable(block);
		if (!placeable) event.setCancelled(true);
	}
	
	@EventHandler
	public void blockPhysics(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		
		switch (block.getType()) {
			case OBSIDIAN:
			case SPONGE:
			case WET_SPONGE:
				event.setCancelled(true);
				return;
		}
		if (manager.isTimedBlock(block)) {
			event.setCancelled(true);
		}
	}
	
	private static final BlockMatcher UNHOEABLE = new BlockSet(
			NFBlocks.GRASS_BLOCK,
			NFBlocks.DIRT,
			NFBlocks.PODZOL
	);
	private static final BlockMatcher UNAXEABLE = new BlockSet(
			NFBlocks.LOG
	);
	
	
	@EventHandler
	public void preventBlockInteraction(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE) return;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		Block block = event.getClickedBlock();
		Action action = event.getAction();
		
		if (block != null && item != null && action == Action.RIGHT_CLICK_BLOCK) {
			switch (item.getType()) {
				case DIAMOND_HOE:
				case DIAMOND_SHOVEL:
				case GOLDEN_HOE:
				case GOLDEN_SHOVEL:
				case IRON_HOE:
				case IRON_SHOVEL:
				case STONE_HOE:
				case STONE_SHOVEL:
				case WOODEN_HOE:
				case WOODEN_SHOVEL:
					if (UNHOEABLE.matchesBlock(block)) event.setCancelled(true);
					break;
					
				case DIAMOND_AXE:
				case GOLDEN_AXE:
				case IRON_AXE:
				case STONE_AXE:
				case WOODEN_AXE:
					if (UNAXEABLE.matchesBlock(block)) event.setCancelled(true);
					break;
			}
		}
	}
}
