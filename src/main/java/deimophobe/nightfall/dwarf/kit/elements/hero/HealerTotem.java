package deimophobe.nightfall.dwarf.kit.elements.hero;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HealerTotem extends AbstractItem implements KitCooldownElement {
	
	private final ComplexCooldown healing = new ComplexCooldown(20, this::groupHeal);
	private final ComplexCooldown shield = new ComplexCooldown(2*60*20, this::shield);
	
	public HealerTotem(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "totem");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public ItemStack getCooldownToggleItem() { return null; }
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isLeftClick(action)) {
			return shield.tryUse();
		} else {
			return healing.tryUse();
		}
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		healing.update();
		shield.update();
	}
	
	private void groupHeal() {
		if (dwarf.tryUseMana(20)) {
			boolean healedDwarf = false;
			for (Dwarf target : DwarfManager.getManager().getGamePlayers()) {
				if (dwarf == target) continue;
				if (dwarf.distanceTo(target) > 15) continue;
					
				boolean canConnect = dwarf.canConnectToPlayer(target, 0.5,
						(location) -> location.getWorld().spawnParticle(Particle.HEART, location.subtract(0,1.2,0), 3, 0.1, 0.1, 0.1)
				);
				if (!canConnect) continue;
				
				healedDwarf = true;
				
				target.playSound("entity.experience_orb.pickup", 0.5f, 0.5f, false);
				
				dwarf.useMana(2);
				target.regenMana(15);
				target.heal(5);
				target.getArmour().repair(15);
			}
			
			if (healedDwarf) {
				dwarf.playSound("entity.experience_orb.pickup", 0.5f, 0.5f, false);
			}
		}
	}
	
	private void shield() {
		for (Dwarf target : DwarfManager.getManager().getGamePlayers()) {
			int amp = 10;
			if (target.hasKitElement(KitElementType.STRONG_ALE))
				amp = 3;
			
			target.givePotionEffect(PotionEffectType.ABSORPTION, 2*60*20, amp, true, false, true);
			target.playSound("block.enchantment_table.use", 10f, 0.5f, false);
		}
	}
	
	@Override
	public float fractionComplete() {
		return shield.fractionComplete();
	}
	
}
