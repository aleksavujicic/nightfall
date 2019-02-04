package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.CompletionCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class RubyPendant extends AbstractAle {
	private final static int MANA_COST = 200;
	private final static int BUFF_DURATION = 40;
	
	private final Cooldown buffResetter = new CompletionCooldown(BUFF_DURATION, this::resetBuff);
	
	public RubyPendant(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("pendant", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void update() {
		super.update();
		buffResetter.update();
	}
	
	@Override
	public void heal() {
		dwarf.givePotionEffect(PotionEffectType.REGENERATION, BUFF_DURATION, 6, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BUFF_DURATION, 3, true, false, true);
		dwarf.playSound("block.enchantment_table.use", 1f, 1.1f, true);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
		
		buffResetter.reset();
	}
	
	private void resetBuff() {
		dwarf.givePermanentPotionEffect(PotionEffectType.REGENERATION, 4, true, true);
	}
}
