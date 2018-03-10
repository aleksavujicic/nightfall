package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.melee.AbstractAOEHitter;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
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
	
	private final ComplexCooldown roarCD = new ComplexCooldown(90*20, this::roar);
	
	@Override
	protected double getDamageToMonster(MonsterEntity entity) {
		if (entity.isAI()) {
			return 30;
		} else {
			return 25;
		}
	}
	
	@Override
	protected double getRadius(MonsterEntity entity) {
		return 4;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		roarCD.update();
		if (roarCD.wasUsedWithin(ROAR_DURATION)) {
			dwarf.getPlayer().getWorld().spawnParticle(Particle.FLAME, dwarf.getEyeLocation(), 5, 0.4, 0.4, 0.4, 0.1);
		}
	}
	
	private final static double AI_RADIUS = 50;
	private final static int ROAR_DURATION = 10*20;
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			return roarCD.tryUse();
		}
		return false;
	}
	
	private void roar() {
		if (Math.random() <= 0.001)
			dwarf.playSound("roar", 1, 1, true);
		else
			dwarf.playSound("dragonroar", 1, 1, true);
		
		dwarf.getPlayer().getWorld().spawnParticle(Particle.FLAME, dwarf.getEyeLocation(), 200, 1, 1, 1, 0.1);
		dwarf.givePotionEffect(PotionEffectType.GLOWING, ROAR_DURATION, 1, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, ROAR_DURATION, 20, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.SPEED, ROAR_DURATION, 1, true, false, true);
		
		for (AIEntity ai : AIManager.getManager().getAIs()) {
			if (dwarf.getLocation().distance(ai.getLocation()) <= AI_RADIUS) {
				ai.setTarget(dwarf);
			}
		}
	}
	
	@Override
	public float getCooldown() {
		return roarCD.getCooldown();
	}
	
}
