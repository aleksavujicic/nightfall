package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.blocks.timedblock.GoboBox;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.damage.DamageManager;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.items.CustomItem;
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
	private boolean kaboom;
	private boolean kaboomTrigger;
	private int pick;
	private int dest;
	private int shrapnel;
	private int force;
	private int speed;
	private int superKaboom;

	private Cooldown placeboxCD;
	private Cooldown throwboxCD;
	private Cooldown kaboomCD;

	private static final int MAX_PLACE_CD = 10;
	private static final int MAX_THROW_CD = 20;
	private static final int MAX_KABOOM_CD = 40;

	protected Goblin(MonsterPlayer mons) {
		super(mons, MobType.GOBO);

		upgrades = monster.getUpgrades(MobType.GOBO);

		this.supplies = (upgrades.get("supplies") + upgrades.get("supplies-inf"))*2;
		int health = (upgrades.get("health") + upgrades.get("health-inf"));
		if (upgrades.get("kaboom") == 1) {
			this.kaboom = true;
			kaboomCD = new ComplexCooldown(MAX_KABOOM_CD);
			kaboomCD.reset();
		} else {
			this.kaboom = false;
			kaboomCD = new DudCooldown();
		}
		this.kaboomTrigger = false;

		this.pick = upgrades.get("pick");
		this.dest = upgrades.get("dest");
		this.shrapnel = upgrades.get("shrapnel");
		this.force = upgrades.get("force");
		this.speed = upgrades.get("speed");
		this.superKaboom = upgrades.get("superkaboom");

		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
		getArmour().addModifier(ItemModifierType.SPEED, (speed * 10), "Upgrade");

		this.placeboxCD = new ComplexCooldown(MAX_PLACE_CD);
		this.throwboxCD = new ComplexCooldown(MAX_THROW_CD);

	}

	@Override
	public void onSpawn() {
		super.onSpawn();
		giveItem("gobo-box", (2+ supplies));
		if (kaboom) {
			giveItem("kaboom", 1);
		}
		if (pick > 0) {
			CustomItem item = getItem("wood-pickaxe").clone();
			item.addModifier(ItemModifierType.EFFICIENCY, (pick - 1), "Pick Upgrade");
			monster.giveItem(item);
		}
	}

	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		placeboxCD.update();
		throwboxCD.update();
		if (kaboomTrigger) {
			kaboomCD.update();
			if (kaboomCD.isAvailable()) {
				kaboom();
			}
		}
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();

		if (Misc.isRightClick(action) && isPlayerHoldingItem("gobo-box") && placeboxCD.isAvailable() && clickedBlock != null && clickedBlock.getType() != Material.ENDER_STONE) {
			Block block = clickedBlock.getRelative(blockFace);
			double damage = 40 + 2 * shrapnel;
			double power = 4.5 + 0.25 * dest;
			double kb = 2.5 + 0.15 * force;
			if ((monster.getTargetBlock(null, 5).getType() != Material.AIR) && (TimedBlock.placeTimedBlock(new GoboBox(block, 100, damage, power, kb, monster)))) {
				monster.useHeldItem();
				placeboxCD.reset();
			}
		}
		// Throw gobo box
		if (Misc.isLeftClick(action) && isPlayerHoldingItem("gobo-box") && (monster.getHeldItem().getAmount() >= 2) && throwboxCD.isAvailable()) {

			Vector direction = monster.getEyeLocation().getDirection();
			direction.setX((direction.getX() / 1.8));
			direction.setY(0.4);
			direction.setZ((direction.getZ() / 1.8));
			TNTPrimed tnt = monster.getLocation().getWorld().spawn(monster.getEyeLocation().add(direction), TNTPrimed.class);
			double damage = 40 + 2 * shrapnel;
			int armorShred = 25 + 5 * shrapnel;
			double power = 4.5 + 0.25 * dest;
			double kb = 2.5 + 0.15 * force;
			tnt.setMetadata("thrower", new FixedMetadataValue(NightfallPlugin.getPlugin(), monster));
			tnt.setMetadata("damage", new FixedMetadataValue(NightfallPlugin.getPlugin(), damage));
			tnt.setMetadata("armorShred", new FixedMetadataValue(NightfallPlugin.getPlugin(), armorShred));
			tnt.setMetadata("power", new FixedMetadataValue(NightfallPlugin.getPlugin(), power));
			tnt.setMetadata("kb", new FixedMetadataValue(NightfallPlugin.getPlugin(), kb));
			tnt.setVelocity(direction);
			tnt.setFuseTicks(60);
			world.playSound(loc, "entity.firework.launch", 2, (float) 0.5);
			monster.useHeldItem();
			monster.useHeldItem();
			throwboxCD.reset();
		}

		if (Misc.isLeftClick(action) && isPlayerHoldingItem("kaboom") && !kaboomTrigger) {
			monster.givePotionEffect(PotionEffectType.SPEED, MAX_KABOOM_CD, speed, true, true, true);
			kaboomTrigger = true;
		}
	}

	private void kaboom() {
		GameDamage damage = monster.createDamage(null, CustomDamageType.SELF_GOBO_KABOOM, 1000);
		damage.instaKill();
		damage.fire(true);

		double dwarfDamage = 60 + 5 * shrapnel + 10 * superKaboom;
		int armorShred = 50 + 5 * shrapnel + 25 * superKaboom;
		double power = 6 + 0.5 * dest + 1.5 * superKaboom;
		double kb = 2.5 + 0.25 * force + 1.25 * superKaboom;

		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();

		BlockConverter.convert(BlockConverter.Type.EXPLOSION, loc, power);
		world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 3, 1, 1, 1);
		world.playSound(loc, "entity.generic.explode", 2, 1);
		
		DamageManager.getManager().DwarfAOEDamage(monster,
				CustomDamageType.GOBO_KABOOM, loc, 6 + superKaboom, dwarfDamage, kb, false, armorShred);
	}
	
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		monster.removePotionEffect(PotionEffectType.SPEED);
		kaboomCD.reset();
		kaboomTrigger = false;
	}

	@Override
	public float getCooldown() {
		return kaboomCD.fractionComplete();
	}
}
