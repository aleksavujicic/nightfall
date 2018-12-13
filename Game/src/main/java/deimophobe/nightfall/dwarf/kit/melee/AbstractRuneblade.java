package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.*;
import deimophobe.nightfall.damage.death.DeathMessageMaker;
import deimophobe.nightfall.damage.death.KeywordDeathMessageMaker;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 10/10/17.
 */
public abstract class AbstractRuneblade extends AbstractItem implements CooldownPiece {
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
	}
	
	
	
	@Override
	public void update() {
		cooldown.update();
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		super.onKill(damage);
		if (isMeleeDamageFromItem(damage))
			dwarf.giveProc(regProc);
	}
	
	
	private static final DeathMessageMaker DASH_DEATH_MSG = new KeywordDeathMessageMaker("runedashed");
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (dwarf.hasProc(dashProc)) {
			damage.setDeathMessageMaker(DASH_DEATH_MSG);
		}
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		if (cooldown.wasUsedWithin(dashProc.getDuration())) {
			damage.cancel();
		}
		
		if (cooldown.wasUsedWithin(60) && damage.getType() == GameDamageType.FALL) {
			damage.cancel();
		}
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isRightClick() && !dwarf.getNoSpecial()) {
			return cooldown.tryUse();
		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		return cooldown.getCooldown();
	}
	
	private void dash() {
		dwarf.playSound("dash", 1f, 1f, true);
		dwarf.giveProc(dashProc);
		//dwarf.setVelocity(dwarf.getLocation().getDirection().setY(0).normalize().multiply(5));
		dwarf.leap(4, -0.1);
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
