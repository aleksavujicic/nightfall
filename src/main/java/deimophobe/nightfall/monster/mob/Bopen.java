package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.event.block.Action;
import org.bukkit.util.Consumer;

/**
 * Created by Deimophobe on 15/03/17.
 */
public class Bopen extends AbstractMob {
	
	private static final Consumer<SkeletonHorse> HORSE_INITIALISER = (horse) -> {
		horse.setInvulnerable(true);
		horse.setTamed(true);
		horse.setJumpStrength(10);
		horse.setAdult();
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
		horse.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
	};
	
	private int cooldown = 0;
	private final static int MAX_CD = 100;
	
	private SkeletonHorse horse = null;
	
	protected Bopen(MonsterPlayer mons) {
		super(mons, MobType.BOPEN);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		mountHorse();
		giveItem("steed");
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		GameMap.getCurrentMap().stealGold(20);
	}
	
	@Override
	public void update() {
		super.update();
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float)cooldown/MAX_CD;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (cooldown == 0 && isPlayerHoldingItem("steed")) {
			mountHorse();
		}
	}
	
	@Override
	public void onShift(boolean sneak) {
		super.onShift(sneak);
		dismountHorse();
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		dismountHorse();
	}
	
	
	public boolean isRidingHorse() {
		return horse != null;
	}
	
	public SkeletonHorse getHorse() {
		return horse;
	}
	
	private void mountHorse() {
		if (!isRidingHorse()) {
			Location loc = monster.getLocation();
			horse = loc.getWorld().spawn(loc, SkeletonHorse.class, horze -> {
				horze.setInvulnerable(true);
				horze.setTamed(true);
				horze.setJumpStrength(10);
				horze.setAdult();
				horze.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
				horze.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
				
				horze.getInventory().setItem(0, getItem("saddle").createItemStack());
				horze.addPassenger(monster.getPlayer());
			});
		}
	}
	
	public void dismountHorse() {
		if (isRidingHorse()) {
			horse.remove();
			horse = null;
			cooldown = MAX_CD;
		}
	}
}
