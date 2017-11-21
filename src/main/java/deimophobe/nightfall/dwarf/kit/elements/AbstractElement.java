package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitElement;

/**
 * Created by Deimophobe on 24/03/17.
 */
public class AbstractElement implements KitElement {
	protected final Dwarf dwarf;
	public AbstractElement(Dwarf dwarf) {
		this.dwarf = dwarf;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {}
	
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {}
	
	@Override
	public void onKill(MonsterDamage damage) {}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {}
	
	@Override
	public void damageNotify(DwarfDamage damage) {}
	
	
	@Override
	public void onShift(boolean sneaking) {}
	
	@Override
	public void notifyDeath(Dwarf deadDwarf) {}
	
	@Override
	public void onRemove() {}
}
