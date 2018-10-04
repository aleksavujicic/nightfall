package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class RubyPendant extends AbstractAle {
	private final static int MANA_COST = 200;
	private final static int BUFF_DURATION = 60;
	
	public RubyPendant(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("pendant", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void heal() {
		dwarf.addUpdateable(
				new LifetimeExpireable(BUFF_DURATION) {
					@Override
					public void onExpiry() {
						super.onExpiry();
						dwarf.givePermanentPotionEffect(PotionEffectType.REGENERATION, 4, true, true);
					}
				}
		);
		
		dwarf.givePotionEffect(PotionEffectType.REGENERATION, BUFF_DURATION, 5, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BUFF_DURATION, 2, true, false, true);
		dwarf.playSound("block.enchantment_table.use", 1f, 1.1f, true);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
	}
}
