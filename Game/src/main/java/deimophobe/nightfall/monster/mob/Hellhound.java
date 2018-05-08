package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

/**
 * Created by Deimophobe on 8/07/17.
 */
final class Hellhound extends AbstractWolf {
	
	Hellhound(MonsterPlayer monster) {
		super(monster, MobType.HELLHOUND);
		getWeapon().addModifier(ItemModifierType.BURNING, 2);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		changeDisguiseWatcher(WolfWatcher.class, (ww) -> ww.setAngry(true));
	}
	
	@Override
	public void update() {
		super.update();
		
		if (everyNthTick(20)) {
			Location location = monster.getLocation();
			location.getWorld().spawnParticle(Particle.FLAME, location, 5, 0.2, 0.2, 0.2, 0.05);
		}
	}
	
	@Override
	protected void leap() {
		super.leap();
		
		int successes = 0;
		monster.playSound("entity.ghast.shoot", 0.5f, 0.5f, true);
		monster.playSound("entity.ghast.shoot", 5f, 0.5f, false);
		for (int i = 0; i < 30; i++) {
			Block block = Misc.randomLocation(monster.getLocation(), 4, 3, 4).getBlock();
			if (BlockType.IGNITEABLE.matchesBlock(block)) {
				block.setType(Material.FIRE);
				successes++;
				
				if (successes >= 7) break;
			}
		}
		
		this.addUpdateable(new LifetimeExpireable(20) {
			@Override
			public void update() {
				super.update();
				Location location = monster.getLocation();
				location.getWorld().spawnParticle(Particle.FLAME, location, 2, 0.2, 0.2, 0.2, 0.05);
			}
		});
	}
	
	@Override
	protected float leapPitch() {
		return 0.85f;
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
