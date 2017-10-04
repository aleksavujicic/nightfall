package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

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
	
	/*
	private Location warpSpot;
	private boolean warping = false;
	private int cooldown = 0;
	
	private final static int TELEPORT_TIME = 20*20;
	private final static int MAX_COOLDOWN = 20*20;
	*/
	
	private final ComplexCooldown cooldown = new ComplexCooldown(40 * 20);
	private final Set<Arrow> activeArrows = new HashSet<>();
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		cooldown.update();
	}
	
	@Override
	public float fractionComplete() {
		return cooldown.fractionComplete();
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		if (cooldown.isAvailable() && isActive() && isActiveProjectile(proj)) {
			if (!GameMap.getCurrentMap().getCurrentMobProtection().continsEntity(proj)) {
				setActive(false);
				
				Location newSpot = proj.getLocation().add(0, 0.25, 0);
				newSpot.add(proj.getLocation().getDirection().multiply(0.25));
				newSpot.setDirection(dwarf.getLocation().getDirection());
				teleportTo(newSpot);
				
				cooldown.reset();
				
				activeArrows.remove(proj);
			}
		}
	}
	
	@Override
	protected void onToggle() {
		super.onToggle();
		removeActiveArrows();
	}
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		arrow = super.onBowFire(arrow, force);
		if (isActive()) {
			ArrowMisc.setGlowColour((Arrow) arrow, ChatColor.DARK_PURPLE);
			activeArrows.add((Arrow) arrow);
		}
		return arrow;
	}
	
	@Override
	protected boolean canActivate() {
		return cooldown.isAvailable();
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getAttacker() instanceof MonsterPlayer)
			removeActiveArrows();
	}
	
	private void removeActiveArrows() {
		for (Arrow arrow : activeArrows) {
			ArrowMisc.removeGlow(arrow);
			removeArrow(arrow);
		}
		activeArrows.clear();
	}
	
	private void teleportTo(Location location) {
		Location here = dwarf.getLocation();
		dwarf.getPlayer().setFallDistance(0);
		dwarf.teleportTo(location);
		
		World world = location.getWorld();
		world.spawnParticle(Particle.SPELL_WITCH, location, 20, 0.5, 0.5, 0.5);
		world.spawnParticle(Particle.SPELL_WITCH, here, 20, 0.5, 0.5, 0.5);
		world.playSound(location, "entity.illusion_illager.mirror_move", 1f, 0.95f);
		world.playSound(here, "entity.illusion_illager.mirror_move", 1f, 0.95f);
		
		
		Vector direction = location.clone().subtract(here).toVector();
		double distance = direction.length();
		Vector delta = direction.multiply(1 / distance);
		int times = (int) (distance / 1);
		
		Location partLoc = here.clone();
		for (int i = 0; i <= times; i++) {
			partLoc.add(delta);
			dwarf.getPlayer().getWorld().spawnParticle(Particle.END_ROD, partLoc, 1, 0.3, 0.3, 0.3, 0.03);
		}
	}
}
