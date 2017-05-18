package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Dagger extends AbstractCooldownItem {
	
	Dagger(Dwarf dwarf) {
		super(dwarf, 1200);
	}
	
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.dagger", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	
	@Override
	public void onKill(GameEntity monster, DamageType b) {
		reduceCooldown(200);
	}
	
	@Override
	public double onSelfHit(GameEntity monster, DamageType type, double damage) {
		monster.givePotionEffect(PotionEffectType.WITHER, 100, 1, true, false, true);
		return damage;
	}
	
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			MonsterPlayer closestMonster = dwarf.getLookingAt(2.5, 5, MonsterManager.getManager().getAlivePlayerMobs());
			
			if (closestMonster != null) {
				Location loc = closestMonster.getPlayer().getEyeLocation();
				
				closestMonster.customDamage(dwarf, DamageType.EVISCERATE, 200);
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
