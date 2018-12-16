package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.blocks.timedblock.DataTimedBlock;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 27/02/18.
 */
class MagiIce extends AbstractMob {
	protected MagiIce(MonsterPlayer monster) {
		super(monster, MobType.ICE_MAGI);
	}
	
	@Update @Display private final Cooldown iceCD = new UseCooldown(45*20, this::makeIce);
	@Update private final Cooldown leftClickIce = new SimpleCooldown(20);
	
	@Override
	public void update() {
		super.update();
		if (!isPlayerHoldingWeapon()) return;
		
		if (everyNthTick(5)) {
			Location eyes = monster.getEyeLocation();
			Misc.spawnRangedParticles(eyes, Particle.FIREWORKS_SPARK, 2, 0.5, 0.5, 0.5);
			
			// Place ice
			if (Math.random() > 0.5) {
				Location center = monster.getLocation();
				Block block = Misc.randomLocation(center, 3, 3, 3).getBlock();
				tryCreateIce(block, 10 * 20, false);
			}
		}
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && isPlayerHoldingWeapon()) {
			iceCD.tryUse();
		}
		if (click.isLeftClick() && clickedBlock != null && isPlayerHoldingWeapon()) {
			if (leftClickIce.isAvailable()) {
				boolean created = tryCreateIce(clickedBlock, 10 * 20, false);
				if (created) {
					playSound("punch-ice");
					Location center = clickedBlock.getLocation().add(0.5, 0.5, 0.5);
					Misc.spawnRangedParticles(center, Particle.FIREWORKS_SPARK, 10, 0.5, 0.5, 0.5);
					leftClickIce.reset();
				}
			}
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		
		Dwarf receiver = damage.getReceiver();
		damage.addPostDamageHandler(() -> {
			receiver.givePotionEffect(PotionEffectType.SLOW, 5*20, 3, true, false, false);
		});
	}
	
	@Override
	protected void displayDeathAnimation() {
		monster.getWorld().spawnParticle(Particle.CLOUD, monster.getEyeLocation().subtract(0, 0.5, 0), 20, 0.5, 0.5, 0.5, 0.01);
		dropFakeWeapon();
		dropFakeItem("armour");
	}
	
	private boolean tryCreateIce(Block block, int lifetime, boolean canFreezeWater) {
		boolean isSolid = NFBlocks.SOLID.matchesBlock(block);
		boolean isWater = NFBlocks.WATER.matchesBlock(block);
		
		if (isSolid || (isWater && canFreezeWater)) {
			TimedBlock timed = new IceBlock(lifetime, block, isSolid);
			return BlockManager.getManager().placeTimedBlock(timed);
		}
		return false;
	}
	
	private void makeIce() {
		Block center = monster.getLocation().getBlock();
		World world = center.getWorld();
		int xCenter = center.getX();
		int yCenter = center.getY();
		int zCenter = center.getZ();
		
		int range = 8;
		
		for (IceBlock iceBlock : BlockManager.getManager().getTimedBlocks(IceBlock.class)) {
			if (center.getLocation().distance(iceBlock.getBlock().getLocation()) > range) continue;
			
			iceBlock.cancel();
		}
		
		for (int x = xCenter - range; x < xCenter + range; x++) {
			for (int y = yCenter - range; y < yCenter + range; y++) {
				for (int z = zCenter - range; z < zCenter + range; z++) {
					Block block = world.getBlockAt(x, y, z);
					if (center.getLocation().distance(block.getLocation()) > range) continue;
					
					tryCreateIce(block, 15*20, true);
				}
			}
		}
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (monster.distanceTo(dwarf) > range) continue;
			dwarf.givePotionEffect(PotionEffectType.SLOW, 10*20, 6, true ,false, true);
		}
		
		monster.playSound("entity.stray.death", 1f, 0.5f, true);
		Misc.spawnRangedParticles(center.getLocation(), Particle.FIREWORKS_SPARK, 150, 3.5, 1, 3.5);
	}
	
	
	private static final BlockData SOLID_ICE = Material.PACKED_ICE.createBlockData();
	private static final BlockData WATER_ICE = Material.ICE.createBlockData();
	private class IceBlock extends DataTimedBlock {
		
		public IceBlock(int lifeTime, Block block, boolean solid) {
			super(lifeTime, block, monster, (solid? SOLID_ICE : WATER_ICE));
		}
	}
	
}
