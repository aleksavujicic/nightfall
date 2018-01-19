package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.melee.AbstractAOEHitter;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 11/03/17.
 */
public class TuiHammer extends AbstractAOEHitter implements CooldownPiece {
	public TuiHammer(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "tuihammer");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.START;
	}
	
	@Override
	protected double getDamageToMonster(MonsterEntity entity) {
		if (entity instanceof MonsterPlayer) {
			if (((MonsterPlayer) entity).getMob().getType() == MobType.ZOMBIE) {
				return 25;
			} else {
				return 20;
			}
		} else if (entity instanceof AIEntity) {
			return 30;
		}
		
		return 0;
	}
	
	@Override
	protected double getRadius(MonsterEntity entity) {
		if (entity instanceof MonsterPlayer) {
			return 3;
		} else if (entity instanceof AIEntity) {
			return 4;
		}
		return 0;
	}
	
	
	private int cooldown;
	private static final int MAX_CD = 90 * 20;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (cooldown >0)
			cooldown--;
	}
	
	private final static double AI_RADIUS = 20;
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && cooldown == 0) {
			dwarf.sendMessage(ChatColor.GOLD + "ROAR!!!");
			
			if (Math.random() <= 0.001)
				dwarf.playSound("roar", 1, 1, true);
			else
				dwarf.playSound("dragonroar", 1, 1, true);
			
			dwarf.getPlayer().getWorld().spawnParticle(Particle.FLAME, dwarf.getLocation(), 200, 1, 1, 1, 0.1);
			dwarf.givePotionEffect(PotionEffectType.GLOWING, ProcType.ROAR.getDuration(), 1, true, false, true);
			dwarf.giveProc(ProcType.ROAR);
			
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
	
}
