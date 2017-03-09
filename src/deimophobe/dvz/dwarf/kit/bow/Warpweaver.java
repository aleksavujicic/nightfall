package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.Game;
import deimophobe.dvz.blocks.timedblock.LampBlock;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Warpweaver extends Bow {
	Warpweaver(Dwarf dwarf) {
		super(dwarf, BowType.WARPWEAVER);
	}
	
	private Location warpSpot;
	private boolean warping = false;
	private int cooldown = 0;
	
	private boolean active = false;
	
	private final static int TELEPORT_TIME = 20*20;
	private final static int MAX_COOLDOWN = 20*20;
	
	@Override
	public void update() {
		if (warping) {
			cooldown++;
			if (cooldown >= TELEPORT_TIME) {
				cooldown = MAX_COOLDOWN;
				teleportBack();
			}
		} else {
			if (cooldown > 0)
				cooldown--;
		}
	}
	
	@Override
	public float fractionComplete() {
		if (warping)
			return 1 - (float)cooldown/TELEPORT_TIME;
		else
			return 1 - (float)cooldown/MAX_COOLDOWN;
	}
	
	private final static String ARROW_METADATA_KEY = "warp";
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		if (canWarp() && proj.hasMetadata(ARROW_METADATA_KEY) && active) {
			warping = true;
			setActive(false);
			
			warpSpot = dwarf.getLocation();
			Location newSpot = proj.getLocation().add(0, 0.25, 0);
			
			dwarf.teleportTo(newSpot.setDirection(warpSpot.getDirection()));
			
			World world = warpSpot.getWorld();
			world.spawnParticle(Particle.SPELL_WITCH, warpSpot, 20, 0.5, 0.5, 0.5);
			world.spawnParticle(Particle.SPELL_WITCH, newSpot, 20, 0.5, 0.5, 0.5);
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (canWarp() && active) {
			arrow.setMetadata(ARROW_METADATA_KEY, new FixedMetadataValue(Game.getGame().getPlugin(), true));
		}
		return arrow;
	}
	
	@Override
	public boolean use(Action action) {
		if (DwarvenItem.isLeftClick(action) && canWarp()) {
			setActive(!active);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean matchesItem(ItemStack toMatch) {
		return (toMatch != null && toMatch.getType() == Material.BOW && toMatch.getDurability() == 6);
	}
	
	private void setActive(boolean setActive) {
		for (ItemStack item : dwarf.getPlayer().getInventory().getStorageContents()) {
			if (!matchesItem(item)) continue;
			
			if (setActive)
				item.addEnchantment(Enchantment.DURABILITY, 1);
			else
				item.removeEnchantment(Enchantment.DURABILITY);
		}
		dwarf.getPlayer().updateInventory();
		active = setActive;
	}
	
	private boolean canWarp() {
		return !warping && cooldown <= 0;
	}
	
	private void teleportBack() {
		warping = false;
		
		Location curSpot = dwarf.getLocation();
		dwarf.teleportTo(warpSpot);
		
		World world = warpSpot.getWorld();
		world.spawnParticle(Particle.SPELL_WITCH, warpSpot, 20, 0.5, 0.5, 0.5);
		world.spawnParticle(Particle.SPELL_WITCH, curSpot, 20, 0.5, 0.5, 0.5);
	}
}
