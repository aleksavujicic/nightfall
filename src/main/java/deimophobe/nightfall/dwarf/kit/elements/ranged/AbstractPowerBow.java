package deimophobe.nightfall.dwarf.kit.elements.ranged;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 28/11/17.
 */
public abstract class AbstractPowerBow extends AbstractToggleBow implements KitCooldownElement {
	
	private final ComplexCooldown cooldown;
	private final ChatColor arrowColour;
	private final double fullArrowDamage;
	private final double arrowResMult;
	private final ProcType proc;
	
	public AbstractPowerBow(Dwarf dwarf, int cooldown, ChatColor arrowColour, double fullArrowDamage, double arrowResMult, ProcType proc) {
		super(dwarf);
		this.cooldown = new ComplexCooldown(cooldown, null, this::offCDSound);
		this.arrowColour = arrowColour;
		this.fullArrowDamage = fullArrowDamage;
		this.arrowResMult = arrowResMult;
		this.proc = proc;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		cooldown.update();
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromBow(damage) && isActiveProjectile(damage.getArrow())) {
			damage.getArrowRes().timesMult(arrowResMult);
		}
	}
	
	@Override
	public float fractionComplete() {
		return cooldown.fractionComplete();
	}
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		arrow = super.onBowFire(arrow, force);
		if (isActive()) {
			ArrowMisc.setGlowColour((Arrow) arrow, arrowColour);
			ArrowMisc.setArrowDamage((Arrow) arrow, fullArrowDamage);
			cooldown.tryUse();
			updateActive();
		}
		return arrow;
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		if (damageFromBow(damage) && isActiveProjectile(damage.getArrow()))
			dwarf.giveProc(proc);
	}
	
	@Override
	protected boolean canActivate() {
		return cooldown.isAvailable();
	}
	
	
	private void offCDSound() {
		dwarf.playSound("offcd", 1, 1.5f, false);
		new BukkitRunnable() {
			@Override
			public void run() {
				dwarf.playSound("offcd", 1, 2f, false);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 5);
	}
}
