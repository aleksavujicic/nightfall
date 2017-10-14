package deimophobe.nightfall.damage;

/**
 * Created by Deimophobe on 1/09/17.
 */
public class MonsterDamageModifier extends DamageModifier {
	private boolean proc;
	
	public void setProc(boolean proc) {
		this.proc = proc;
	}
	
	public MonsterDamageModifier() {}
	
	@Override
	public void applyToDamage(GameDamage damage) {
		if (!(damage instanceof MonsterDamage)) throw new IllegalArgumentException("MonsterDamageModifier can only apply to MonsterDamage.");
		MonsterDamage monsterDamage = (MonsterDamage) damage;
		
		super.applyToDamage(damage);
		monsterDamage.setProc(proc);
	}
}
