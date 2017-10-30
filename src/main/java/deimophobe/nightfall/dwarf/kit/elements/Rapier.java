package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterPlayer;
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
	private static final int MAX_STACKS = 20;
	private static final int PARRY_COST = 4;
	
	private int inivincCD;
	private int stackCD;
	private int stacks;
	private double theta;
	private ComplexCooldown leapCD = new ComplexCooldown(1*20, this::leap);
	
	Rapier(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.rapier", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() { return ITEM.createItemStack();}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	private static final double r1 = 10, g1 = 252, b1 = 234;
	private static final double r2 = 10, g2 = 47, b2 = 254;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (stackCD > 0)
			stackCD--;
		if (inivincCD > 0)
			inivincCD--;
		leapCD.update();
		
		if (stackCD == 0 && quartSec) {
			if (stacks > 0)
				stacks--;
		}
		
		theta = (theta + 0.05) % (2 * Math.PI);
		
		Location playerLoc = dwarf.getPlayer().getEyeLocation();
		
		for (int i = 0; i < stacks; i++) {
			double frac = (double) i / MAX_STACKS;
			double red = (r1 + frac * (r2 - r1));
			double green = (g1 + frac * (g2 - g1));
			double blue = (b1 + frac * (b2 - b1));
			double myTheta = theta - frac * 2 * Math.PI;
			
			if (stacks == MAX_STACKS) {
				red = 15;
				green = 20;
				blue = 256;
			}
			red *= 1d/256;
			green *= 1d/256;
			blue *= 1d/256;
			
			double r = 0.75*Math.pow(Math.sin(myTheta + theta),2);
			Location particleLoc = playerLoc.clone().add(r*Math.cos(myTheta), 0.5, r*Math.sin(myTheta));
			particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 0, red, green, blue, 1);
		}
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromItem(damage)) {
			MonsterEntity monster = damage.getMonster();
			if (monster instanceof MonsterPlayer) {
				if (!((MonsterPlayer) monster).hasSpawnProtection() && stacks < MAX_STACKS)
					stacks++;
			}
			
			damage.getDamage().addBoost(stacks*2);
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
			if (stacks >= PARRY_COST && leapCD.tryUse()) {
				stacks -= PARRY_COST;
				return true;
			}
		}
		return false;
	}
	
	private void leap() {
		dwarf.leap(1.5,0.5);
		dwarf.playSound("entity.zombie.attack_iron_door", 1f, 1.5f, true);
		inivincCD = INVINC_TIME;
	}
	
	@Override
	public float fractionComplete() {
		return (float) stacks/MAX_STACKS;
	}
}
