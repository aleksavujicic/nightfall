package deimophobe.nightfall.blocks;

import deimophobe.nightfall.dwarf.kit.hero.Trident;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerPortalEvent;

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
			case STATIONARY_WATER:
			case FROSTED_ICE:
				event.setCancelled(true);
		}
		
		// Prevent snow melt too
		if (event.getBlock().getType() == Material.SNOW)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void preventObsidian(BlockFormEvent event) {
		if (event.getNewState().getType() == Material.OBSIDIAN) {
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
		
		if (event.getBlock().getType() == Material.STATIONARY_WATER) {
			if (!toBlock.getRelative(0,-1,0).getType().isSolid()) return;
			
			int numFaceWaterBlocks = 0;
			if (toBlock.getRelative(1,0,0).getType() == Material.STATIONARY_WATER)
				numFaceWaterBlocks++;
			if (toBlock.getRelative(-1,0,0).getType() == Material.STATIONARY_WATER)
				numFaceWaterBlocks++;
			if (toBlock.getRelative(0,0,1).getType() == Material.STATIONARY_WATER)
				numFaceWaterBlocks++;
			if (toBlock.getRelative(0,0,-1).getType() == Material.STATIONARY_WATER)
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
		if (manager.isTimedBlock(block)) {
			event.setCancelled(true);
		}
	}
}
