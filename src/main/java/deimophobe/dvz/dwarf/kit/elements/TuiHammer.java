package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitCooldownElement;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.monster.ai.AIManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 11/03/17.
 */
class TuiHammer extends AbstractAOEHitter implements KitCooldownElement {
	TuiHammer(Dwarf dwarf) {
		super(dwarf, 4);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero.tuihammer", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.START;
	}
	
	@Override
	protected double getDamageToMonster(GameEntity entity) {
		if (entity instanceof MonsterPlayer) {
			return (dwarf.hasProc() ? 12 : 8);
		} else if (entity instanceof AIEntity) {
			return (dwarf.hasProc() ? 50 : 25);
		}
		return 0;
	}
	
	
	private int cooldown;
	private static final int MAX_CD = 60 * 20;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (cooldown >0)
			cooldown--;
	}
	
	private final static double AI_RADIUS = 20;
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && cooldown == 0) {
			dwarf.sendMessage(ChatColor.GOLD + "ROAR!!!");
			dwarf.giveProc(Dwarf.ProcType.ROAR);
			
			for (AIEntity ai : AIManager.getManager().getAIs()) {
				if (dwarf.getLocation().distance(ai.getLocation()) <= AI_RADIUS) {
					ai.setTarget(dwarf);
				}
			}
			
			cooldown = MAX_CD;
			return true;
		}
		return false;
	}
	
	@Override
	public float fractionComplete() {
		return 1 - ((float)cooldown/MAX_CD);
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return getItem().createItemStack();
	}
}
