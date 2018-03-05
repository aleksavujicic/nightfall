package deimophobe.nightfall.damage;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Deimophobe on 13/02/18.
 */
public enum GameDamageType {
	MELEE,
	RANGED("shot"),
	
	// Natural Damage
	CONTACT(new ForcedDeathMessageMaker("was pricked to death."), new FixedDOTModifier(DamageOverTimeType.CONTACT, 10, 2, 1)),
	MAGMA_BLOCK(new ForcedDeathMessageMaker("burnt their feet"), new FixedDOTModifier(DamageOverTimeType.CONTACT, 10, 4, 4)),
	DROWNING(new ForcedDeathMessageMaker("drowned"), 8, 1),
	FIRE(new ForcedDeathMessageMaker("couldn't find water"), new FixedDOTModifier(DamageOverTimeType.FIRE, 6, 8, 2)),
	LAVA(new ForcedDeathMessageMaker("tried to swim in lava"), new FixedDOTModifier(DamageOverTimeType.FIRE, 5, 15, 5)),
	
	FALL(new ForcedDeathMessageMaker("fell to their doom"), damage -> {
		damage.getMulitPartDamage().timesMult(3*(1 - Math.pow(Math.random(),2)/2));
		damage.setNoDamageTicks(1);
	}),
	
	VOID(new ForcedDeathMessageMaker("was swallowed by the abyss"), GameDamage::instaKill),
	
	POISON(new KeywordDeathMessageMaker("poisoned"), new PoisonModifier(DamageOverTimeType.POISON, PotionEffectType.POISON, level -> (double) level*2, level -> 8L)),
	WITHER(new KeywordDeathMessageMaker("withered"), new PoisonModifier(DamageOverTimeType.WITHER, PotionEffectType.WITHER, level -> (double) level*2, level -> 8L)),
	
	
	// Mob damage
	SEPPUKU(new ForcedDeathMessageMaker("committed sudoku")),
	SHRINE_PROTECTION(new ForcedDeathMessageMaker("was zapped by lightning")),
	SELF_GOBO_KABOOM(new ForcedDeathMessageMaker("went kaboom")),
	
	JADE_BOW("pierced"),
	VOLCANIC_BOW("scorched"),
	LUMINOUS("pierced"),
	SCEPTER("pierced"),
	EVISCERATE("eviscerated"),
	HAMMER_AOE,
	GLAIVE_AOE,
	GLAIVE_ALT,
	INCORRECT_HELD_ITEM(new ForcedDeathMessageMaker("was a bit of a klutz and dropped their blade")),
	TINDERFLAME("zooped"),
	WILDFIRE("incinerated"),
	SILENT_STRIKE,
	MYST,
	SHADOW_STRIKE,
	BUFFPOOL(
			(playerName, damage) -> new TextComponent(playerName + " was consumed by " + damage.getAttackerName() + "'s buffpool")
	),
	BUBBLE_BEAM("bubbled"),
	GEYSER("bubbled"),
	WATER_BOW_AOE("splooshed"),
	
	AI_REMOVER,
	
	// Dwarf damage
	DEATH_PLAGUE(new ForcedDeathMessageMaker("was touched by " + ChatColor.DARK_GRAY + "DEATH")),
	GOBO_KABOOM("exploded"),
	GOBO_BOX_EXPLOSION("exploded"),
	BLAZE_EXPLOSION("blasted"),
	WITHER_SKULL("vapourised"),
	HUSK_STOMP("stomped"),
	IMPACT_AOE("pushed"),
	MOBSPAWN(new ForcedDeathMessageMaker("was consumed by the "  + ChatColor.DARK_GRAY + ChatColor.ITALIC + "Night")),
	
	MINOTAUR_CHARGE("trampled"),
	WRAITH_CHARGE("drained"),
	
	BLOOD_MAGIC, // Current for arthea's teleport
	
	
	// Misc
	COMMAND,
	
	@Deprecated TEMPORARY
	
	;
	
	private final Consumer<GameDamage<?,?>> damageModifer;
	private final DeathMessageMaker deathMessageMaker;
	
	GameDamageType() {
		this.deathMessageMaker = DeathMessageMaker.SIMPLE_DEATH_MESSAGE;
		this.damageModifer = gameDamage -> {};
	}
	
	GameDamageType(String keyword) {
		this.deathMessageMaker = new KeywordDeathMessageMaker(keyword);
		this.damageModifer = gameDamage -> {};
	}
	
	GameDamageType(DeathMessageMaker deathMessageMaker) {
		this.deathMessageMaker = deathMessageMaker;
		this.damageModifer = gameDamage -> {};
	}
	
	GameDamageType(DeathMessageMaker deathMessageMaker, Consumer<GameDamage<?, ?>> damageModifer) {
		this.damageModifer = damageModifer;
		this.deathMessageMaker = deathMessageMaker;
	}
	
	GameDamageType(DeathMessageMaker deathMessageMaker, double defaultDamage, int defaultArmourShred) {
		this.deathMessageMaker = deathMessageMaker;
		this.damageModifer = (damage) -> {
			damage.getMulitPartDamage().setBase(defaultDamage);
			if (damage instanceof DwarfDamage)
				((DwarfDamage) damage).setArmourShred(defaultArmourShred);
			damage.setNoDamageTicks(1);
		};
	}
	
	public void applyModifier(GameDamage<?,?> damage) {
		damageModifer.accept(damage);
	}
	
	public DeathMessageMaker getDefaultDeathMessageMaker() {
		return deathMessageMaker;
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
	
	private static abstract class AbstractDOTModifier implements Consumer<GameDamage<?,?>> {
		private final DamageOverTimeType type;
		
		protected AbstractDOTModifier(DamageOverTimeType type) {
			this.type = type;
		}
		
		public abstract long getRequiredDelay(GameDamage<?,?> damage);
		
		@Override
		public void accept(GameDamage<?, ?> damage) {
			if (!damage.getReceiver().canDamageOverTimeTick(type, getRequiredDelay(damage))) {
				damage.cancel();
			} else {
				damage.addPostDamageHandler(d -> d.getReceiver().doDamageOverTimeTick(type));
				damage.setNoDamageTicks(1);
			}
		}
	}
	
	private static class FixedDOTModifier extends AbstractDOTModifier {
		private final long delay;
		private final double defaultDamage;
		private final double defaultArmourShred;
		
		protected FixedDOTModifier(DamageOverTimeType type, long delay, double defaultDamage, double defaultArmourShred) {
			super(type);
			this.delay = delay;
			this.defaultDamage = defaultDamage;
			this.defaultArmourShred = defaultArmourShred;
		}
		
		@Override
		public long getRequiredDelay(GameDamage<?,?> damage) {
			return delay;
		}
		
		@Override
		public void accept(GameDamage<?, ?> damage) {
			super.accept(damage);
			damage.getMulitPartDamage().setBase(defaultDamage);
			if (damage instanceof DwarfDamage)
				((DwarfDamage) damage).setArmourShred(defaultArmourShred);
			
		}
	}
	
	
	private static final class PoisonModifier extends AbstractDOTModifier {
		private final PotionEffectType potionEffectType;
		private final Function<Integer, Double> levelMapper;
		private final Function<Integer, Long> delayMapper;
		
		private PoisonModifier(DamageOverTimeType dotType, PotionEffectType potionEffectType, Function<Integer, Double> levelMapper, Function<Integer, Long> delayMapper) {
			super(dotType);
			this.potionEffectType = potionEffectType;
			this.levelMapper = levelMapper;
			this.delayMapper = delayMapper;
		}
		
		@Override
		public long getRequiredDelay(GameDamage<?, ?> damage) {
			int level = damage.getReceiver().getPotionEffectLevel(potionEffectType);
			return delayMapper.apply(level);
		}
		
		@Override
		public void accept(GameDamage<?, ?> damage) {
			super.accept(damage);
			int level = damage.getReceiver().getPotionEffectLevel(potionEffectType);
			damage.getMulitPartDamage().setBase(levelMapper.apply(level));
		}
	}
}
