package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.util.Buffpool;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HealerTotem extends AbstractItem implements CooldownPiece {
	
	private final ComplexCooldown healing = new ComplexCooldown(20, this::groupHeal);
	private final ComplexCooldown buffpoolCD = new ComplexCooldown(180*20, this::createBuffpool);
	
	public HealerTotem(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "totem");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isLeftClick(action)) {
			return buffpoolCD.tryUse();
		} else {
			return healing.tryUse();
		}
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		healing.update();
		buffpoolCD.update();
		
		if (activePool != null) {
			activePool.update();
			
			if (activePool.hasEnded()) {
				activePool = null;
			}
		}
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
	
	private Buffpool activePool;
	
	private static final Buffpool.Colour BUFFPOOL_COLOUR = new Buffpool.Colour(0.7, 0.2, 0.4);
	private void createBuffpool() {
		activePool = new Buffpool(dwarf, 20*20, 3, BUFFPOOL_COLOUR, 8, 3);
	}
	
	@Override
	public float getCooldown() {
		return buffpoolCD.getCooldown();
	}
	
}
