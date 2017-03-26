package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Game;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.hero.Hero;
import deimophobe.dvz.dwarf.kit.KitCooldownElement;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.monster.ai.AIManager;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 11/03/17.
 */
class TuiHammer extends AbstractAOEHitter implements KitCooldownElement {
	TuiHammer(Dwarf dwarf) {
		super(dwarf, 4);
	}
	
	private final static ItemStack ITEM = DwarfManager.getManager().getItem("hero.tuihammer", Slot.MAIN_HAND);
	
	@Override
	public ItemStack getItem() {
		return ITEM;
	}
	
	@Override
	public KitGiveType getGiveType() {
		return KitGiveType.START;
	}
	
	@Override
	protected double getDamageToMonster(GameEntity entity) {
		if (entity instanceof MonsterPlayer) {
			return (dwarf.hasProc() ? 20 : 10);
		} else if (entity instanceof AIEntity) {
			return (dwarf.hasProc() ? 80 : 40);
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
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && cooldown == 0) {
			dwarf.sendMessage(ChatColor.GOLD + "ROAR!!!");
			dwarf.giveProc(Dwarf.ProcType.ROAR);
			
			for (AIEntity ai : AIManager.getManager().getAIs()) {
				if (dwarf.getLocation().distance(ai.getLocation()) <= AI_RADIUS) {
					ai.setTarget(dwarf);
				}
			}
			
			cooldown = MAX_CD;
		}
	}
	
	@Override
	public float fractionComplete() {
		return 1 - ((float)cooldown/MAX_CD);
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return getItem();
	}
}
