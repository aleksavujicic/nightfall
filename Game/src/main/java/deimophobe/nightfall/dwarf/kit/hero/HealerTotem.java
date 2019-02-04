package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.util.ArcaneMark;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HealerTotem extends AbstractItem implements CooldownPiece {
	
	private final ComplexCooldown healing = new ComplexCooldown(20, this::groupHeal);
	private final ComplexCooldown arcaneMarkCD = new ComplexCooldown(180*20, this::createMark);
	
	public HealerTotem(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "totem");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public PickupType getPickupType() {return PickupType.START;}
	
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isLeftClick()) {
			return arcaneMarkCD.tryUse();
		} else {
			return healing.tryUse();
		}
	}
	
	@Override
	public void update() {
		healing.update();
		arcaneMarkCD.update();
	}
	
	private void groupHeal() {
		if (dwarf.hasMana(20)) {
			boolean healedDwarf = false;
			for (Dwarf target : DwarfManager.getManager().getGamePlayers()) {
				if (dwarf == target) continue;
				if (dwarf.distanceTo(target) > 15) continue;
					
				boolean canConnect = dwarf.canConnectToPlayer(target, 0.5,
						(location) -> location.getWorld().spawnParticle(Particle.HEART, location.subtract(0,1.2,0), 3, 0.1, 0.1, 0.1)
				);
				if (!canConnect) continue;
				
				healedDwarf = true;
				
				target.playSound("healing", 0.5f, 0.8f, false);
				
				dwarf.useMana(2);
				target.regenMana(15);
				target.heal(5);
				target.getArmour().repair(15);
			}
			
			if (healedDwarf) {
				dwarf.playSound("healing", 0.5f, 0.8f, false);
				dwarf.useMana(20);
			}
		}
	}
	
	private void createMark() {
		dwarf.addUpdateable(
				new ArcaneMark(dwarf, ArcaneMark.Type.ARTHEA,20*20)
		);
	}
	
	@Override
	public float getCooldown() {
		return arcaneMarkCD.getCooldown();
	}
	
}
