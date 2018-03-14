package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Hammer extends AbstractAOEHitter implements CooldownPiece {
	
	public Hammer(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "hammer");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	private final ComplexCooldown cooldown = new ComplexCooldown(60*20, this::roar);
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		cooldown.update();
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getAttacker() instanceof AIEntity<?> && damage.getType() == GameDamageType.MELEE && isHoldingItem()) {
			damage.multiplyKnockback(0.5);
		}
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isRightClick(action)) {
			return cooldown.tryUse();
		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		return cooldown.getCooldown();
	}
	
	
	private static final double ROAR_RADIUS = 35;
	private void roar() {
		for (AIEntity ai : AIManager.getManager().getAIs()) {
			if (dwarf.distanceTo(ai) <= ROAR_RADIUS) {
				ai.setTarget(dwarf);
			}
		}
		dwarf.playSound("dragonroar", 1f, 1.4f, true);
		dwarf.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, 5*20, 10, true, false, true);
	}
	
	@Override
	protected double getDamageToMonster(MonsterEntity entity) {
		if (entity instanceof MonsterPlayer) {
			if (((MonsterPlayer) entity).getMob().getType() == MobType.ZOMBIE) {
				return 15;
			} else {
				return 10;
			}
		} else if (entity.isAI()) {
			return 20;
		}
		
		return 0;
	}
	
	@Override
	protected double getRadius(MonsterEntity entity) {
		return 3;
	}
}
