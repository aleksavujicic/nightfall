package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.RepeaterCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class BloodiedWaraxe extends AbstractItem implements CooldownPiece {
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "axe");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getPickupType() { return PickupType.SWORD; }
	
	private final ComplexCooldown cd = new ComplexCooldown(60*20, this::giveProc, this::notifyOffCD);
	private final Cooldown hunger = new RepeaterCooldown(30*20, this::hunger);
	
	public BloodiedWaraxe(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	@Override
	public void update() {
		super.update();
		cd.update();
		hunger.update();
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		cd.reduceCooldown(20);
		if (dwarf.hasProc(ProcType.MALICE) && isMeleeDamageFromItem(damage)) {
			dwarf.heal(5);
			dwarf.regenMana(5);
		}
		
		hunger.reset();
	}
	
	@Override
	public boolean onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		if (click.isRightClick() && !dwarf.getNoSpecial()) {
			return cd.tryUse();
		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		return cd.getCooldown();
	}
	
	private void giveProc() {
		dwarf.giveProc(ProcType.MALICE);
		//dwarf.playSound("maliceuse", 20f, 1f, false);
		dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, ProcType.MALICE.getDuration(), 1,true,false,true);
	}
	
	private void notifyOffCD() {
		dwarf.playSound("entity.elder_guardian.curse", 1, 1f, false);
	}
	
	private void hunger() {
		if (Math.random() <= 0.4 && isHoldingItem()) {
			dwarf.playSound("dwarf.item.waraxe.idle");
		}
	}
	
}
