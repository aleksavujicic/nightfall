package deimophobe.nightfall.damage;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Deimophobe on 13/02/18.
 */
public enum GameDamageType {
	MELEE,
	RANGED,
	
	// Natural Damage
	CONTACT(2, 1),
	DROWNING(8, 1),
	FIRE(5, 4),
	LAVA(12, 10),
	MAGMA_BLOCK(4, 4),
	
	FALL((damage) -> {
		damage.getMulitPartDamage().timesMult(3*(1 - Math.pow(Math.random(),2)/2));
	}),
	
	VOID(GameDamage::instaKill),
	
	POISON(new PoisonModifier(PotionEffectType.POISON, level -> (double) level*2)),
	WITHER(new PoisonModifier(PotionEffectType.WITHER, level -> (double) level*2)),
	
	
	// Mob damage
	SEPPUKU,
	SHRINE_PROTECTION,
	SELF_GOBO_KABOOM,
	
	JADE_BOW,
	VOLCANIC_BOW,
	LUMINOUS,
	EVISCERATE,
	HAMMER_AOE,
	GLAIVE_AOE,
	GLAIVE_ALT,
	INCORRECT_HELD_ITEM,
	TINDERFLAME,
	WILDFIRE,
	SILENT_STRIKE,
	MYST,
	SHADOW_STRIKE,
	SCEPTER_OF_MAGMA,
	BUFFPOOL,
	BUBBLE_BEAM,
	GEYSER,
	WATER_BOW_AOE,
	
	AI_REMOVER,
	
	// Dwarf damage
	DEATH_PLAGUE,
	GOBO_KABOOM,
	GOBO_BOX_EXPLOSION,
	BLAZE_EXPLOSION,
	WITHER_SKULL,
	HUSK_STOMP,
	MOBSPAWN,
	
	MINOTAUR_CHARGE,
	WRAITH_CHARGE,
	
	BLOOD_MAGIC, // Current for arthea's teleport
	
	
	// Misc
	COMMAND,
	IMPACT_AOE,
	
	@Deprecated TEMPORARY
	
	;
	
	private final Consumer<GameDamage<?,?>> damageModifer;
	
	GameDamageType() {
		this.damageModifer = gameDamage -> {};
	}
	
	GameDamageType(Consumer<GameDamage<?,?>> damageModifer) {
		this.damageModifer = damageModifer;
	}
	
	GameDamageType(double defaultDamage, int defaultArmourShred) {
		this.damageModifer = (damage) -> {
			damage.getMulitPartDamage().setBase(defaultDamage);
			if (damage instanceof DwarfDamage)
				((DwarfDamage) damage).setArmourShred(defaultArmourShred);
		};
	}
	
	public void applyModifier(GameDamage<?,?> damage) {
		damageModifer.accept(damage);
	}
	
	public boolean isArrow() {
		switch (this) {
			case RANGED:
			case JADE_BOW:
			case VOLCANIC_BOW:
			case LUMINOUS:
			case WATER_BOW_AOE:
				return true;
				
			default:
				return false;
		}
	}
	
	public static GameDamageType getTypeFromEventCause(EntityDamageEvent.DamageCause cause) {
		switch (cause) {
			case CONTACT: return CONTACT;
			case DROWNING: return DROWNING;
			case HOT_FLOOR: return MAGMA_BLOCK;
			case FALL: return FALL;
			case LAVA: return LAVA;
			
			case FIRE:
			case FIRE_TICK:
				return FIRE;
			
			case POISON: return POISON;
			case WITHER: return WITHER;
			
			case VOID: return VOID;
			
			default:
				throw new IllegalArgumentException("Cannot create GameDamage with event cause " + cause);
		}
	}
	
	private static final class PoisonModifier implements Consumer<GameDamage<?,?>> {
		private final PotionEffectType potionEffectType;
		private final Function<Integer, Double> levelMapper;
		
		private PoisonModifier(PotionEffectType potionEffectType, Function<Integer, Double> levelMapper) {
			this.potionEffectType = potionEffectType;
			this.levelMapper = levelMapper;
		}
		
		@Override
		public void accept(GameDamage<?, ?> damage) {
			int level = damage.getReceiver().getPotionEffectLevel(potionEffectType);
			damage.getMulitPartDamage().setBase(levelMapper.apply(level));
			damage.setNoDmgTicks(1);
		}
	}
}
