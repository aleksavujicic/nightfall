package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class ShadowBlade extends AbstractItem implements CooldownPiece {
	public ShadowBlade(Dwarf dwarf){
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "shadowblade");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	private final ComplexCooldown soulShatterCD = new ComplexCooldown(10, this::soulShatter);
	
	private static final int SOUL_SHATTER_RADIUS = 3;
	private static final int MAX_SOULS = 50;
	private static final double AI_HIT = .5;
	private static final double AI_KILL = 1;
	private static final double MOB_KILL = 2.5;
	
	private double souls;
	
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		soulShatterCD.update();
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage){
		super.onDamageAttack(damage);
//		if (damage.getReceiver() instanceof AIEntity) {
//			damage.getMulitPartDamage().timesMult(1.5);
//			souls += AI_HIT;
//		} else {
//			souls++;
//		}
//
//		if (souls > MAX_SOULS) {
//			souls = MAX_SOULS;
//		}
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		super.onKill(damage);
		if (damage.getReceiver().isAI()) {
			souls += AI_KILL;
		}
		else {
			souls += MOB_KILL;
		}
		
		if (souls > MAX_SOULS) {
			souls = MAX_SOULS;
		}
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			if (souls > 2) {
				soulShatterCD.tryUse();
			}
		}
		return false;
	}
	
	private void soulShatter() {
		double kb = .15*souls;
		double SSarea = SOUL_SHATTER_RADIUS+.05*souls;
		
		Location center = dwarf.getLocation().add(dwarf.getLocation().getDirection().multiply(1.5));
		
		World world = center.getWorld();
		
		world.spawnParticle(Particle.DRAGON_BREATH, center, 30, SSarea, 0.5, SSarea, 0);
		world.spawnParticle(Particle.SMOKE_NORMAL, center, 30, SSarea, 0.5, SSarea, 0);
		
		for (MonsterEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
			if (entity.distanceTo(center) <= SSarea) {
				Vector offset = entity.getEyeLocation().subtract(center).toVector();
				
				Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(2, offset.length())) );
				knockback.setY(knockback.getY() / 2 + 0.1);
				
				entity.doDamage(dwarf, GameDamageType.SILENT_STRIKE, souls);
				entity.setVelocity(knockback);
			}
		}
		souls = 0;
	}
	
	@Override
	public float getCooldown() {
		return Math.min(1, (float) souls/MAX_SOULS);
	}
}
