package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.Random;

/**
 * Created by Deimophobe on 8/07/17.
 */
final class Hellhound extends AbstractWolf {
	
	Hellhound(MonsterPlayer monster) {
		super(monster, MobType.HELLHOUND);
		getWeapon().addModifier(ItemModifierType.BURNING, 1, "Breath of Hell");
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		changeDisguiseWatcher(WolfWatcher.class, (ww) -> ww.setAngry(true));
	}
	
	@Override
	public void update(boolean a, boolean b, boolean sec, boolean d, boolean e) {
		super.update(a,b,sec,d,e);
		//if (sec)
			//tryPlaceMagmaBlock();
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		//tryPlaceMagmaBlock();
	}
	
	@Override
	protected float leapPitch() {
		return 0.85f;
	}
	
	private void tryPlaceMagmaBlock() {
		Random random = new Random();
		double dx = random.nextDouble()*6 - 3;
		double dy = random.nextDouble()*6 - 3;
		double dz = random.nextDouble()*6 - 3;
		Block block = monster.getLocation().add(dx, dy, dz).getBlock();
		if (block.getType().isSolid())
			TimedBlock.placeTimedBlock(new TimedBlock(block, Material.MAGMA, 140, monster));
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(org.bukkit.entity.Wolf.class, wolf -> {
			wolf.setSitting(monster.isSneaking());
			wolf.setTamed(false);
			wolf.setAngry(true);
			wolf.setTarget(monster.getPlayer());
		});
	}
}
