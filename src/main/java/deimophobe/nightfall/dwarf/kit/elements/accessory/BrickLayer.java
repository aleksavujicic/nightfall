package deimophobe.nightfall.dwarf.kit.elements.accessory;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.ConsumerCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 20/11/17.
 */
public class BrickLayer extends AbstractItem {
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "bricklayer");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	
	private static final int MAX_VOLUME = 1000;
	
	private Block firstCorner = null;
	private Block secondCorner = null;
	private boolean selectingFirst = true;
	private final ConsumerCooldown<Block> selector = new ConsumerCooldown<>(10, this::selectBlock);
	private final ComplexCooldown toggler = new ComplexCooldown(10, this::startOrPause);
	
	private Builder builder = null;
	
	//private final RemoveListener listener;
	
	public BrickLayer(Dwarf dwarf) {
		super(dwarf);
	//	listener = new RemoveListener();
	//	Bukkit.getPluginManager().registerEvents(listener, NightfallPlugin.getPlugin());
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		selector.update();
		toggler.update();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isLeftClick(action)) {
			return selector.tryUse(clickedBlock);
		} else {
			return toggler.tryUse();
		}
	}
	
	private void selectBlock(Block block) {
		if (!canUseInCurrentPhase()) {
			dwarf.sendTitleMessage(ChatColor.RED + "You can't use this now.");
			return;
		}
		
		if (builder != null) {
			builder.cancel();
		}
		
		if (selectingFirst) {
			firstCorner = block;
			dwarf.sendTitleMessage(ChatColor.AQUA + "First corner selected");
		} else {
			secondCorner = block;
			dwarf.sendTitleMessage(ChatColor.AQUA + "Second corner selected");
		}
		
		selectingFirst = !selectingFirst;
	}
	
	private void startOrPause() {
		if (!canUseInCurrentPhase()) {
			dwarf.sendTitleMessage(ChatColor.RED + "You can't use this now.");
			return;
		}
		
		if (firstCorner == null || secondCorner == null) {
			dwarf.sendTitleMessage(ChatColor.RED + "Select corner blocks first");
			return;
		}
		
		if (builder == null) {
			if (getVolume() > MAX_VOLUME) {
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
	
	private boolean canUseInCurrentPhase() {
		Phase phase = Game.getGame().getPhase();
		return phase == Phase.STARTING || phase == Phase.BUILD || phase == Phase.PLAGUE;
	}
	
	private class Builder extends BukkitRunnable {
		private Block nextBlock;
		
		private final Dimension xDim;
		private final Dimension yDim;
		private final Dimension zDim;
		
		private boolean paused = false;
		
		protected Builder(Block firstCorner, Block secondCorner) {
			this.nextBlock = firstCorner;
			
			xDim = new Dimension(firstCorner.getX(), secondCorner.getX());
			yDim = new Dimension(firstCorner.getY(), secondCorner.getY());
			zDim = new Dimension(firstCorner.getZ(), secondCorner.getZ());
			
			runTaskTimer(NightfallPlugin.getPlugin(), 0, 4);
			
			dwarf.sendTitleMessage(ChatColor.YELLOW + "Placing blocks...");
		}
		
		@Override
		public void run() {
			if (paused) return;
			if (!canUseInCurrentPhase()) {
				cancel();
				return;
			}
			
			if (!dwarf.hasConsumable(ConsumableType.COBBLESTONE)) {
				dwarf.sendTitleMessage(ChatColor.RED + "No more cobble to place");
				this.cancel();
				return;
			}
			
			boolean placed = false;
			while (!placed) {
				if (nextBlock.getType() == Material.AIR && GameMap.getCurrentMap().isBlockPlaceable(nextBlock)) {
					dwarf.forceUseConsumable(ConsumableType.COBBLESTONE);
					dwarf.playSound("block.stone.place");
					nextBlock.setType(Material.COBBLESTONE);
					placed = true;
				}
				
				boolean xOverrun = xDim.increment();
				if (xOverrun) {
					boolean zOverrun = zDim.increment();
					if (zOverrun) {
						boolean yOverrun = yDim.increment();
						if (yOverrun) {
							dwarf.sendTitleMessage(ChatColor.GREEN + "Finished placing cobble");
							this.cancel();
							return;
						}
					}
				}
				
				nextBlock = nextBlock.getWorld().getBlockAt(xDim.getValue(), yDim.getValue(), zDim.getValue());
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
	
	
	/*
	private class RemoveListener implements Listener {
		@EventHandler
		public void onPhaseChange(PhaseChangeEvent event) {
			if (event.getPhase() == Phase.GAME) {
				if (builder != null) builder.cancel();
				dwarf.replaceItem(ITEM, null);
				
				HandlerList.unregisterAll(this);
			}
		}
	}
	
	@Override
	public void onRemove() {
		HandlerList.unregisterAll(listener);
	}
	*/
}
