package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.ai.AIEntity;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 28/10/17.
 */
class Rapier extends AbstractItem implements KitCooldownElement {
	
	private static final int STACK_CD_TIME = 5*20;
	private static final int INVINC_TIME = 2*20;
	private static final int MAX_STACKS = 80;
	private static final int MAX_AI_STACKS = 30;
	private static final int PARRY_COST = 10;
	
	private int inivincCD;
	private int stackCD;
	private int stacks;
	private double theta;
	
	Rapier(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.rapier", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() { return ITEM.createItemStack();}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (stackCD > 0)
			stackCD--;
		if (inivincCD > 0)
			inivincCD--;
		
		if (stackCD == 0) {
			if (stacks > 0)
				stacks--;
		}
		
		theta = (theta + 0.05) % (2 * Math.PI);
		
		Location playerLoc = dwarf.getPlayer().getEyeLocation();
		
		for (int i = 0; i < stacks; i++) {
			double frac = (double) i / MAX_STACKS;
			double red = (87d + frac * 148);
			double green = (99d - frac * 70);
			double blue = (237d - frac * 158);
			double myTheta = theta - frac * 2 * Math.PI;
			
			if (stacks == MAX_STACKS) {
				red = 220;
				green = 58;
				blue = 252;
			}
			red *= 1d/256;
			green *= 1d/256;
			blue *= 1d/256;
			
			Location particleLoc = playerLoc.clone().add(Math.cos(myTheta), -1, Math.sin(myTheta));
			particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 0, red, green, blue, 1);
		}
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromItem(damage)) {
			if (damage.getMonster() instanceof AIEntity) {
				damage.getDamage().timesMult(0.5);
				if (stacks < MAX_AI_STACKS)
					stacks++;
			} else {
				if (stacks < MAX_STACKS)
					stacks++;
			}
			
			damage.getDamage().addBoost(stacks/2);
			stackCD = STACK_CD_TIME;
		}
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (inivincCD > 0)
			damage.cancel();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isRightClick(action) && isHoldingItem()) {
			if (stacks >= PARRY_COST) {
				stacks -= PARRY_COST;
				dwarf.leap(2,0.5);
				dwarf.playSound("entity.zombie.attack_iron_door", 1f, 1.5f, true);
				inivincCD = INVINC_TIME;
				
				return true;
			}
		}
		return false;
	}
	
	@Override
	public float fractionComplete() {
		return (float) stacks/MAX_STACKS;
	}
}
