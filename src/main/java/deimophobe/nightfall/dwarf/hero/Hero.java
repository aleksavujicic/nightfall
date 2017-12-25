package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.HeroArmour;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class Hero extends Dwarf {
	protected final HeroType type;
	
	protected Hero(Player player, HeroType type) {
		super(player, type.getData());
		
		this.type = type;
		
		setArmour(new HeroArmour(this, type.getData().getHat()));
		
		makeBlindImmune();
		makePlagueImmune();
	}
	
	@Override
	public void onRemove() {
		super.onRemove();
		SkinManager.getManager().removeSkinChange(this);
	}
	
	@Override
	public void updateTitle() {}
	
	@Override
	public void updateHat() {}
	
	
	@Override
	public void showTrash() {}
	
	@Override
	public void updateVisibility() {}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.multiplyManaDrain(0.2);
	}
	
	@Override
	public void regenMana(int amt) {
		super.regenMana(amt/3);
	}
}
