package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 15/03/17.
 */
public class Bopen extends Mob {
	private int cooldown = 0;
	private final static int MAX_CD = 100;
	
	private SkeletonHorse horse = null;
	
	private static final ItemStack SADDLE;
	static {
		SADDLE = new ItemStack(Material.SADDLE);
	}
	
	protected Bopen(MonsterPlayer mons) {
		super(mons, MobType.BOPEN);
		mountHorse();
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		return damage/8;
	}
	
	@Override
	public void update() {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float)cooldown/MAX_CD;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (cooldown == 0 && isPlayerHoldingItem(1)) {
			mountHorse();
		}
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
			horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
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
