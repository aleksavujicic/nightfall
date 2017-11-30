package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ShadowBlade extends AbstractItem implements KitCooldownElement {

	public ShadowBlade(Dwarf dwarf){
		super(dwarf);
	}

	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "shadowblade");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() {
		return ITEM.createItemStack();
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	}

	private final ComplexCooldown cd = new ComplexCooldown(60*20);
	private final ComplexCooldown invisPreventer = new ComplexCooldown(20, null, this::updateInvisibility);
	private boolean invisible = false;


	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		cd.update();
		invisPreventer.update();
		
		if (invisible && Math.random() <= 0.5) {
			dwarf.getWorld().spawnParticle(Particle.SMOKE_NORMAL, dwarf.getLocation().add(0,0.5,0), 5, 0.2, 0.5, 0.2, 0.03);
		}
	}

	@Override
	public void onDamageAttack(MonsterDamage damage){
		super.onDamageAttack(damage);
		resetInvisibility();
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		
		// Prevent attacks from ais if invisible
		if (invisible && damage.getAttacker() instanceof AIEntity) {
			((AIEntity) damage.getAttacker()).forceUpdateTarget();
			damage.cancel();
		}
		
		// Otherwise cancel invisibility
		if (damage.getAttacker() instanceof MonsterPlayer) {
			resetInvisibility();
		}
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			if (cd.isAvailable()) {
				MonsterPlayer closestPlayerMonster = dwarf.getLookingAt(2.5, 13, MonsterManager.getManager().getAlivePlayerMobs());

				AIEntity closestAIMonster = dwarf.getLookingAt(2.5,13, AIManager.getManager().getAIs());

				if (closestPlayerMonster != null) {
					Location monsterLoc = closestPlayerMonster.getLocation();

					Vector lookDir = monsterLoc.getDirection().setY(0);
					Location newLoc = monsterLoc.subtract(lookDir);

					if (!newLoc.getBlock().getType().isSolid()) {
						closestPlayerMonster.doDamage(dwarf, CustomDamageType.SHADOW_STRIKE, 100, true);
						dwarf.teleportTo(newLoc);
						dwarf.givePotionEffect(PotionEffectType.INVISIBILITY, 10*20,3,true,true,true);
						dwarf.playSound("entity.endermen.teleport", 1, 1, true);
						cd.reset();
					}
				}
				else if (closestAIMonster != null) {
					Location monsterLoc = closestAIMonster.getLocation();

					Vector lookDir = monsterLoc.getDirection().setY(0);
					Location newLoc = monsterLoc.subtract(lookDir);

					if (!newLoc.getBlock().getType().isSolid()) {
						closestAIMonster.doDamage(dwarf, CustomDamageType.SHADOW_STRIKE, 80, true,true);
						dwarf.teleportTo(newLoc);
						dwarf.givePotionEffect(PotionEffectType.INVISIBILITY, 10*20,3,true,true,true);
						dwarf.playSound("entity.endermen.teleport", 1, 1, true);
						cd.reset();
						cd.reduceCooldown(40*20);
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		updateInvisibility(sneaking);
	}
	
	private void resetInvisibility() {
		invisPreventer.reset();
		updateInvisibility();
	}
	
	private void updateInvisibility() {
		updateInvisibility(dwarf.isSneaking());
	}
	
	private void updateInvisibility(boolean sneaking) {
		Armour armour = dwarf.getArmour();
		
		if (sneaking && invisPreventer.isAvailable()) {
			if (armour instanceof DwarvenArmour) ((DwarvenArmour) armour).hideArmour();
			dwarf.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
			invisible = true;
			updateTargettingAIs();
		} else {
			if (armour instanceof DwarvenArmour) ((DwarvenArmour) armour).showArmour();
			dwarf.removePotionEffect(PotionEffectType.INVISIBILITY);
			invisible = false;
		}
	}
	
	private void updateTargettingAIs() {
		for (AIEntity aiEntity : AIManager.getManager().getAIs()) {
			if (aiEntity.getTarget() == dwarf.getPlayer()) {
				aiEntity.forceUpdateTarget();
			}
		}
	}
	
	@Override
	public float fractionComplete() {
		return cd.fractionComplete();
	}
}
