package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitCooldownElement;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Hammer extends AbstractAOEHitter implements KitCooldownElement {
	
	Hammer(Dwarf dwarf) {
		super(dwarf, 2);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.hammer", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	private int cooldown;
	private static final int MAX_CD = 40;
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		cooldown = 0;
		return super.onHit(monster, type, damage);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (!dwarf.isBlocking()) {
			if (cooldown > 0)
				cooldown--;
		} else {
			cooldown++;
			if (cooldown > MAX_CD) cooldown = MAX_CD;
			
			if (cooldown == MAX_CD && quartSec) {
				dwarf.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
				dwarf.getArmour().repair(10);
				dwarf.regenMana(1);
				
				if (cooldown >= MAX_CD) cooldown = MAX_CD;
			}
		}
	}
	
	@Override
	public float fractionComplete() {
		return (float)cooldown/MAX_CD;
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return getItem().createItemStack();
	}
	
	@Override
	protected double getDamageToMonster(GameEntity entity) {
		if (entity instanceof MonsterPlayer) {
			return (dwarf.hasProc() ? 20 : 5);
		} else if (entity instanceof AIEntity) {
			return  (dwarf.hasProc() ? 40 : 20);
		}
		
		return 0;
	}
}
