package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * Created by Deimophobe on 19/01/17.
 */
public enum MobType {
	ZOMBIE("zombie"),
	GOBO("gobo"),
	
	WITHERSKELE("witherskele"),
	FLAMELANCER("flamelancer"),
	WOLF("wolf"),
	SPIDERLING("spiderling"),
	RAT("rat"),
	GOLEM("golem"),
	OGRE("ogre"),
	
	GB_DAGGER("gb-dagger"),
	GB_RUNEBLADE("gb-runeblade"),
	GB_AXE("gb-axe"),
	GB_HAMMER("gb-hammer"),
	
	HELLHOUND("hellhound"),
	
	KRUNGOR("krungor"),
	BOPEN("bopen"),
	
	
	TESTMOB,
	
	;
	
	private final MobData mobData;
	public MobData getMobData() {
		return mobData;
	}
	
	public String getName() {
		return name().toLowerCase();
	}
	
	MobType(String name) {
		this.mobData = MobData.getMobData(name);
	}
	MobType() {
		mobData = null;
	}
	
	public Mob createMob(MonsterPlayer monster) {
		switch (this) {
			case ZOMBIE: return new Zombie(monster);
			case GOBO: return new Goblin(monster);
			
			case WITHERSKELE: return new WitherSkele(monster);
			case FLAMELANCER: return new Flamelancer(monster);
			case WOLF: return new Wolf(monster);
			case SPIDERLING: return new Spiderling(monster);
			case RAT: return new Rat(monster);
			case GOLEM: return new Golem(monster);
			case OGRE: return new Ogre(monster);
			
			case KRUNGOR: return new Krungor(monster);
			case BOPEN: return new Bopen(monster);
			
			case GB_DAGGER:
			case GB_RUNEBLADE:
			case GB_AXE:
			case GB_HAMMER:
				return new Ghostblade(monster, this);
				
			case HELLHOUND:
				return new Hellhound(monster);
				
			case TESTMOB:
				return new TestMob(monster);
		}
		Bukkit.getLogger().severe("Unknown mobtype: " + this);
		throw new NullPointerException("Unknown mobtype: " + this);
	}
	
	public Map<String, CustomItem> getItems() {
		if (mobData == null) return Collections.emptyMap();
		return mobData.getItems();
	}
	
	public static Collection<String> getAllMobTypes() {
		Set<String> mobs = new HashSet<>();
		for (MobType type : values())
			mobs.add(type.name().toLowerCase());
		return mobs;
	}
	
	public static MobType getMobType(String type) {
		type = type.replace('-','_');
		for (MobType mobType : values()) {
			if (mobType.name().equalsIgnoreCase(type))
				return mobType;
		}
		Bukkit.getLogger().warning("No mob of type '" + type + "'!?");
		return null;
	}
}
