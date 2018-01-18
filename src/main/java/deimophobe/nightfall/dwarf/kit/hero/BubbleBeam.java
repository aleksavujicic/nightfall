package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.entity.MonsterEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class BubbleBeam extends AbstractItem {
	
	public BubbleBeam(Dwarf dwarf) { super(dwarf); }
	
	private final ComplexCooldown beamer = new ComplexCooldown(10, this::shootBeam);
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero","bubblebeam");
	private final static double DAMAGE = 15;
	static { ITEM.addModifier(ItemModifierType.ATTACK, (int) DAMAGE); }
	
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	//@Override public ItemStack getCooldownToggleItem() { return null; }
	
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		beamer.update();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		
		if (Misc.isLeftClick(action)) {
			return beamer.tryUse();
		}
		return false;
	}
	
	
	private static final Consumer<Location> PARTICLE_PLACER = (location) -> {
//		double dx = Misc.randomDouble(-0.1,0.1);
//		double dy = Misc.randomDouble(-0.1,0.1);
//		double dz = Misc.randomDouble(-0.1,0.1);
//
//		for (int i=0; i<2; i++)
		location.getWorld().spawnParticle(Particle.WATER_BUBBLE, location, 5, 0.05, 0.05, 0.05, 0);
		location.getWorld().spawnParticle(Particle.CRIT_MAGIC, location, 1, 0.05, 0.05, 0.05, 0);
	};
	
	private final Consumer<MonsterEntity> DAMAGER = (monster) -> {
		MonsterDamage damage = (MonsterDamage) monster.createDamage(dwarf, CustomDamageType.SCEPTER_OF_MAGMA, DAMAGE + dwarf.getBonusMeleeDamage()/2);
		if (dwarf.hasProc()) damage.setProc(true);
		damage.setNoDmgTicks(1);
		damage.fire(true);
		
		monster.givePotionEffect(PotionEffectType.SLOW, 5*20, 2, true, true, true);
	};
	
	private void shootBeam() {
		dwarf.fireHitscan(15, 1.25, 0.2, 0.2, PARTICLE_PLACER, null, DAMAGER);
	}
	
}
