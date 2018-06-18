package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.blocks.timedblock.DataTimedBlock;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 27/02/18.
 */
class MagiIce extends AbstractMob {
	protected MagiIce(MonsterPlayer monster) {
		super(monster, MobType.ICE_MAGI);
	}
	
	@Update @Display private final Cooldown iceCD = new UseCooldown(30*20, this::makeIce);
	
	@Override
	public void update() {
		super.update();
		if (!isPlayerHoldingWeapon()) return;
		
		Location eyes = monster.getEyeLocation();
		Misc.spawnRangedParticles(eyes, Particle.FIREWORKS_SPARK, 2, 0.5, 0.5, 0.5);
		
		// Place ice
		Location center = monster.getLocation();
		Block block = Misc.randomLocation(center, 3, 3, 3).getBlock();
		tryCreateIce(block, 10*20);
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && isPlayerHoldingWeapon()) {
			iceCD.tryUse();
		}
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		
		if (damage.getType().isArrow()) {
			damage.cancel();
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		
		Dwarf receiver = damage.getReceiver();
		damage.addPostDamageHandler(() -> {
			receiver.givePotionEffect(PotionEffectType.SLOW, 5*20, 3, true, false, false);
			receiver.playSound("entity.player.attack.sweep", 1f, 0.5f, true);
		});
	}
	
	@Override
	protected void displayDeathAnimation() {
		monster.getWorld().spawnParticle(Particle.CLOUD, monster.getEyeLocation().subtract(0, 0.5, 0), 20, 0.5, 0.5, 0.5, 0.01);
		dropFakeWeapon();
		dropFakeItem("armour");
	}
	
	private void tryCreateIce(Block block, int lifetime) {
		if (BlockType.SOLID.matchesBlock(block)) {
			TimedBlock timed = new DataTimedBlock(lifetime, block, monster, Material.PACKED_ICE);
			BlockManager.getManager().placeTimedBlock(timed);
		}
	}
	
	private void makeIce() {
		Block center = monster.getLocation().getBlock();
		World world = center.getWorld();
		int xCenter = center.getX();
		int yCenter = center.getY();
		int zCenter = center.getZ();
		
		for (int x = xCenter - 7; x < xCenter + 7; x++) {
			for (int y = yCenter - 3; y < yCenter + 2; y++) {
				for (int z = zCenter - 7; z < zCenter + 7; z++) {
					Block block = world.getBlockAt(x, y, z);
					if (center.getLocation().distance(block.getLocation()) > 7) continue;
					
					tryCreateIce(block, 15*20);
				}
			}
		}
		
		monster.playSound("entity.stray.death", 1f, 0.5f, true);
		Misc.spawnRangedParticles(center.getLocation(), Particle.FIREWORKS_SPARK, 500, 3.5, 1, 3.5);
	}
	
}
