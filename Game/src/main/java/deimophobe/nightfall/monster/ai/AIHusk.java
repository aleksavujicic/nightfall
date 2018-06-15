package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Husk;

import java.util.function.Consumer;

class AIHusk extends AIEntity<Husk> {
	
	private static final double HEALTH = 80;
	private static final double DAMAGE = 30;
	
	private static final Consumer<Husk> HUSK_INITIALISER = husk -> {
		husk.setBaby(false);
		
		double speed = Misc.randomDouble(0, 0.5);
		AttributeModifier speedModifier = new AttributeModifier("speed", speed, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
		husk.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(speedModifier);
		husk.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.5);
		husk.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(HEALTH);
		husk.setHealth(HEALTH);
		
		husk.getEquipment().setItemInMainHand(null);
	};
	
	AIHusk(Location location, String name, Dwarf target) {
		super(location, name, target, Husk.class, HUSK_INITIALISER);
	}
	
	@Override
	public boolean isBowInstaKillable() {
		return false;
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.setProc(false);
		damage.getArrowRes().timesMult(0.5);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.getMultiPartDamage().setBase(DAMAGE);
	}
	
	
	@Override
	public void onDeath(MonsterDamage damage) {
		if (damage.getType() != GameDamageType.AI_REMOVER) {
			entity.getLocation().getWorld().playSound(getLocation(), "entity.husk.death", 1f, 0.6f);
		}
		super.onDeath(damage);
	}
}
