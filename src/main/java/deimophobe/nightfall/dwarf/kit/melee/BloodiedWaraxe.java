package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class BloodiedWaraxe extends AbstractItem implements CooldownPiece {
	
	private final ComplexCooldown cd = new ComplexCooldown(60*20, this::giveProc, this::notifyOffCD);
	
	public BloodiedWaraxe(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "axe");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	@Override
	public void update() {
		super.update();
		cd.update();
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		cd.reduceCooldown(20);
		if (dwarf.hasProc() && isMeleeDamageFromItem(damage)) {
			dwarf.heal(5);
			dwarf.regenMana(5);
		}
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isRightClick() && !dwarf.getNoSpecial()) {
			cd.tryUse();
		}
		return false;
	}
	
	private void giveProc() {
		dwarf.giveProc(ProcType.MALICE);
		//dwarf.playSound("maliceuse", 20f, 1f, false);
		dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, ProcType.MALICE.getDuration(), 1,true,false,true);
	}
	
	private void notifyOffCD() {
		dwarf.playSound("entity.elder_guardian.curse", 1, 1f, false);
	}
	
	@Override
	public float getCooldown() {
		return cd.getCooldown();
	}
	
}
