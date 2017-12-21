package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.blocks.timedblock.GoboBox;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.damage.DamageModifier;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
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
public class Goblin extends AbstractMob {

	protected Map<String, Integer> upgrades;

	private final int supplies;
	
	protected final int dest;
	protected final int shrapnel;
	protected final int force;

	private Cooldown placeboxCD;
	private Cooldown throwboxCD;

	private static final int MAX_PLACE_CD = 10;
	private static final int MAX_THROW_CD = 20;
	
	
	Goblin(MonsterPlayer mons) {
		this(mons, MobData.getMobData("gobo"));
	}
	
	protected Goblin(MonsterPlayer mons, MobData data) {
		super(mons, MobType.GOBO, data);

		upgrades = monster.getUpgrades(MobType.GOBO);

		this.supplies = (upgrades.get("supplies") + upgrades.get("supplies-inf"))*2;
		int health = (upgrades.get("health") + upgrades.get("health-inf"));
		
		this.dest = upgrades.get("dest");
		this.shrapnel = upgrades.get("shrapnel");
		this.force = upgrades.get("force-gobo");

		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");

		this.placeboxCD = new ComplexCooldown(MAX_PLACE_CD);
		this.throwboxCD = new ComplexCooldown(MAX_THROW_CD);
	}

	@Override
	public void onSpawn() {
		super.onSpawn();
		giveItem("gobo-box", (2+ supplies));
	}

	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (halfSec) {
			monster.removePotionEffect(PotionEffectType.WITHER);
			monster.removePotionEffect(PotionEffectType.POISON);
		}
		placeboxCD.update();
		throwboxCD.update();
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();

		if (Misc.isRightClick(action) && isPlayerHoldingItem("gobo-box") && placeboxCD.isAvailable() && clickedBlock != null && clickedBlock.getType() != Material.ENDER_STONE) {
			Block block = clickedBlock.getRelative(blockFace);
			double damage = 40 + 2 * shrapnel;
			double power = 4.5 + 0.25 * dest;
			double kb = 0.3 + 0.02 * force;
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
			tnt.setMetadata("thrower", new FixedMetadataValue(NightfallPlugin.getPlugin(), this));
			tnt.setVelocity(direction);
			tnt.setFuseTicks(60);
			world.playSound(loc, "entity.firework.launch", 2, 0.5f);
			monster.useHeldItem();
			monster.useHeldItem();
			throwboxCD.reset();
		}
	}

	public void thrownGoboBox(Location centerLoc) {
		double damage = 40 + 2 * shrapnel;
		int armorShred = 10 + 5 * shrapnel;
		double power = 4.5 + 0.25 * dest;
		double kb = 0.5 + 0.06 * force;

		BlockConverter.convert(BlockConverter.Type.THROWNEXPLOSION, centerLoc, power);
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
			if (offset.length() > 5) continue;

			DamageModifier modifier = new DamageModifier();

			Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(2, offset.length())) );
			knockback.setY(knockback.getY() / 2 + 0.1);
			modifier.addKnockback(knockback);

			DwarfDamage aoeDamage = dwarf.createDamage(this.monster, CustomDamageType.GOBO_BOX_EXPLOSION, damage);
			modifier.applyToDamage(aoeDamage);
			aoeDamage.setArmourShred(armorShred);
			aoeDamage.fire(true);
		}
	}
}
