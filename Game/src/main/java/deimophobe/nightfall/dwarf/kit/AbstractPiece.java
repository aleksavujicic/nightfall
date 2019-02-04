package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 24/03/17.
 */
public class AbstractPiece implements KitPiece {
	protected final Dwarf dwarf;
	private final KitPieceType type;
	
	public AbstractPiece(Dwarf dwarf, KitPieceType type) {
		this.dwarf = dwarf;
		this.type = type;
	}
	
	@Override
	public final KitPieceType getType() {
		return type;
	}
	
	@Override
	public void update() {}
	
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {}
	
	@Override
	public void onKill(MonsterDamage damage) {}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {}
	
	
	@Override
	public void onShift(boolean sneaking) {}
	
	@Override
	public void notifyDeath(Dwarf deadDwarf) {}
	
	@Override
	public void onRemove() {}
}
