package deimophobe.nightfall.monster;

import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;

/**
 * Created by Deimophobe on 1/05/18.
 */
public class VampirismCooldown extends SimpleCooldown {
	private final MonsterPlayer monster;
	
	private final int manaDrain;
	private final double lifeSteal;
	
	private final Cooldown manaDrainer = new SimpleCooldown(20);
	
	public VampirismCooldown(int maxCD, MonsterPlayer monster, int manaDrain, double lifeSteal) {
		super(maxCD);
		this.monster = monster;
		this.manaDrain = manaDrain;
		this.lifeSteal = lifeSteal;
	}
	
	@Override
	public void update() {
		super.update();
		manaDrainer.update();
	}
	
	@Override
	public boolean tryUse() {
		throw new UnsupportedOperationException("Use tryUse(DwarfDamage) instead.");
	}
	
	public boolean tryUse(DwarfDamage damage) {
		if (!super.tryUse()) return false;
		
		if (manaDrainer.isAvailable()) {
			manaDrainer.reset();
			damage.addManaDrain(manaDrain);
		}
		damage.addPostDamageHandler(() -> {
			monster.heal(lifeSteal);
		});
		
		return true;
	}
}
