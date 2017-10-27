package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Tombmaker extends AbstractCooldownItem {
	
	Tombmaker(Dwarf dwarf) {
		super(dwarf, 60*20);
	}
	
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.tombmaker", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SHOVEL; }
	
	
	@Override
	public void onKill(MonsterDamage damage) {
		if (itemCausedDamage(damage) && dwarf.hasKitElement(KitElementType.GRB))
			dwarf.giveProc(ProcType.REGULAR);
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			dwarf.playSound("proc", 1, 1, false);
			dwarf.givePotionEffect(PotionEffectType.FAST_DIGGING, 20*20 , 3, true, false, true);
			resetCooldown();
			return true;
		}
		return false;
	}
	
	private static final double FIND_CHANCE = 0.001;
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		if (block.getType() == Material.GRAVEL && Game.getGame().getPhase() == Phase.BUILD && didBreak) {
			if (Math.random() <= FIND_CHANCE) {
				ScavengeItem.getRandom().giveToDwarf(dwarf);
			}
		}
	}
	
	
	
	private static class ScavengeItem {
		private static double MAX_WEIGHT;
		private static final Set<ScavengeItem> ITEMS = new HashSet<>();
		static {
			new ScavengeItem(ConsumableType.SLAB, 1, 5);
			new ScavengeItem(ConsumableType.SLAB, 2, 3);
			new ScavengeItem(ConsumableType.SLAB, 3, 1);
			new ScavengeItem(ConsumableType.HEAL_STATION, 1, 8);
			new ScavengeItem(ConsumableType.HEAL_STATION, 2, 5);
			new ScavengeItem(ConsumableType.HEAL_STATION, 4, 2);
			new ScavengeItem(ConsumableType.LAMP, 2, 10);
			new ScavengeItem(ConsumableType.LAMP, 6, 5);
			new ScavengeItem(ConsumableType.LAMP, 12, 2);
			new ScavengeItem(ConsumableType.SOS, 1, 1.5);
			new ScavengeItem(ConsumableType.SOS, 2, 0.5);
			new ScavengeItem(ConsumableType.WRENCH, 1, 2);
			new ScavengeItem(ConsumableType.WRENCH, 2, 0.5);
			new ScavengeItem(ConsumableType.WIZARD_MORTAR, 8, 10);
			new ScavengeItem(ConsumableType.WIZARD_MORTAR, 16, 5);
			new ScavengeItem(ConsumableType.WIZARD_MORTAR, 32, 2);
			
			for (ScavengeItem item : ITEMS)
				item.computeAdjWeight();
		}
		
		private final ConsumableType type;
		private final int amt;
		private final double rawWeight;
		private double adjustedWeight;
		
		private ScavengeItem(ConsumableType type, int amt, double rawWeight) {
			this.type = type;
			this.amt = amt;
			this.rawWeight = rawWeight;
			
			MAX_WEIGHT += rawWeight;
			ITEMS.add(this);
		}
		
		private void computeAdjWeight() {
			adjustedWeight = rawWeight/MAX_WEIGHT;
		}
		
		private void giveToDwarf(Dwarf dwarf) {
			dwarf.giveConsumable(type, amt);
		}
		
		private static ScavengeItem getRandom() {
			double rand = Math.random();
			for (ScavengeItem item : ITEMS) {
				rand -= item.adjustedWeight;
				if (rand < 0)
					return item;
			}
			// Should never reach here
			return ITEMS.iterator().next();
		}
	}
}
