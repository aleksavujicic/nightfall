package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 10/10/17.
 */
public abstract class AbstractRuneblade extends AbstractItem implements KitCooldownElement {
	private static final int CD_TIME = 400;
	
	private final ComplexCooldown cooldown;
	private final ProcType regProc;
	private final ProcType dashProc;
	
	protected AbstractRuneblade(Dwarf dwarf, int maxCD, ProcType regProc, ProcType dashProc) {
		super(dwarf);
		this.regProc = regProc;
		this.dashProc = dashProc;
		this.cooldown = new ComplexCooldown(maxCD, this::dash, this::offCDSound);
	}
	
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	};
	
	
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		cooldown.update();
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		super.onKill(damage);
		if (damageFromItem(damage))
			dwarf.giveProc(regProc);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		if (cooldown.wasUsedWithin(dashProc.getDuration())) {
			damage.cancel();
		}
		
		if (cooldown.wasUsedWithin(60) && damage.getType() == NaturalDamageType.FALL) {
			damage.cancel();
		}
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			return cooldown.tryUse();
		}
		return false;
	}
	
	@Override
	public float fractionComplete() {
		return cooldown.fractionComplete();
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return null;
	}
	
	private void dash() {
		dwarf.playSound("dash", 1f, 1f, true);
		dwarf.giveProc(dashProc);
		//dwarf.setVelocity(dwarf.getLocation().getDirection().setY(0).normalize().multiply(5));
		dwarf.leap(5, 0);
	}
	
	private void offCDSound() {
		dwarf.playSound("offcd", 1, 1.2f, false);
		new BukkitRunnable() {
			@Override
			public void run() {
				dwarf.playSound("offcd", 1, 1.5f, false);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 5);
	}
}
