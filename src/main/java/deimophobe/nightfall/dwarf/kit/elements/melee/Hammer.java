package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Hammer extends AbstractAOEHitter implements KitCooldownElement {
	
	public Hammer(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "hammer");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	private final ComplexCooldown cooldown = new ComplexCooldown(40*20, this::roar);
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		cooldown.update();
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
	public float fractionComplete() {
		return cooldown.fractionComplete();
	}
	
	
	private static final double ROAR_RADIUS = 20;
	private void roar() {
		for (AIEntity ai : AIManager.getManager().getAIs()) {
			if (dwarf.distanceTo(ai) <= ROAR_RADIUS) {
				ai.setTarget(dwarf);
			}
		}
		dwarf.playSound("dragonroar", 1f, 1.5f, true);
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return getItem().createItemStack();
	}
	
	@Override
	protected double getDamageToMonster(MonsterEntity entity) {
		if (entity instanceof MonsterPlayer) {
			if (((MonsterPlayer) entity).getMob().getType() == MobType.ZOMBIE) {
				return 20;
			} else {
				return 10;
			}
		} else if (entity instanceof AIEntity) {
			return 25;
		}
		
		return 0;
	}
	
	@Override
	protected double getRadius(MonsterEntity entity) {
		return 3;
	}
}
