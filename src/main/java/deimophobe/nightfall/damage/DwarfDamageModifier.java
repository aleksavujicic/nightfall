package deimophobe.nightfall.damage;

/**
 * Created by Deimophobe on 1/09/17.
 */
public class DwarfDamageModifier extends DamageModifier {
	private int armourShred = 0;
	private int manaDrain = 0;
	
	public DwarfDamageModifier setArmourShred(int armourShred) {
		this.armourShred = armourShred;
		return this;
	}
	
	public DwarfDamageModifier setManaDrain(int manaDrain) {
		this.manaDrain = manaDrain;
		return this;
	}
	
	public DwarfDamageModifier() {}
	
	@Override
	void applyToDamage(GameDamage damage) {
		if (!(damage instanceof DwarfDamage)) throw new IllegalArgumentException("DwarfDamageModifier can only apply to DwarfDamage.");
		DwarfDamage dwarfDamage = (DwarfDamage) damage;
		
		super.applyToDamage(damage);
		dwarfDamage.setArmourShred(armourShred);
		dwarfDamage.setManaDrain(manaDrain);
	}
}
