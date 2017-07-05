package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Bukkit;

/**
 * Created by Deimophobe on 15/06/17.
 */
public class TestMob extends AbstractMob {
	
	protected TestMob(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override public boolean isProccable() {return false;}
	@Override public double getResistance() {return 0;}
	@Override public double getArrowRes() {return 0;}
	@Override public int getArmourShred() {return 0;}
	@Override public int getTorchXP() {return 0;}
	@Override public boolean isShrineImmune() {return false;}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage){
		Bukkit.broadcastMessage("Hit dwarf " + dwarf.getName() + " for " + damage + " damage of type " + type.name());
		return 0;
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		Bukkit.broadcastMessage("Got hit by dwarf " + dwarf.getName() + " for " + damage + " damage of type " + type.name());
		return 0;
	}
}
