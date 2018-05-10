package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Arrow;
import org.bukkit.material.Dispenser;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 26/04/18.
 */
public class TurretBlock extends DataTimedBlock {
	
	private final Dwarf placer;
	private Location launchLocation;
	private final BlockFace initialFace;
	private final double damage;
	
	private final Cooldown faceRotater = new SimpleCooldown(10);
	
	public TurretBlock(int arrows, @NotNull Block block, @NotNull Dwarf placer, @NotNull BlockFace initialFace, double damage) {
		super(arrows*20, block, placer, Material.DISPENSER);
		this.placer = placer;
		this.initialFace = initialFace;
		this.damage = damage;
	}
	
	@Override
	public void update() {
		super.update();
		faceRotater.update();
		
		if (everyNTicks(20)) fireArrow();
	}
	
	@Override
	public boolean isPlaceable() {
		return super.isPlaceable() && !BlockType.LIQUID.matchesBlock(block);
	}
	
	@Override
	protected void setBlock() {
		super.setBlock();
		setBlockFace(initialFace);
		faceRotater.reset();
	}
	
	@Override
	public void placeBlock() {
		super.placeBlock();
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 20, 0.3, 0.4, 0.4, 0, new MaterialData(Material.ANVIL));
		world.playSound(center, Sound.BLOCK_METAL_PLACE, 1f, 1f);
		world.playSound(center, Sound.BLOCK_ANVIL_PLACE, 0.8f, 0.8f);
	}
	
	@Override
	public void unplaceBlock(boolean cancelled) {
		super.unplaceBlock(cancelled);
		if (cancelled) return;
		
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 15, 0.4, 0.4, 0.4, 0, block.getState().getData());
		world.playSound(center, Sound.BLOCK_STONE_PLACE, 1f, 1f);
	}
	
	@Override
	public void onHit(GamePlayer player, ClickType click, BlockFace blockFace) {
		super.onHit(player, click, blockFace);
		
		if (player != placer) return;
		if (!click.isRightClick()) return;
		if (!faceRotater.tryUse()) return;
		setBlockFace(blockFace);
		
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 5, 0.4, 0.4, 0.4, 0, block.getState().getData());
		world.playSound(center, Sound.BLOCK_STONE_PLACE, 1f, 1f);
	}
	
	private void setBlockFace(BlockFace face) {
		BlockState state = block.getState();
		Dispenser dispenser = ((Dispenser) state.getData());
		dispenser.setFacingDirection(face);
		state.setData(dispenser);
		state.update();
		
		Block adjacentBlock = block.getRelative(face);
		Vector offset = adjacentBlock.getLocation().subtract(block.getLocation()).toVector();
		
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		launchLocation = center.add(offset.clone().multiply(0.6));
		launchLocation.setDirection(offset);
	}
	
	private void fireArrow() {
		if (!placer.isOnline()) return;
		
		Arrow arrow = ArrowMisc.summonArrow(placer, launchLocation, damage, 3, 1f, 0.1f);
		ArrowMisc.addDamageModifier(arrow, gameDamage -> {
			gameDamage.setItemStack(ConsumableType.TURRET.getItemStack());
			if (gameDamage.getReceiver() instanceof AIEntity<?>) {
				gameDamage.instaKill();
			}
		});
		block.getWorld().playSound(block.getLocation(), "block.dispenser.dispense", 1f, 1f);
		block.getWorld().playSound(block.getLocation(), "block.dispenser.launch", 1f, 1f);
	}
}
