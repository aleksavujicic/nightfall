package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.blocks.timedblock.GoboBox;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.damage.DamageManager;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
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

import java.util.Map;

/**
 * Created by Deimophobe on 28/02/17.
 */
class Goblin extends AbstractMob {

	protected Map<String, Integer> upgrades;

	private int supplies;

	protected Goblin(MonsterPlayer mons) {
		super(mons, MobType.GOBO);

		upgrades = monster.getUpgrades(MobType.GOBO);

		this.supplies = (upgrades.get("supplies") + upgrades.get("supplies-inf"))*2;
		int health = (upgrades.get("health") + upgrades.get("health-inf"))*2;
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
	}

	@Override
	public void onSpawn() {
		super.onSpawn();
		giveItem("gobo-box", (2+ supplies));
		giveItem("kaboom", 1);
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
			if ((monster.getTargetBlock(null, 5).getType() != Material.AIR) && (TimedBlock.placeTimedBlock(new GoboBox(block, 100, 5, monster)))) {
				monster.useHeldItem();
				placeBoxCD = MAX_PLACE_CD;
			}
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
		GameDamage damage = monster.createDamage(null, CustomDamageType.SELF_GOBO_KABOOM, 1000);
		damage.instaKill();
		damage.fire(true);
		
		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();

		BlockConverter.convert(BlockConverter.Type.EXPLOSION, loc, 8);
		world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 3, 1, 1, 1);
		world.playSound(loc, "entity.generic.explode", 2, 1);
		
		DamageManager.getManager().AOEDamage(DwarfManager.getManager().getDwarves(), monster,
				CustomDamageType.GOBO_KABOOM, loc, 6, 80, 4);
	}
	
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		kaboomCD = 0;
	}

	@Override
	public float getCooldown() {
		return (float)kaboomCD/MAX_KABOOM_CD;
	}
}
