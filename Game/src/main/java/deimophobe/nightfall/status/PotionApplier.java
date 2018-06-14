package deimophobe.nightfall.status;

import deimophobe.nightfall.game.GameEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

/**
 * Created by Deimophobe on 11/06/18.
 */
class PotionApplier implements StatusEffectApplier<Integer> {
	private final PotionEffectType potionEffect;
	private final boolean stacks;
	
	PotionApplier(PotionEffectType potionEffect, boolean stacks) {
		this.potionEffect = potionEffect;
		this.stacks = stacks;
	}
	
	@Override
	public void setState(GameEntity<?> receiver, Set<Integer> levels, int duration) {
		LivingEntity entity = receiver.getEntity();
		if (levels.isEmpty()) {
			entity.removePotionEffect(potionEffect);
		} else {
			int totalLevel = 0;
			for (int level : levels) {
				if (stacks) totalLevel += level;
				else totalLevel = Math.max(level, totalLevel);
			}
			entity.addPotionEffect(new PotionEffect(potionEffect, duration, totalLevel - 1));
		}
	}
	
	@Override
	public void update() {}
}
