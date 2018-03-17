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
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class Soulblade extends AbstractItem implements CooldownPiece {

	private final ComplexCooldown soulShatterCD = new ComplexCooldown(10, this::soulShatter);

	private static final double SOUL_SHATTER_RADIUS = 3.5;
	private static final int MAX_SOULS = 50;
	private static final double HIT = 1;

	private double souls = 0;

	public Soulblade(Dwarf dwarf){
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "soulblade");

	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	}

	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		soulShatterCD.update();
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isMeleeDamageFromItem(damage)) {
			souls += HIT;
		}
		soulCheck();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			if (souls > 10) {
				soulShatterCD.tryUse();
			}
		}
		return false;
	}
	
	private void soulShatter() {
		//Location center = dwarf.getLocation().add(dwarf.getLocation().getDirection().multiply(1.5));
		Location center = dwarf.getEyeLocation();
		Misc.moveLocation(center, 1, 0, -0.5);
		
		World world = center.getWorld();
		world.spawnParticle(Particle.DRAGON_BREATH, center, (int) souls, 0.5, 0.1, 0.5, 0.003 * souls);
		world.spawnParticle(Particle.SMOKE_NORMAL, center, 20, 0.5, 0.1, 0.5, 0.003 * souls);
		
		float pitch = 0.5f + (float) (0.006f * souls);
		world.playSound(center,"dash", 1f, pitch);
		world.playSound(center,"entity.generic.burn", 1f, pitch);
		
		double kb = 0.5 + 0.02 * souls;
		double area = SOUL_SHATTER_RADIUS;
		double baseDamage = souls * 4;
		
		souls = 0;
		
		for (MonsterEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
			if (entity.distanceTo(center) <= area) {
				Vector offset = entity.getEyeLocation().subtract(center).toVector();
				
				Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(2, offset.length())) );
				knockback.setY(knockback.getY() / 2 + 0.1);
				
				MonsterDamage mDamage = entity.createDamage(dwarf, GameDamageType.SILENT_STRIKE, baseDamage);
				if (entity instanceof AIEntity) {
					mDamage.getMultiPartDamage().addBoost(50);
				}
				mDamage.setKnockback(knockback);
				mDamage.fire(true);
			}
		}
	}
	
	private void soulCheck() {
		if (souls > MAX_SOULS) {
			souls = MAX_SOULS;
		}
	}
	
	@Override
	public float getCooldown() {
		return Math.min(1, (float) souls/MAX_SOULS);
	}
}
