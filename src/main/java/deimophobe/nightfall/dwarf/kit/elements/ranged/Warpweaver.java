package deimophobe.nightfall.dwarf.kit.elements.ranged;

import deimophobe.nightfall.util.ArrowMisc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Warpweaver extends AbstractToggleBow {
	public Warpweaver(Dwarf dwarf) {
		super(dwarf);
	}
	
	// Includes cost of fired arrow (so with ARROW_COST = 15,
	// this will drain 14 arrows on land as 1 arrow was used to fire).
	private final static int ARROW_COST = 15;
	
	private final static int POWER = 30;
	private final static CustomItem ITEM = getBow("warpweaver", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "WARPBOW";}
	@Override public int getPower() {return POWER;}
	
	private final Set<Arrow> activeArrows = new HashSet<>();
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		if (isActive()
		      && isActiveProjectile(proj)
		      && dwarf.hasArrows(ARROW_COST - 1)
		      && !GameMap.getCurrentMap().getCurrentMobProtection().continsEntity(proj)) {
			
			setActive(false);
			removeActiveArrows();
			dwarf.useArrows(ARROW_COST - 1);
			
			Location newSpot = proj.getLocation().add(0, 0.25, 0);
			newSpot.add(proj.getLocation().getDirection().multiply(0.25));
			newSpot.setDirection(dwarf.getLocation().getDirection());
			teleportTo(newSpot);
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
		
		// Note: dwarf does not use arrow until after fire event.
		if (!dwarf.hasArrows(ARROW_COST)) {
			setActive(false);
		}
		
		if (isActive()) {
			ArrowMisc.setGlowColour((Arrow) arrow, ChatColor.DARK_PURPLE);
			activeArrows.add((Arrow) arrow);
		}
		return arrow;
	}
	
	@Override
	protected boolean canActivate() {
		return dwarf.hasArrows(ARROW_COST);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getAttacker() instanceof MonsterPlayer) {
			dwarf.sendTitleMessage(ChatColor.DARK_PURPLE + "Warp interrupted by monster player!");
			removeActiveArrows();
		}
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
		dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, 5*20, 1, true, true, true);
		
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
			dwarf.getPlayer().getWorld().spawnParticle(Particle.END_ROD, partLoc, 1, 0, 0, 0, 0);
			dwarf.getPlayer().getWorld().spawnParticle(Particle.DRAGON_BREATH, partLoc, 3, 0.1, 0.1, 0.1, 0.01);
		}
	}
}
