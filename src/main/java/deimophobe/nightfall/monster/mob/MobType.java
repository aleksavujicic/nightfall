package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * Created by Deimophobe on 19/01/17.
 */
public enum MobType {
	ZOMBIE,
	ZOMBIE_FURY,
	ZOMBIE_HUSK,
	ZOMBIE_SABOTEUR,

	GOBO,
	
	WITHERSKELE,
	FLAMELANCER,
	WOLF,
	SPIDERLING,
	RAT,
	GOLEM,
	
	GB_DAGGER,
	GB_RUNEBLADE,
	GB_AXE,
	GB_HAMMER,
	
	HELLHOUND,
	WRAITH,
	MINOTAUR,
	WALKER,
	
	KRUNGOR,
	BOPEN,
	
	TESTMOB,
	
	PLAGUE_ZOMBIE(false),
	;
	
	private final MobData mobData;
	public MobData getMobData() {
		return mobData;
	}
	
	private final boolean spawnable;
	
	public String getName() {
		return name().replace('_','-').toLowerCase();
	}
	
	MobType(boolean spawnable) {
		this.mobData = MobData.getMobData(getName());
		mobData.verify();
		
		this.spawnable = spawnable;
	}
	
	MobType() {
		this(true);
	}
	
	public Mob createMob(MonsterPlayer monster) {
		switch (this) {
			case ZOMBIE: {
				if (monster.getUpgrades(MobType.ZOMBIE).computeIfAbsent("husk", (k) -> 0) == 1) {
					return new Zombie_Husk(monster);
				}
				else if (monster.getUpgrades(MobType.ZOMBIE).computeIfAbsent("fury", (k) -> 0) == 1) {
					return new Zombie_Fury(monster);
				}
				else if (monster.getUpgrades(MobType.ZOMBIE).computeIfAbsent("saboteur", (k) -> 0) == 1) {
					return new Zombie_Saboteur(monster);
				}
				else {
					return new Zombie(monster);
				}
			}
			case GOBO: return new Goblin(monster);
			
			case WITHERSKELE: return new WitherSkele(monster);
			case FLAMELANCER: return new Flamelancer(monster);
			case WOLF: return new Wolf(monster);
			case SPIDERLING: return new Spiderling(monster);
			case RAT: return new Rat(monster);
			case GOLEM: return new Golem(monster);
			
			case KRUNGOR: return new Krungor(monster);
			case BOPEN: return new Bopen(monster);
			
			case GB_DAGGER:
			case GB_RUNEBLADE:
			case GB_AXE:
			case GB_HAMMER:
				return new Ghostblade(monster, this);
				
			case HELLHOUND: return new Hellhound(monster);
			case WRAITH: return new Wraith(monster);
			case MINOTAUR: return new Minotaur(monster);
			case WALKER: return new Walker(monster);
				
			case TESTMOB: return new TestMob(monster);
				
			case PLAGUE_ZOMBIE:
				throw new IllegalArgumentException("Plague zombie cannot be created normally.");
				
		}
		Bukkit.getLogger().severe("Unknown mobtype: " + this);
		throw new IllegalArgumentException("Unknown mobtype: " + this + ". Did deimo forgot to set a case for this?");
	}
	
	public static Collection<String> getAllMobTypes() {
		Set<String> mobs = new HashSet<>();
		for (MobType type : values())
			if (type.spawnable)
				mobs.add(type.getName());
		return mobs;
	}
	
	public static MobType getMobType(String type) {
		for (MobType mobType : values()) {
			if (mobType.spawnable && mobType.getName().equalsIgnoreCase(type))
				return mobType;
		}
		throw new IllegalArgumentException("No mob of type '" + type + "'!?");
	}
	
	// Used for ItemManager.
	public Map<String, CustomItem> getItems() {
		return mobData.getItems();
	}
	
	
}
