package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 24/09/17.
 */
public class Greatsword extends AbstractItem {
	
	public Greatsword(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "greatsword");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getPickupType() {
		return PickupType.SWORD;
	}
	
	private static final int MAX_EXHAUSTION = 3*20;
	private int exhaustion = 0;
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (exhaustion > 0) {
			damage.cancel();
		} else if (isMeleeDamageFromItem(damage)) {
			giveExhaustion();
		}
	}
	
	@Override
	public void update() {
		super.update();
		if (exhaustion > 0)
			exhaustion--;
	}
	
	private void giveExhaustion() {
		dwarf.givePotionEffect(PotionEffectType.SLOW_DIGGING, MAX_EXHAUSTION, 200, true, false, true);
		exhaustion = MAX_EXHAUSTION;
	}
}
