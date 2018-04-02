package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.blocks.timedblock.GoboBox;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;
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

		int supplies_inf = upgrades.get("supplies-inf");
		this.supplies = (upgrades.get("supplies") + supplies_inf)*2;
		int health = (upgrades.get("health") + upgrades.get("health-inf"));
		
		this.dest = upgrades.get("dest");
		this.shrapnel = upgrades.get("shrapnel");
		this.force = upgrades.get("force-gobo");

		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");

		this.placeboxCD = new ComplexCooldown(MAX_PLACE_CD);
		this.throwboxCD = new ComplexCooldown(Math.max(MAX_THROW_CD - 5, MAX_THROW_CD - (int)(Math.log((double)supplies) / Math.log(2))));
	}

	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		giveItem("gobo-box", (2+ supplies));
	}

	@Override
	public void update() {
		super.update();
		placeboxCD.update();
		throwboxCD.update();
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();

		if (click.isRightClick() && isPlayerHoldingItem("gobo-box") && placeboxCD.isAvailable() && clickedBlock != null && clickedBlock.getType() != Material.ENDER_STONE) {
			Block block = clickedBlock.getRelative(blockFace);
			double damage = 40 + 6 * shrapnel;
			double power = 4.5 + 0.25 * dest;
			double kb = 0.4 + 0.04 * force;
			if ((monster.getTargetBlock(null, 5).getType() != Material.AIR) && (TimedBlock.placeTimedBlock(new GoboBox(block, 100, damage, power, kb, monster)))) {
				monster.useHeldItem();
				placeboxCD.reset();
			}
		}
		// Throw gobo box
		if (click.isLeftClick() && isPlayerHoldingItem("gobo-box") && (monster.getHeldItem().getAmount() >= 2) && throwboxCD.isAvailable()) {

			Vector direction = monster.getEyeLocation().getDirection();
			direction.setX((direction.getX() / 1.65));
			direction.setY(0.4);
			direction.setZ((direction.getZ() / 1.65));
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
		double damage = 40 + 6 * shrapnel;
		int armorShred = 10 + 4 * shrapnel;
		double power = 4.5 + 0.25 * dest;
		double kb = 1 + 0.1 * force;

		BlockConverter.convert(BlockConverter.Type.THROWNEXPLOSION, centerLoc, power);
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();

			double range = 5.5;
            double offlength = offset.length();
            if (offlength > range) continue;

            Vector knockback = offset.normalize().multiply(kb * (1 - offlength / range));
			knockback.setY(knockback.getY() / 2 + 0.3);

			DwarfDamage aoeDamage = dwarf.createDamage(this.monster, GameDamageType.GOBO_BOX_EXPLOSION, damage);
			aoeDamage.setKnockback(knockback);
			aoeDamage.setArmourShred(armorShred);
			aoeDamage.fire();
		}
	}
}
