package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 11/03/17.
 */
class TuiHammer extends AbstractAOEHitter {
	TuiHammer(Dwarf dwarf) {
		super(dwarf, SwordType.TUI_HAMMER, 20*20, 4);
	}
	
	@Override
	protected double getDamageToMonster(GameEntity entity) {
		if (entity instanceof MonsterPlayer) {
			return (dwarf.hasProc() ? 20 : 10);
		} else if (entity instanceof AIEntity) {
			return  (dwarf.hasProc() ? 80 : 40);
		}
		return 0;
	}
	
	private final static double AI_RADIUS = 20;
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			dwarf.sendMessage(ChatColor.GOLD + "ROAR!");
			dwarf.playSound("entity.enderdragon.growl", 1, 1, true);
			
			for (AIEntity ai : AIManager.getManager().getAIs()) {
				if (dwarf.getLocation().distance(ai.getLocation()) <= AI_RADIUS) {
					ai.setTarget(dwarf);
				}
			}
			
			resetCooldown();
		}
	}
	
	@Override
	public void playOffCDSound() {}
}
