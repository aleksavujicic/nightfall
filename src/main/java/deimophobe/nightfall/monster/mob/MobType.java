package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.monster.MonsterPlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Deimophobe on 19/01/17.
 */
public enum MobType {
	ZOMBIE(MobType::spawnZombie),
    SKELETON(MobType::spawnSkeleton),
	GOBO(MobType::spawnGobo),
	
	EMBER_SPRITE(EmberSprite::new),
	WOLF(Wolf::new),
	HELLHOUND(Hellhound::new, "wolf.hellhound"),
	SPIDERLING(Spiderling::new),
	RAT(Rat::new),
	GOLEM(Golem::new),
	WRAITH(Wraith::new),
	MINOTAUR(Minotaur::new),
	BATTERING_RAM(BatteringRam::new),
	DOPPELGANGER(Doppelganger::new),

	WALKER(Walker::new),
	
	GB_DAGGER(Ghostblade::createDaggerGB, "ghostblade.dagger"),
	GB_RUNEBLADE(Ghostblade::createRunebladeGB, "ghostblade.runeblade"),
	GB_AXE(Ghostblade::createAxeGB, "ghostblade.axe"),
	GB_HAMMER(Ghostblade::createHammerGB, "ghostblade.hammer"),
	GB_SPAWN(Ghostblade::createSpawnGB, "ghostblade.spawn"),

	TICKER(Ticker::new),
	
	KRUNGOR(Krungor::new),
	BOPEN(Bopen::new),
	MAGUS(Magus::new),
	OGRE_MAGI(OgreMagi::new),
	
	TESTMOB(TestMob::new),
	
	PLAGUE_ZOMBIE
	;
	
	private final MobData mobData;
	public MobData getMobData() { return mobData; }
	
	private final Function<MonsterPlayer, Mob> mobCreator;
	
	public String getName() {
		return name().replace('_','-').toLowerCase();
	}
	
	MobType() { this(null, null); }
	MobType(Function<MonsterPlayer, Mob> mobCreator) { this(mobCreator, null); }
	
	MobType(Function<MonsterPlayer, Mob> mobCreator, String mobDataKey) {
		if (mobDataKey == null)
			mobDataKey = getName();
		
		this.mobData = MobData.getMobData(mobDataKey);
		this.mobCreator = mobCreator;
	}
	
	
	public Mob createMob(MonsterPlayer monster) {
		if (mobCreator != null) {
			return mobCreator.apply(monster);
		} else {
			throw new UnsupportedOperationException("Cannot directly create mob for mobtype: " + this);
		}
	}
	
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
	
	public boolean isSpawnable() {
		return mobCreator != null;
	}
	
	public static Collection<String> getAllMobTypes() {
		Set<String> mobs = new HashSet<>();
		for (MobType type : values()) {
			if (type.isSpawnable()) {
				mobs.add(type.getName());
			}
		}
		return mobs;
	}
	
	public static MobType getMobType(String type) throws UnknownEnumElementException {
		return Misc.getEnumMemberFromString(type, values(), "MobType");
	}
	
	// Used for ItemManager.
	public Map<String, CustomItem> getItems() {
		return mobData.getItems();
	}
	
	
}
