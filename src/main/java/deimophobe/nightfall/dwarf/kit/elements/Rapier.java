package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 28/10/17.
 */
class Rapier extends AbstractItem implements KitCooldownElement {
	
	private static final int STACK_CD_TIME = 5*20;
	private static final int INVINC_TIME = 3*20;
	private static final int MAX_STACKS = 60;
	private static final int PARRY_COST = 3;
	
	private int inivincCD;
	private int stackCD;
	private int stacks;
	
	Rapier(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.rapier", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() { return ITEM.createItemStack();}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (stackCD > 0)
			stackCD--;
		if (inivincCD > 0)
			inivincCD--;
		
		if (stackCD == 0) {
			if (stacks > 0)
				stacks--;
		}
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromItem(damage)) {
			stacks++;
			if (stacks > MAX_STACKS)
				stacks = MAX_STACKS;
			
			damage.getDamage().addBoost(stacks);
			stackCD = STACK_CD_TIME;
		}
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (inivincCD > 0)
			damage.cancel();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isRightClick(action) && isHoldingItem()) {
			if (stacks >= PARRY_COST) {
				stacks -= PARRY_COST;
				dwarf.leap(2,0.5);
				dwarf.playSound("entity.zombie.attack_iron_door", 1f, 1.5f, true);
				
				return true;
			}
		}
		return false;
	}
	
	@Override
	public float fractionComplete() {
		return (float) stacks/stackCD;
	}
}
