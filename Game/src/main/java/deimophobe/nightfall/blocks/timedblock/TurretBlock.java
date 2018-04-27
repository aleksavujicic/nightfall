package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Arrow;
import org.bukkit.material.Dispenser;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 26/04/18.
 */
public class TurretBlock extends DataTimedBlock {
	
	private final Dwarf placer;
	private final Location launchLocation;
	
	private final BlockFace face;
	
	private final double damage;
	
	public TurretBlock(int arrows, @NotNull Block block, @NotNull Dwarf placer, @NotNull BlockFace face, double damage) {
		super(arrows*20, block, placer, Material.DISPENSER);
		this.placer = placer;
		this.face = face;
		this.damage = damage;
		
		Block adjacentBlock = block.getRelative(face);
		Vector offset = adjacentBlock.getLocation().subtract(block.getLocation()).toVector();
		
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		this.launchLocation = center.add(offset.clone().multiply(0.7));
		launchLocation.setDirection(offset);
	}
	
	@Override
	protected void setBlock() {
		super.setBlock();
		BlockState state = block.getState();
		Dispenser dispenser = ((Dispenser) state.getData());
		dispenser.setFacingDirection(face);
		state.setData(dispenser);
		state.update();
	}
	
	@Override
	public void update() {
		super.update();
		
		if (everyNTicks(20)) fireArrow();
	}
	
	private void fireArrow() {
		Arrow arrow = ArrowMisc.summonArrow(placer, launchLocation, damage, 3, 1f, 0.1f);
		ArrowMisc.addDamageModifier(arrow, gameDamage -> {
			gameDamage.setItemStack(ConsumableType.TURRET.getItemStack());
		});
		block.getWorld().playSound(block.getLocation(), "block.dispenser.dispense", 1f, 1f);
		block.getWorld().playSound(block.getLocation(), "block.dispenser.launch", 1f, 1f);
	}
}
