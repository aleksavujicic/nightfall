package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/03/17.
 */
public class Bopen extends AbstractTypedMob {
	
	@Override protected MobType getType() {return MobType.BOPEN;}
	
	private int cooldown = 0;
	private final static int MAX_CD = 100;
	
	private SkeletonHorse horse = null;
	
	private static final ItemStack SADDLE;
	static {
		SADDLE = new ItemStack(Material.SADDLE);
	}
	
	protected Bopen(MonsterPlayer mons) {
		super(mons);
	}
	
	@Override
	public void spawn() {
		super.spawn();
		mountHorse();
		giveItem("steed");
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		ShrineManager.getManager().stealGold(20);
		return damage;
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		return damage/8;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float)cooldown/MAX_CD;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (cooldown == 0 && isPlayerHoldingItem("steed")) {
			mountHorse();
		}
	}
	
	@Override
	public void onShift(boolean sneak) {
		dismountHorse();
	}
	
	@Override
	public void onDeath() {
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
			horse = (SkeletonHorse) loc.getWorld().spawnEntity(loc, EntityType.SKELETON_HORSE);
			horse.setInvulnerable(true);
			horse.setTamed(true);
			horse.setJumpStrength(10);
			horse.setAdult();
			horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
			horse.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
			horse.addPassenger(monster.getPlayer());
			horse.getInventory().setItem(0, SADDLE);
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
