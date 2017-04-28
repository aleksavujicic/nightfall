package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Bukkit;

/**
 * Created by Deimophobe on 19/01/17.
 */
public enum MobType {
	ZOMBIE("zombie"),
	GOBO("gobo"),
	
	WITHERSKELE("witherskele"),
	FLAMELANCER("flamelancer"),
	WOLF("wolf"),
	DIREWOLF("direwolf"),
	SPIDERLING("spiderling"),
	RAT("rat"),
	GOLEM("golem"),
	OGRE("ogre"),
	
	GB_DAGGER("gb-dagger"),
	GB_RUNEBLADE("gb-runeblade"),
	GB_AXE("gb-axe"),
	GB_HAMMER("gb-hammer"),
	
	KRUNGOR("krungor"),
	BOPEN("bopen"),
	;
	
	private final String name;
	public String getName() {return name;}
	
	MobType(String name) {
		this.name = name;
	}
	
	
	
	public static MobType getMobType(String type) {
		for (MobType mobType : values()) {
			if (mobType.getName().equalsIgnoreCase(type))
				return mobType;
		}
		Bukkit.getLogger().warning("No mob of type '" + type + "'!?");
		return null;
	}
	
	public MobData getMobData() {
		return MobData.getMobData(name);
	}
	
	public Mob createMob(MonsterPlayer monster) {
		switch (this) {
			case ZOMBIE: return new Zombie(monster);
			case GOBO: return new Goblin(monster);
			
			case WITHERSKELE: return new WitherSkele(monster);
			case FLAMELANCER: return new Flamelancer(monster);
			case WOLF: return new Wolf(monster);
			case DIREWOLF: return new Direwolf(monster);
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
		}
		Bukkit.getLogger().warning("Unknown mobtype: " + this);
		return null;
	}
}
