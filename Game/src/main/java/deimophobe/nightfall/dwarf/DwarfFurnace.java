package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * Created by Deimophobe on 4/05/18.
 *
 */
public class DwarfFurnace implements Updateable {
	
	private final Dwarf dwarf;
	private final Cooldown messageCooldown = new SimpleCooldown(40);
	private final Queue<CookingProcess> processes = new LinkedList<>();
	private final Map<ConsumableType, Integer> readyItems = new EnumMap<>(ConsumableType.class);
	
	public DwarfFurnace(Dwarf dwarf) {
		this.dwarf = dwarf;
	}
	
	@Override
	public void update() {
		messageCooldown.update();
		
		CookingProcess currentProcess = processes.peek();
		if (currentProcess == null) return;
		
		currentProcess.update();
		if (currentProcess.isComplete()) {
			processes.poll();
		}
	}
	
	public void addItems(ConsumableType result, int time, int count) {
		processes.add(new CookingProcess(result, time, count));
	}
	
	void giveItems() {
		boolean gave = false;
		for (Map.Entry<ConsumableType, Integer> entry : readyItems.entrySet()) {
			dwarf.giveConsumable(entry.getKey(), entry.getValue());
			gave = true;
		}
		readyItems.clear();
		
		if (gave) dwarf.playSound("entity.item.pickup", 1f, 0.8f, false);
		
		if (gave || messageCooldown.isAvailable()) {
			int itemsLeft = totalItemCount();
			if (itemsLeft == 0) {
				dwarf.sendTitleMessage(ChatColor.GREEN + "There are no items left in your furnace");
			} else if (itemsLeft == 1) {
				dwarf.sendTitleMessage(ChatColor.YELLOW + "There is " + ChatColor.AQUA + "1" + ChatColor.YELLOW + " item left in your furnace");
			} else {
				dwarf.sendTitleMessage(ChatColor.YELLOW + "There are " + ChatColor.AQUA + itemsLeft + ChatColor.YELLOW + " items left in your furnace");
			}
			messageCooldown.reset();
		}
	}
	
	private int totalItemCount() {
		int count = 0;
		for (CookingProcess process : processes) {
			count += process.leftToCook;
		}
		return count;
	}
	
	private class CookingProcess implements Updateable {
		private final ConsumableType result;
		private final int cookTime;
		private int leftToCook;
		private int cooldown;
		
		private CookingProcess(ConsumableType result, int cookTime, int leftToCook) {
			this.result = result;
			this.cookTime = cookTime;
			this.leftToCook = leftToCook;
		}
		
		@Override
		public void update() {
			cooldown--;
			if (cooldown > 0) return;
			
			// Finished cooking current item
			cooldown = cookTime;
			leftToCook--;
			DwarfFurnace.this.readyItems.compute(result, (k, v) -> (v == null ? 1 : v + 1));
		}
		
		private boolean isComplete() {
			return leftToCook == 0;
		}
	}
}
