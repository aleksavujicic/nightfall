package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ConsumerCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 20/11/17.
 */
public class Bricklayer extends AbstractItem {
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "bricklayer");
	private final static CustomItem SPEEDY_ITEM = DwarvenItems.getItem("accessory", "bricklayer");
	static {
		SPEEDY_ITEM.setShiny(true);
		SPEEDY_ITEM.setName("Speedy Bricklayer");
	}
	
	@Override
	public CustomItem getItem() {
		return (speedy ? SPEEDY_ITEM : ITEM);
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	
	private Block firstCorner = null;
	private Block secondCorner = null;
	private final ConsumerCooldown<Block> selector = new ConsumerCooldown<>(10, this::selectBlock);
	private final Cooldown toggler = new UseCooldown(10, this::startOrPause);
	
	private Builder builder = null;
	
	private final boolean speedy;
	
	public Bricklayer(Dwarf dwarf, boolean speedy) {
		super(dwarf);
		this.speedy = speedy;
	}
	
	@Override
	public void update() {
		super.update();
		selector.update();
		toggler.update();
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isLeftClick()) {
			return selector.tryUse(clickedBlock);
		} else {
			return toggler.tryUse();
		}
	}
	
	private void selectBlock(Block block) {
		if (builder != null) {
			builder.cancel();
		}
		
		if (!dwarf.isSneaking()) {
			firstCorner = block;
			dwarf.sendTitleMessage(ChatColor.AQUA + "First corner selected");
		} else {
			secondCorner = block;
			dwarf.sendTitleMessage(ChatColor.AQUA + "Second corner selected");
		}
	}
	
	private void startOrPause() {
		if (firstCorner == null || secondCorner == null) {
			dwarf.sendTitleMessage(ChatColor.RED + "Select corner blocks first");
			return;
		}
		
		OperationState state = getOperationState();
		if (state == null) {
			dwarf.sendTitleMessage(ChatColor.RED + "You cannot build right now");
			return;
		}
		
		if (builder == null) {
			int maxVol = state.maxVolume;
			if (getVolume() > maxVol) {
				dwarf.sendTitleMessage(ChatColor.RED + "Your selected region is too big");
				return;
			}
			
			builder = new Builder(firstCorner, secondCorner);
		} else {
			builder.togglePause();
		}
	}
	
	private int getVolume() {
		if (firstCorner == null || secondCorner == null) {
			return 0;
		}
		
		return Math.abs(
				(firstCorner.getX() - secondCorner.getX())
				* (firstCorner.getY() - secondCorner.getY())
				* (firstCorner.getZ() - secondCorner.getZ())
		);
	}
	
	private OperationState getOperationState() {
		boolean build = isBuildPhase();
		
		if (build && speedy) return OperationState.SPEEDY_BUILD;
		if (!build && speedy) return OperationState.SPEEDY_NOBUILD;
		if (build && !speedy) return OperationState.NOSPEEDY_BUILD;
		
		return null;
	}
	
	private boolean isBuildPhase() {
		return Game.getGame().getPhase().isBefore(Phase.GAME);
	}
	
	private class Builder extends BukkitRunnable {
		private BlockSupplier supplier;
		private boolean paused = false;
		
		
		protected Builder(Block firstCorner, Block secondCorner) {
			supplier = new BlockSupplier(firstCorner, secondCorner);
			
			OperationState state = getOperationState();
			if (state == null) throw new IllegalStateException("Cannot start builder if operation state is null.");
			
			int freq = state.frequency;
			int delay = state.startDelay;
			
			runTaskTimer(NightfallPlugin.getPlugin(), delay, freq);
			dwarf.sendTitleMessage(ChatColor.YELLOW + "Placing blocks...");
		}
		
		@Override
		public void run() {
			if (!dwarf.isOnline()) paused = true;
			if (paused) return;
			
			if (!dwarf.hasConsumable(ConsumableType.COBBLESTONE)) {
				dwarf.sendTitleMessage(ChatColor.RED + "No more cobble to place");
				this.cancel();
				return;
			}
			
			while (true) {
				Block nextBlock = supplier.get();
				
				if (nextBlock == null) {
					dwarf.sendTitleMessage(ChatColor.GREEN + "Finished placing cobble");
					this.cancel();
					return;
				}
				
				if (BlockType.IGNORABLE.matchesBlock(nextBlock) && GameMap.getCurrentMap().isBlockPlaceable(nextBlock)) {
					dwarf.removeItems(ConsumableType.COBBLESTONE, 1, true);
					nextBlock.getWorld().playSound(nextBlock.getLocation(), "block.stone.place", 1f, 1f);
					dwarf.playSound("block.stone.place");
					nextBlock.setType(Material.COBBLESTONE);
					break;
				}
			}
		}
		
		private void togglePause() {
			paused = !paused;
			
			if (paused) dwarf.sendTitleMessage(ChatColor.RED + "Pausing build");
			else dwarf.sendTitleMessage(ChatColor.GREEN + "Resuming build");
		}
		
		@Override
		public synchronized void cancel() throws IllegalStateException {
			super.cancel();
			builder = null;
		}
	}
	
	private static class BlockSupplier implements Supplier<Block> {
		private final World world;
		
		private final Dimension xDim;
		private final Dimension yDim;
		private final Dimension zDim;
		
		private boolean finished = false;
		
		protected BlockSupplier(Block firstCorner, Block secondCorner) {
			world = firstCorner.getWorld();
			
			// Swap corners if first corner above first
			// so that places blocks from bottom to top
			if (firstCorner.getY() > secondCorner.getY()) {
				Block temp = secondCorner;
				secondCorner = firstCorner;
				firstCorner = temp;
			}
			
			xDim = new Dimension(firstCorner.getX(), secondCorner.getX());
			yDim = new Dimension(firstCorner.getY(), secondCorner.getY());
			zDim = new Dimension(firstCorner.getZ(), secondCorner.getZ());
		}
		
		@Override
		public Block get() {
			if (finished) return null;
			
			// Get block
			Block block = world.getBlockAt(xDim.getValue(), yDim.getValue(), zDim.getValue());
			
			// Increment dimensions
			boolean xOverrun = xDim.increment();
			if (xOverrun) {
				boolean zOverrun = zDim.increment();
				if (zOverrun) {
					boolean yOverrun = yDim.increment();
					if (yOverrun) {
						finished = true;
					}
				}
			}
			
			return block;
		}
	}
	
	private static class Dimension {
		private final int first;
		private final int last;
		private int value;
		
		private Dimension(int first, int last) {
			this.first = first;
			this.last = last;
			value = first;
		}
		
		private int getValue() {
			return value;
		}
		
		private boolean increment() {
			boolean overrun;
			if (first <= last) {
				value++;
				overrun = (value > last);
			} else {
				value--;
				overrun = (value < last);
			}
			
			if (overrun) value = first;
			return overrun;
		}
	}
	
	private enum OperationState {
		SPEEDY_BUILD(1500, 0, 3),
		SPEEDY_NOBUILD(750, 20, 10),
		NOSPEEDY_BUILD(1000, 0, 5),
		
		;
		
		private final int maxVolume;
		private final int startDelay;
		private final int frequency;
		
		OperationState(int maxVolume, int startDelay, int frequency) {
			this.maxVolume = maxVolume;
			this.startDelay = startDelay;
			this.frequency = frequency;
		}
	}
	
	@Override
	public void onRemove() {
		if (builder != null) builder.cancel();
	}
}
