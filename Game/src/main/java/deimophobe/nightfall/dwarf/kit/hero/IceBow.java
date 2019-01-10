package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.dwarf.kit.ranged.AbstractBow;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 2/02/18.
 */
public class IceBow extends AbstractBow {
	public IceBow(Dwarf dwarf) {super(dwarf);}
	
	private final static int POWER = 80;
	private final static CustomItem ITEM = getBow("hero", "heranabow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getGiveType() { return PickupType.START; }
	@Override public String getBowIdentifier() {return "ICEBOW";}
	@Override public int getPower() {return POWER;}
	
	
	private static final float REQUIRED_FORCE = 0.9f;
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isRangedDamageFromBow(damage)) {
			float force = ArrowMisc.getArrowForce(damage.getArrow());
			if (force >= REQUIRED_FORCE) {
				arrowAOE(damage.getReceiver().getLocation(), damage.getReceiver());
			}
		}
	}
	
	@Override
	public Projectile onBowFire(Projectile proj, float force) {
		proj = super.onBowFire(proj, force);
		if (proj instanceof Arrow && force >= REQUIRED_FORCE) {
			Arrow arrow = (Arrow) proj;
			ArrowMisc.setGlowColour(arrow, ChatColor.DARK_AQUA);
		}
		return proj;
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock, BlockFace hitFace) {
		super.onProjectileLand(proj, hitBlock, hitFace);
		
		if (proj instanceof Arrow) {
			Arrow arrow = (Arrow) proj;
			float force = ArrowMisc.getArrowForce(arrow);
			if (force >= REQUIRED_FORCE) {
				arrowAOE(proj.getLocation(), null);
			}
		}
	}
	
	private static final BlockData WATER_1 = Material.LAPIS_BLOCK.createBlockData();
	private static final BlockData WATER_2 = Material.WATER.createBlockData();
	private static final BlockData WATER_3 = Material.LIGHT_BLUE_CONCRETE.createBlockData();
	private static final BlockData WATER_4 = Material.LIGHT_BLUE_CONCRETE_POWDER.createBlockData();
	
	private static final double AOE_RADIUS = 3.5;
	private static final double DISPLAY_RADIUS = 1.5;
	private void arrowAOE(Location location, MonsterEntity<?> exclude) {
		World world = location.getWorld();
		
		world.spawnParticle(Particle.BLOCK_CRACK, location, 3, DISPLAY_RADIUS, 0.3, DISPLAY_RADIUS, 0, WATER_1);
		world.spawnParticle(Particle.BLOCK_CRACK, location, 10, DISPLAY_RADIUS, 0.3, DISPLAY_RADIUS, 0, WATER_2);
		world.spawnParticle(Particle.BLOCK_CRACK, location, 20, DISPLAY_RADIUS, 0.3, DISPLAY_RADIUS, 0, WATER_3);
		world.spawnParticle(Particle.BLOCK_CRACK, location, 50, DISPLAY_RADIUS, 0.3, DISPLAY_RADIUS, 0, WATER_4);
		world.spawnParticle(Particle.WATER_DROP, location, 400, DISPLAY_RADIUS, 0.3, DISPLAY_RADIUS, 0);
		
		world.playSound(location, "entity.generic.swim", 1f, 0.6f);
		world.playSound(location, "entity.player.hurt_drown", 1f, 0.6f);
		
		for (MonsterEntity<?> monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
			if (monster == exclude) continue;
			if (monster.distanceTo(location) <= AOE_RADIUS) {
				monster.doDamage(dwarf, GameDamageType.WATER_BOW_AOE, 30);
			}
		}
		
		for (Dwarf d : DwarfManager.getManager().getDwarves()) {
			if (d.distanceTo(location) <= AOE_RADIUS) {
				Player player = d.getPlayer();
				if (player.getFireTicks() > 0) {
					player.setFireTicks(0);
					d.playSound("entity.generic.extinguish_fire");
				}
			}
		}
	}
}
