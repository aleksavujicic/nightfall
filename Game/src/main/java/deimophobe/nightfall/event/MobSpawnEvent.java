package deimophobe.nightfall.event;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.mob.AbstractMob;
import deimophobe.nightfall.monster.mob.Mob;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Deimophobe on 3/04/18.
 */
public class MobSpawnEvent extends Event {
	public static final HandlerList handlers = new HandlerList();
	private final MonsterPlayer monster;
	private final Mob mob;
	private final SpawnMethod spawnMethod;
	
	public MobSpawnEvent(MonsterPlayer monster, Mob mob, SpawnMethod spawnMethod) {
		this.monster = monster;
		this.mob = mob;
		this.spawnMethod = spawnMethod;
	}
	
	public MonsterPlayer getMonster() { return monster; }
	public Mob getMob() { return mob; }
	public SpawnMethod getSpawnMethod() { return spawnMethod; }
	public MobType getMobType() { return mob.getType(); }
	
	public void addWeaponModifier(ItemModifierType type, int value, String reason) {
		AbstractMob mob = castMob();
		if (mob.doesWeaponExist()) {
			CustomItem weapon = mob.getWeapon();
			weapon.addModifier(type, value, reason);
		}
	}
	
	@Deprecated
	private AbstractMob castMob() {
		return (AbstractMob) mob;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
