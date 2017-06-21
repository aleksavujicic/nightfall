package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.ProcType;
import deimophobe.dvz.dwarf.kit.KitCooldownElement;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.monster.ai.AIManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.inventivetalent.glow.GlowAPI;

/**
 * Created by Deimophobe on 11/03/17.
 */
class TuiHammer extends AbstractAOEHitter implements KitCooldownElement {
	TuiHammer(Dwarf dwarf) {
		super(dwarf);
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
			return (dwarf.hasProc() ? 15 : 10);
		} else if (entity instanceof AIEntity) {
			return (dwarf.hasProc() ? 40 : 20);
		}
		return 0;
	}
	
	@Override
	protected double getRadius() {
		return  (dwarf.hasProc() ? 5 : 4);
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
			
			if (Math.random() <= 0.001)
				dwarf.playSound("roar", 1, 1, true);
			else
				dwarf.playSound("dragonroar", 1, 1, true);
			
			dwarf.getPlayer().getWorld().spawnParticle(Particle.FLAME, dwarf.getLocation(), 200, 1, 1, 1, 0.1);
			dwarf.setGlowing(ProcType.ROAR.getDuration(), GlowAPI.Color.GOLD);
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
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return getItem().createItemStack();
	}
}
