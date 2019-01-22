package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.monster.MobCreator;
import deimophobe.nightfall.monster.MonsterPlayer;

import java.util.*;
import java.util.function.Function;

/**
 * Created by Deimophobe on 19/01/17.
 */
public enum MobType implements MobCreator<Mob> {
	ZOMBIE_FURY(ZombieFury::new, "zombie.fury", "fury"),
	ZOMBIE_HUSK(ZombieHusk::new, "zombie.husk", "husk"),
	ZOMBIE_SABOTEUR(ZombieSaboteur::new, "zombie.saboteur", "saboteur"),
	
	SKELETON_FLAME(SkeletonFlamelancer::new, "skeleton.flamelancer", "flame"),
	SKELETON_IMPACT(SkeletonImpact::new, "skeleton.impact", "impact"),
	SKELETON_WITHER(SkeletonWither::new, "skeleton.wither", "wither"),
	
	GOBLIN_KABOOM(GoblinKaboom::new, "gobo", "goblin"),
	
	
	ZOMBIE_BASE(ZombieBasic::new, "zombie"),
	SKELETON_BASE(Skeleton::new, "skeleton"),
	GOBLIN_BASE(Goblin::new, "gobo"),
	
	
	EMBER_SPRITE(EmberSprite::new),
	WOLF(WolfMob::new),
	HELLHOUND(Hellhound::new, "wolf.hellhound"),
	SPIDERLING(Spiderling::new),
	RAT(Rat::new),
	GOLEM(Golem::new),
	WRAITH(Wraith::new),
	MINOTAUR(Minotaur::new),
	BATTERING_RAM(BatteringRam::new),
	DOPPELGANGER(Doppelganger::new),
	SILVERBELL(Silverbell::new),
	
	MAMABEAR(MamaBear::new),
	POLARBABE(PolarBabes::new),

	WALKER(Walker::new),

	SQUID(SquidMob::new),
	TICKER(Ticker::new),
	ZEPHYR(Zephyr::new),
	
	TORUS(Torus::new),
	BOPEN(Bopen::new),
	MAGUS(Magus::new),
	FIRE_MAGI(MagiFire::new, "magi.fire"),
	ICE_MAGI(MagiIce::new, "magi.ice"),
	THUNDER_MAGI(MagiThunder::new, "magi.thunder"),
	
	WAR_BALLOON(WarBalloon::new),
	
	TESTMOB(TestMob::new),
	
	PLAGUE_ZOMBIE,
	PLAGUE_ASSASSIN,
	;
	
	private final String name;
	private final String primaryPrefix;
	private final MobData mobData;
	private final Function<MonsterPlayer, Mob> mobCreator;
	
	
	MobType() {
		this(null, null);
	}
	MobType(Function<MonsterPlayer, Mob> mobCreator) {
		this(mobCreator, null);
	}
	MobType(Function<MonsterPlayer, Mob> mobCreator, String mobDataKey) {
		this(mobCreator, mobDataKey, null);
	}
	
	MobType(Function<MonsterPlayer, Mob> mobCreator, String mobDataKey, String primaryPrefix) {
		this.name = name().replace('_','-').toLowerCase();
		
		if (mobDataKey == null)
			mobDataKey = getName();
		
		this.mobData = MobData.getMobData(mobDataKey);
		this.mobCreator = mobCreator;
		this.primaryPrefix = primaryPrefix;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Mob createMob(MonsterPlayer monster) {
		if (mobCreator != null) {
			return mobCreator.apply(monster);
		} else {
			throw new UnsupportedOperationException("Cannot directly create mob for mobtype: " + this);
		}
	}
	
	public String getPrimaryPrefix() {
		return primaryPrefix;
	}
	
	public MobData getMobData() {
		return mobData;
	}
	
	public boolean isSpawnable() {
		return mobCreator != null;
	}
	
	public boolean isPrimary() {
		return primaryPrefix != null;
	}
	
	public boolean isZombie() {
		switch (this) {
			case ZOMBIE_BASE:
			case ZOMBIE_FURY:
			case ZOMBIE_HUSK:
			case ZOMBIE_SABOTEUR:
				return true;
			default:
				return false;
		}
	}
	
	
	// ----- STATIC HELPERS -----
	
	// Used for ItemManager.
	public Map<String, CustomItem> getItems() {
		return mobData.getItems();
	}
	
	private static final List<MobType> spawnableMobs = new ArrayList<>();
	private static final List<MobType> primaryMobs = new ArrayList<>();
	static {
		for (MobType type : values()) {
			if (type.isSpawnable()) spawnableMobs.add(type);
			if (type.isPrimary()) primaryMobs.add(type);
		}
	}
	
	public static MobType[] getSpawnableMobs() {
		return spawnableMobs.toArray(new MobType[0]);
	}
	public static MobType[] getPrimaryMobs() {
		return primaryMobs.toArray(new MobType[0]);
	}
}
