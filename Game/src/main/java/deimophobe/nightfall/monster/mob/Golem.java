package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.NFBlocks;
import deimophobe.nightfall.blocks.blocktype.BlockMatcher;
import deimophobe.nightfall.blocks.blocktype.BlockSet;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Golem extends AbstractMob {
	private static final BlockMatcher UNBREAKABLE_BLOCKS = new BlockSet(
		NFBlocks.UNBREAKABLE_BLOCKS,
		NFBlocks.LIQUID
	).orOfMaterial(
		Material.AIR,
		Material.BARRIER,
		Material.BEDROCK
	);
	
	@Update
	private final Cooldown breakCD = new SimpleCooldown(10);
	
	Golem(MonsterPlayer monster) {
		super(monster, MobType.GOLEM);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		monster.givePermanentPotionEffect(PotionEffectType.SLOW_DIGGING, 4);
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isLeftClick() && isPlayerHoldingWeapon() && breakCD.isAvailable()) {
			
			breakCD.reset();
			swingArms();
			
			Location smokeLoc = clickedBlock.getLocation().add(0.5, 0.5, 0.5);
			monster.getPlayer().spawnParticle(Particle.SMOKE_NORMAL, smokeLoc, 15, 0, 0.25, 0, 0.05);
			if (!UNBREAKABLE_BLOCKS.matchesBlock(clickedBlock)) {
				BlockManager.getManager().breakBlock(clickedBlock);
			}
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		breakCD.reset();
		swingArms();
	}
	
	@Override
	public void update() {
		super.update();
		if (everyNthTick(10*20) && monster.isSneaking()) {
			monster.setEntityStatus((byte) 11);
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		// Show/hide rose
		if (sneaking) {
			monster.setEntityStatus((byte) 11);
		} else {
			monster.setEntityStatus((byte) 34);
		}
	}
	
	private void swingArms() {
		monster.playSound("entity.generic.explode", 0.8f, 0.5f, true);
		
		// Show fancy hand animation
		monster.setEntityStatus((byte) 4);
	}
}
