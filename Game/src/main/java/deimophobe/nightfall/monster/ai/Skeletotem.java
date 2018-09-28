package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.entity.GameEntityShooter;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 29/09/18.
 */
public class Skeletotem extends AIEntity<WitherSkeleton> implements GameEntityShooter<WitherSkeleton> {
	
	private static final double HEALTH = 50;
	
	private static final ItemStack BOW = ItemManager.getMiscItem("ai-bow").createItemStack();
	
	private static final Consumer<WitherSkeleton> INITIALISER = (skeleton) -> {
		AttributeModifier speedModifier = new AttributeModifier("speed", -100, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
		skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(speedModifier);
		skeleton.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.5);
		skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(HEALTH);
		skeleton.setHealth(HEALTH);
		
		skeleton.getEquipment().setItemInMainHand(BOW);
	};
	
	
	protected Skeletotem(Location location, String name, Dwarf target) {
		super(location, name, target, WitherSkeleton.class, INITIALISER);
	}
	
	@Override
	public boolean isBowInstaKillable() {
		return false;
	}
	
	
	@Override
	public void onDeath(MonsterDamage damage) {
		if (damage.getType() != GameDamageType.AI_REMOVER) {
			entity.getLocation().getWorld().playSound(getLocation(), "entity.witherskeleton.death", 1f, 0.6f);
		}
		super.onDeath(damage);
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		ArrowMisc.setArrowDamage(arrow, 25);
		ArrowMisc.setArrowForce(arrow, 1);
		arrow.setCritical(false);
		arrow.setKnockbackStrength(1);
		arrow.setFireTicks(0);
		
		faceTarget();
		
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
	}
	
	private void faceTarget() {
		Vector offset = getTarget().getLocation().subtract(this.getLocation()).toVector();
		entity.getLocation().setDirection(offset);
	}
}
