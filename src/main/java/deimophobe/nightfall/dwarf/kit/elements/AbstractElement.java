package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.entity.GameEntity;
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
	public double onHit(GameEntity monster, DamageType type, double damage) {return damage;}
	
	@Override
	public double onGotHit(GameEntity monster, DamageType type, double damage) {return damage;}
	
	@Override
	public void onLateGotHit(GameEntity monster, DamageType type, double damage) {}
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {}
	
	@Override
	public void onShift(boolean sneaking) {}
	
	@Override
	public void notifyDeath(Dwarf deadDwarf) {}
}
