package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.ChatColor;

/**
 * Created by Deimophobe on 29/11/17.
 */
public class PenyStick extends AbstractItem {
	public PenyStick(Dwarf dwarf) { super(dwarf); }
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "penystick");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.START;
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isMeleeDamageFromItem(damage)) {
			MonsterEntity monster = damage.getMonster();
			if (monster instanceof MonsterPlayer) {
				((MonsterPlayer) monster).sendTitleMessage(ChatColor.DARK_AQUA + "Penny" + ChatColor.YELLOW + " is more popular");
			}
		}
	}
}
