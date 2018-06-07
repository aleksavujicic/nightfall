package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.Game;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.PigZombie;

import java.util.function.Consumer;

class AIMinotaur extends AIEntity<PigZombie> {
	
	private static final Consumer<PigZombie> MINO_INITIALISER = minotaur -> {
		minotaur.setBaby(false);
		
		double speed = Misc.randomDouble(-0.2, 0.4);
		AttributeModifier speedModifier = new AttributeModifier("speed", speed, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
		minotaur.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(speedModifier);
		minotaur.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.5);
		minotaur.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
		minotaur.setHealth(60);
		
		minotaur.setAnger(1000000);
		minotaur.setAngry(true);
		minotaur.setSilent(true);
		
		minotaur.getEquipment().setItemInMainHand(null);
	};
	
	private int lastHurtCry = 0;
	
	AIMinotaur(Location location, String name, Dwarf target) {
		super(location, name, target, PigZombie.class, MINO_INITIALISER);
	}
	
	@Override
	public boolean isBowInstaKillable() {
		return false;
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.setProc(false);
		
		if (damage.getType().isArrow()) {
			damage.getMultiPartDamage().timesMult(0.5);
		}
		
		damage.addPostDamageHandler(() -> {
			if (damage.willKill()) return;
			
			int currentTick = Game.getGame().getCurrentTick();
			if (lastHurtCry + 8 <= currentTick) {
				Location center = entity.getLocation();
				center.getWorld().playSound(center, "entity.shulker.hurt", 1f, 0.5f);
				lastHurtCry = currentTick;
			}
		});
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.getMultiPartDamage().setBase(18);
	}
	
	
	@Override
	public void onDeath(MonsterDamage damage) {
		if (damage.getType() != GameDamageType.AI_REMOVER) {
			entity.getLocation().getWorld().playSound(getLocation(), "entity.shulker.death", 1f, 0.6f);
		}
		super.onDeath(damage);
	}
}
