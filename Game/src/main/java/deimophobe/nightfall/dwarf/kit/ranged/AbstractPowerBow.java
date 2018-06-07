package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.death.DeathMessageMaker;
import deimophobe.nightfall.damage.death.KeywordDeathMessageMaker;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 28/11/17.
 */
public abstract class AbstractPowerBow extends AbstractToggleBow implements CooldownPiece {
	
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
	public void update() {
		super.update();
		cooldown.update();
	}
	
	private static final DeathMessageMaker POWER_DEATH_MSG = new KeywordDeathMessageMaker("power shot");
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isRangedDamageFromBow(damage) && isActiveProjectile(damage.getArrow())) {
			damage.getArrowRes().timesMult(arrowResMult);
			damage.setDeathMessageMaker(POWER_DEATH_MSG);
			
			if (damage.getMonster().isAI()) {
				damage.instaKill();
			}
		}
	}
	
	@Override
	public float getCooldown() {
		return cooldown.getCooldown();
	}
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		arrow = super.onBowFire(arrow, force);
		if (isActive() && arrow instanceof Arrow) {
			ArrowMisc.setGlowColour((Arrow) arrow, arrowColour);
			ArrowMisc.setArrowDamage((Arrow) arrow, fullArrowDamage);
			ArrowMisc.setArrowForce((Arrow) arrow, 1);
			((Arrow) arrow).setCritical(true);
			onPowerFire((Arrow) arrow);
			cooldown.reset();
			updateActive();
		}
		return arrow;
	}

	@Override
	public void onKill(MonsterDamage damage) {
		if (isRangedDamageFromBow(damage) && isActiveProjectile(damage.getArrow()))
			dwarf.giveProc(proc);
	}
	
	@Override
	protected boolean canActivate() {
		return cooldown.isAvailable();
	}
	
	protected void onPowerFire(Arrow poweredArrow) { }
	
	
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
