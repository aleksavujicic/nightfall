package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Dagger extends AbstractCooldownItem {
	
	Dagger(Dwarf dwarf) {
		super(dwarf, 300*20);
	}
	
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.dagger", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	@Override
	public void onKill(MonsterDamage damage) {
		reduceCooldown(20);
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (itemCausedDamage(damage)) {
			damage.getMonster().givePotionEffect(PotionEffectType.WITHER, 100, 1, true, false, true);
		}
	}
	
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			MonsterPlayer closestMonster = dwarf.getLookingAt(2.5, 5, MonsterManager.getManager().getAlivePlayerMobs());
			
			if (closestMonster != null) {
				Location loc = closestMonster.getPlayer().getEyeLocation();
				
				closestMonster.doDamage(dwarf, CustomDamageType.EVISCERATE, 200, true);
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 20, 0.3, 0.3, 0.3, 1);
				//world.spigot().playEffect(loc, GameEffect.COLOURED_DUST, 0, 1, red, green, blue, 1, 0, 64);
				//world.spawnParticle(Particle.SPELL_INSTANT, ltarget.getEyeLocation(), 1, 0.3, 0.3, 0.3, 0);
				dwarf.playSound("entity.wither.shoot", 1f, 1.5f, true);
				resetCooldown();
			}
			return true;
		}
		return false;
	}
}
