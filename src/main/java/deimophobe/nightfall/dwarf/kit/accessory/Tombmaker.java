package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.DwarfShovel;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Tombmaker extends DwarfShovel implements CooldownPiece {
	
	public Tombmaker(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final ComplexCooldown hasteCD = new ComplexCooldown(60*20, this::hasteBuff);
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "tombmaker");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SHOVEL; }
	
	
	@Override
	public void onKill(MonsterDamage damage) {
		if (damageFromItem(damage) && dwarf.hasKitElement(KitPieceType.GRB))
			dwarf.giveProc(ProcType.REGULAR);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		hasteCD.update();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			return hasteCD.tryUse();
		}
		return false;
	}
	
	private void hasteBuff() {
		dwarf.playSound("proc", 1, 1, false);
		dwarf.givePotionEffect(PotionEffectType.FAST_DIGGING, 30*20 , 3, true, false, true);
	}
	
	
	@Override
	public float fractionComplete() {
		return hasteCD.fractionComplete();
	}
	
	
	
	
}
