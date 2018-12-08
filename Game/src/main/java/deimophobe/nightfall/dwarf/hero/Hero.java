package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.HeroArmour;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class Hero extends Dwarf {
	protected final HeroType type;
	
	public HeroType getType() {
		return type;
	}
	
	protected Hero(Player player, HeroType type) {
		super(player, type.getData());
		
		this.type = type;
		
		setArmour(new HeroArmour(this, type.getData().getHat().clone()));
	}
	
	@Override
	public boolean isHero() {
		return true;
	}
	
	@Override
	public PlagueStatus getPlagueStatus() {
		return PlagueStatus.IMMUNE;
	}
	
	@Override
	public void updateTitle() {}
	
	@Override
	public void updateHat() {}
	
	
	@Override
	public void showTrash() {}
}
