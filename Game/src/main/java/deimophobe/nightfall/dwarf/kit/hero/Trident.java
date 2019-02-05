package deimophobe.nightfall.dwarf.kit.hero;

import com.google.common.collect.Sets;
import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.NFBlocks;
import deimophobe.nightfall.blocks.timedblock.IceSlab;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.*;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class Trident extends AbstractItem implements CooldownPiece, LogOnOffPiece {
	
	public Trident(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
		TRIDENTS.add(this);
	}
	
	private static final double MAX_WATER = 400;
	private double water = MAX_WATER;
	
	private final Set<Block> waterBlocks = new HashSet<>();
	
	private ComplexCooldown waterCD = new ComplexCooldown(3, this::sprayWater);
	private ComplexCooldown waterRegenDelay = new ComplexCooldown(10*20, null, this::clearWaterBlocks);
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero","trident");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getPickupType() { return PickupType.START; }
	
	@Override
	public void update() {
		super.update();
		waterCD.update();
		waterRegenDelay.update();
		
		if (waterRegenDelay.isAvailable() && water < MAX_WATER) {
			water = Math.min(water + 0.1, MAX_WATER);
		}
	}
	
	@Override
	public boolean onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		
		if (click.isRightClick()) {
			if (water > 0) return waterCD.tryUse();
		} else  {
			freezeWaterBlocks();
		}
		
		return false;
	}
	
	@Override
	public void onLogOn() {}
	
	@Override
	public void onLogOff() {
		clearWaterBlocks();
	}
	
	private static final Set<Material> WATER_MATERIALS =
			Sets.newHashSet(Material.AIR);
			//Sets.newHashSet(Material.AIR, Material.WATER, Material.STATIONARY_WATER); add carpet
	private void sprayWater() {
		
		Block looking = dwarf.getTargetBlock(WATER_MATERIALS, 12);
		boolean placedBlock = false;
		if (water > 0) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						Block block = looking.getRelative(x, y, z);
						placedBlock |= replaceBlockWithWater(block);
					}
				}
			}
			water = Math.max(water, 0);
		}
		
		if (placedBlock) {
			waterRegenDelay.reset();
			dwarf.playSound("item.bucket.empty", 1f, 1f, true);
			
			World world = looking.getWorld();
			world.spawnParticle(Particle.WATER_DROP, looking.getLocation().add(0.5,0.5,0.5), 100, 1.5, 1.5, 1.5);
		} else {
			dwarf.playSound("item.bucket.empty", 1f, 2f, false);
			
			World world = looking.getWorld();
			world.spawnParticle(Particle.WATER_DROP, looking.getLocation().add(0.5,0.5,0.5), 5, 0.5, 0.5, 0.5);
		}
	}
	
	private boolean replaceBlockWithWater(Block block) {
		if (NFBlocks.IGNORABLE.matchesBlock(block) && GameMap.getCurrentMap().isBlockPlaceable(block)) {
			water--;
			block.setType(Material.WATER, false);
			waterBlocks.add(block);
			return true;
		}
		return false;
	}
	
	private void freezeWaterBlocks() {
		if (waterBlocks.isEmpty()) return;
		
		dwarf.playSound("block.snow.place", 1, 1, true);
		
		// Clear water blocks before freezing so they may be placed on by timed ice blocks
		Set<Block> freezeBlock = new HashSet<>(waterBlocks);
		waterBlocks.clear();
		for (Block block : freezeBlock) {
			BlockManager.getManager().placeTimedBlock(new IceSlab(block, dwarf));
		}
	}
	
	private void clearWaterBlocks() {
		if (waterBlocks.isEmpty()) return;
		
		for (Block block : waterBlocks) {
			block.setType(Material.AIR, false);
			block.getWorld().spawnParticle(Particle.WATER_DROP, block.getLocation().add(0.5, 0.5, 0.5), 15, 0.5, 0.5, 0.5);
		}
		waterBlocks.clear();
	}
	
	@Override
	public float getCooldown() {
		return (float) (water/MAX_WATER);
	}
	
	@Override
	public void onRemove() {
		super.onRemove();
		TRIDENTS.remove(this);
		clearWaterBlocks();
	}
	
	private static final Set<Trident> TRIDENTS = new HashSet<>();
	public static boolean isTridentWaterBlock(Block block) {
		for (Trident trident : TRIDENTS) {
			if (trident.waterBlocks.contains(block)) return true;
		}
		return false;
	}
}
