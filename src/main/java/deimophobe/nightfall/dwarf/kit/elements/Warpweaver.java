package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Warpweaver extends AbstractToggleBow implements KitCooldownElement {
	Warpweaver(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 30;
	private final static CustomItem ITEM = DwarvenItems.getBow("warpweaver", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() {return ITEM.createItemStack(); }
	@Override public String getBowIdentifier() {return "WARPBOW";}
	@Override public int getPower() {return POWER;}
	
	private Location warpSpot;
	private boolean warping = false;
	private int cooldown = 0;
	
	private final static int TELEPORT_TIME = 20*20;
	private final static int MAX_COOLDOWN = 20*20;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
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
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		if (isActive() && isActiveProjectile(proj)) {
			warping = true;
			setActive(false);
			
			warpSpot = dwarf.getLocation();
			Location newSpot = proj.getLocation().add(0, 0.25, 0);
			newSpot.setDirection(warpSpot.getDirection());
			
			teleportTo(newSpot);
		}
	}
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		arrow = super.onBowFire(arrow, force);
		if (isActive()) {
			ArrowMisc.setGlowColour((Arrow) arrow, ChatColor.DARK_PURPLE);
		}
		return arrow;
	}
	
	@Override
	protected boolean canActivate() {
		return !warping && cooldown <= 0;
	}
	
	private void teleportBack() {
		warping = false;
		teleportTo(warpSpot);
	}
	
	private void teleportTo(Location location) {
		Location here = dwarf.getLocation();
		dwarf.teleportTo(location);
		
		World world = location.getWorld();
		world.spawnParticle(Particle.SPELL_WITCH, location, 20, 0.5, 0.5, 0.5);
		world.spawnParticle(Particle.SPELL_WITCH, here, 20, 0.5, 0.5, 0.5);
		world.playSound(location, "entity.illusion_illager.mirror_move", 1f, 0.95f);
		world.playSound(here, "entity.illusion_illager.mirror_move", 1f, 0.95f);
	}
}
