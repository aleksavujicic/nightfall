package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

class ShadowBlade extends AbstractItem implements KitCooldownElement {

	ShadowBlade(Dwarf dwarf){
		super(dwarf);
	}

	private final static CustomItem ITEM = DwarvenItems.getItem("sword.shadowblade", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() {
		return ITEM.createItemStack();
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	}

	private final ComplexCooldown cd = new ComplexCooldown(10*20);


	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		cd.update();
	}

	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			if (cd.tryUse()) {
				MonsterPlayer closestPlayerMonster = dwarf.getLookingAt(2.5, 13, MonsterManager.getManager().getAlivePlayerMobs());

				AIEntity closestAIMonster = dwarf.getLookingAt(2.5,13, AIManager.getManager().getAIs());
				dwarf.givePotionEffect(PotionEffectType.INVISIBILITY, 10*20,3,true,true,true);

				if (closestPlayerMonster != null) {
					Location monsterLoc = closestPlayerMonster.getLocation();

					Vector lookDir = monsterLoc.getDirection().setY(0);
					Location newLoc = monsterLoc.subtract(lookDir);
					closestPlayerMonster.doDamage(dwarf, CustomDamageType.SHADOW_STRIKE, 80, true);


					if (!newLoc.getBlock().getType().isSolid()) {
						dwarf.teleportTo(newLoc);
						dwarf.playSound("entity.endermen.teleport", 1, 1, true);

					}
				}
				else if (closestAIMonster != null) {
					Location monsterLoc = closestAIMonster.getLocation();

					Vector lookDir = monsterLoc.getDirection().setY(0);
					Location newLoc = monsterLoc.subtract(lookDir);
					closestAIMonster.doDamage(dwarf, CustomDamageType.SHADOW_STRIKE, 50, true);

					cd.reduceCooldown(5*20);

					if (!newLoc.getBlock().getType().isSolid()) {
						dwarf.teleportTo(newLoc);
						dwarf.playSound("entity.endermen.teleport", 1, 1, true);

					}
				}
			}
		}
		return false;
	}

	@Override
	public void onKill(MonsterDamage damage){
		cd.reduceCooldown(1*10);
	}

	@Override
	public float fractionComplete() {
		return cd.fractionComplete();
	}
}
