package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Explosion;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.blocks.timedblock.GoboBox;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.Action;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 28/02/17.
 */
class Goblin extends AbstractMob {

	@Override
	public void onSpawn() {
		super.onSpawn();
		giveItem("gobo-box", 8);
		giveItem("kaboom", 1);
	}
	
	protected Goblin(MonsterPlayer mons) {
		super(mons, MobType.GOBO);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (placeBoxCD > 0)
			placeBoxCD--;

		if (throwBoxCD > 0)
			throwBoxCD--;

		if (kaboomCD > 0 && kaboomCD < MAX_KABOOM_CD) {
			kaboomCD++;
		}

		if (kaboomCD == MAX_KABOOM_CD) {
			kaboom();
		}
	}
	
	private static final int MAX_PLACE_CD = 10;
	private static final int MAX_THROW_CD = 20;
	private int placeBoxCD = 0;
	private int throwBoxCD = 0;
	
	private static final int MAX_KABOOM_CD = 40;
	private int kaboomCD = 0;
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();

		if (Misc.isRightClick(action) && isPlayerHoldingItem("gobo-box") && placeBoxCD == 0 && clickedBlock != null && clickedBlock.getType() != Material.ENDER_STONE) {
			Block block = clickedBlock.getRelative(blockFace);
			TimedBlock.placeTimedBlock(new GoboBox(block, 50, 5, monster));
			monster.useHeldItem();
			placeBoxCD = MAX_PLACE_CD;
		}
		// Throw gobo box
		if (Misc.isLeftClick(action) && isPlayerHoldingItem("gobo-box") && (monster.getHeldItem().getAmount() >= 2) && throwBoxCD == 0) {

			Vector direction = monster.getEyeLocation().getDirection();
			direction.setX((direction.getX() / 1.8));
			direction.setY(0.4);
			direction.setZ((direction.getZ() / 1.8));
			TNTPrimed tnt = monster.getLocation().getWorld().spawn(monster.getEyeLocation().add(direction), TNTPrimed.class);
			tnt.setMetadata("thrower", new FixedMetadataValue(NightfallPlugin.getPlugin(), monster));
			tnt.setVelocity(direction);
			tnt.setFuseTicks(60);
			world.playSound(loc, "entity.firework.launch", 2, (float) 0.5);
			monster.useHeldItem();
			monster.useHeldItem();
			throwBoxCD = MAX_THROW_CD;
		}

		if (Misc.isLeftClick(action) && isPlayerHoldingItem("kaboom") && kaboomCD == 0) {
			monster.givePotionEffect(PotionEffectType.SPEED, MAX_KABOOM_CD, 4, true, true, true);
			kaboomCD = 1;
		}
	}
	
	private void kaboom() {
		monster.customDamage(null, DamageType.KABOOM, 10000);
		
		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();

		BlockConverter.convert(BlockConverter.Type.EXPLOSION, loc, 8);
		world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 3, 1, 1, 1);
		world.playSound(loc, "entity.generic.explode", 2, 1);
		(new Explosion(monster, DwarfManager.getManager().getGamePlayers(), loc, DamageType.CUSTOM_EXPLOSION, 80, 6, 4)).explode();
	}

	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		kaboomCD = 0;
		return damage;
	}

	@Override
	public float getCooldown() {
		return (float)kaboomCD/MAX_KABOOM_CD;
	}
}
