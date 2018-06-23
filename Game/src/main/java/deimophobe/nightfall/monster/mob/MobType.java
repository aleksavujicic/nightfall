package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.monster.MobCreator;
import deimophobe.nightfall.monster.MonsterPlayer;

import java.util.*;
import java.util.function.Function;

/**
 * Created by Deimophobe on 19/01/17.
 */
public enum MobType implements MobCreator<Mob> {
	// Seperate these into own types, use custom mob creators for each.
	ZOMBIE(MobType::spawnZombie),
    SKELETON(MobType::spawnSkeleton),
	GOBO(MobType::spawnGobo),
	
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

	TICKER(Ticker::new),
	ZEPHYR(Zephyr::new),
	
	TORUS(Torus::new),
	BOPEN(Bopen::new),
	MAGUS(Magus::new),
	FIRE_MAGI(MagiFire::new, "magi.fire"),
	ICE_MAGI(MagiIce::new, "magi.ice"),
	
	WAR_BALLOON(WarBalloon::new),
	
	TESTMOB(TestMob::new),
	
	PLAGUE_ZOMBIE,
	PLAGUE_ASSASSIN,
	;
	
	private final MobData mobData;
	public MobData getMobData() { return mobData; }
	
	private final Function<MonsterPlayer, Mob> mobCreator;
	
	MobType() { this(null, null); }
	MobType(Function<MonsterPlayer, Mob> mobCreator) { this(mobCreator, null); }
	
	MobType(Function<MonsterPlayer, Mob> mobCreator, String mobDataKey) {
		if (mobDataKey == null)
			mobDataKey = getName();
		
		this.mobData = MobData.getMobData(mobDataKey);
		this.mobCreator = mobCreator;
	}
	
	@Override
	public String getName() {
		return name().replace('_','-').toLowerCase();
	}
	
	@Override
	public Mob createMob(MonsterPlayer monster) {
		if (mobCreator != null) {
			return mobCreator.apply(monster);
		} else {
			throw new UnsupportedOperationException("Cannot directly create mob for mobtype: " + this);
		}
	}
	
	public boolean isSpawnable() {
		return mobCreator != null;
	}
	
	
	// ----- STATIC HELPERS -----
	
	private static Mob spawnZombie(MonsterPlayer monster) {
		if (monster.getUpgrades(MobType.ZOMBIE).computeIfAbsent("husk", (k) -> 0) == 1) {
			return new ZombieHusk(monster);
		}
		else if (monster.getUpgrades(MobType.ZOMBIE).computeIfAbsent("fury", (k) -> 0) == 1) {
			return new ZombieFury(monster);
		}
		else if (monster.getUpgrades(MobType.ZOMBIE).computeIfAbsent("saboteur", (k) -> 0) == 1) {
			return new ZombieSaboteur(monster);
		}
		else {
			return new ZombieMob(monster);
		}
	}
	
	private static Mob spawnSkeleton(MonsterPlayer monster) {
		if (monster.getUpgrades(MobType.SKELETON).computeIfAbsent("wither", (k) -> 0) == 1) {
			return new SkeletonWither(monster);
		}
		else if (monster.getUpgrades(MobType.SKELETON).computeIfAbsent("flamelancer", (k) -> 0) == 1) {
			return new SkeletonFlamelancer(monster);
		}
		else if (monster.getUpgrades(MobType.SKELETON).computeIfAbsent("impact", (k) -> 0) == 1) {
			return new SkeletonImpact(monster);
		}
		else {
			return new Skeleton(monster);
		}
	}
	
	private static Mob spawnGobo(MonsterPlayer monster) {
		if (monster.getUpgrades(MobType.GOBO).computeIfAbsent("kaboom", (k) -> 0) == 1) {
			return new GoblinKaboom(monster);
		}
		else {
			return new Goblin(monster);
		}
	}
	
	// Used for ItemManager.
	public Map<String, CustomItem> getItems() {
		return mobData.getItems();
	}
	
	public static MobType getMobType(String type) throws UnknownEnumElementException {
		return Misc.getEnumMemberFromString(type, values(), "MobType");
	}
	
	private static final List<MobType> spawnableMobs = new ArrayList<>();
	static {
		for (MobType type : values()) {
			if (type.isSpawnable()) spawnableMobs.add(type);
		}
	}
	
	public static MobType[] getSpawnableMobs() {
		return spawnableMobs.toArray(new MobType[0]);
	}
}
