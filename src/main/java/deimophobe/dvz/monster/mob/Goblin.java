package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Misc;
import deimophobe.dvz.blocks.BlockConverter;
import deimophobe.dvz.blocks.timedblock.GoboBox;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 28/02/17.
 */
class Goblin extends AbstractTypedMob {

	@Override
	public void spawn() {
		super.spawn();
		giveItem("gobo-box", 8);
		giveItem("kaboom", 1);
	}

	@Override protected MobType getType() {return MobType.GOBO;}
	
	protected Goblin(MonsterPlayer mons) {
		super(mons);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (placeBoxCD > 0)
			placeBoxCD--;
		
		if (kaboomCD > 0) {
			kaboomCD++;
			
			if (kaboomCD == MAX_KABOOM_CD)
				kaboom();
		}
	}
	
	private static final int MAX_PLACE_CD = 10;
	private int placeBoxCD = 0;
	
	private static final int MAX_KABOOM_CD = 60;
	private int kaboomCD = 0;
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isPlayerHoldingItem("gobo-box") && placeBoxCD == 0 && clickedBlock != null) {
			Block block = clickedBlock.getRelative(blockFace);
			TimedBlock.placeTimedBlock(new GoboBox(block, 50, 5));
			monster.useHeldItem();
			placeBoxCD = MAX_PLACE_CD;
		}
		if (Misc.isLeftClick(action) && isPlayerHoldingItem("kaboom") && kaboomCD == 0) {
			monster.sendMessage("KAAAAAAAA");
			monster.givePotionEffect(PotionEffectType.SPEED, MAX_KABOOM_CD, 3, true, true, true);
			kaboomCD = 1;
		}
	}
	
	private void kaboom() {
		monster.sendMessage("BOOM");
		
		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();
		
		BlockConverter.convert(BlockConverter.Type.EXPLOSION, loc, 8);
		world.spawnParticle(Particle.EXPLOSION_LARGE, loc, 3, 1, 1, 1);
		world.playSound(loc, "entity.generic.explode", 2, 1);
		
		monster.customDamage(null, DamageType.KABOOM, 10000);
	}
	
	@Override
	public float getCooldown() {
		return (float)kaboomCD/MAX_KABOOM_CD;
	}
}
