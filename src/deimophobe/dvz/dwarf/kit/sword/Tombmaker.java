package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.*;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Tombmaker extends Sword {
	
	Tombmaker(Dwarf dwarf) {
		super(dwarf, SwordType.TOMBMAKER, 300);
	}
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {
		if (type.isMelee() && dwarf.hasProc() && isHoldingItem())
			dwarf.giveProc(Dwarf.ProcType.REGULAR);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			dwarf.playSound("proc", 1, 1, false);
			dwarf.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 2), true);
			resetCooldown();
		}
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block.getType() == Material.GRAVEL && Game.getGame().getPhase() == Phase.GAME) {
			dwarf.giveProc(Dwarf.ProcType.GRAVEL_PROC);
		}
	}
}
