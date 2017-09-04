package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Dragonskin extends AbstractToggleBow {
	
	Dragonskin(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 50;
	private final static CustomItem ITEM = DwarvenItems.getBow("dragonskin", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.BOW; }
	@Override public String getBowIdentifier() {return "DRAGONSKIN";}
	@Override public int getPower() {return POWER;}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		if (damageFromItem(damage) && damage.hasArrowData()) {
			Projectile arrow  = damage.arrowData().getArrow();
			if (isActiveProjectile(arrow)) {
				damage.setDamage(100);
				damage.multiplyArrowRes(0.5);
			}
		}
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		if (damageFromItem(damage))
			dwarf.giveProc(ProcType.DRAGONSKIN);
	}
	
	@Override
	protected boolean canToggle() {
		return true;
	}
}
