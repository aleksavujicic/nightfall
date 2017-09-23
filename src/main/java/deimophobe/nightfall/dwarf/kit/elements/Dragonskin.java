package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Dragonskin extends AbstractToggleBow implements KitCooldownElement {
	
	Dragonskin(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 50;
	private final static CustomItem ITEM = DwarvenItems.getBow("dragonskin", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.BOW; }
	@Override public String getBowIdentifier() {return "DRAGONSKIN";}
	@Override public int getPower() {return POWER;}
	@Override public ItemStack getCooldownToggleItem() {return ITEM.createItemStack();}
	
	private ComplexCooldown cooldown = new ComplexCooldown(30*20);
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		cooldown.update();
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromBow(damage) && isActiveProjectile(damage.getArrow())) {
			damage.getArrowRes().timesMult(0.5);
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
			ArrowMisc.setGlowColour((Arrow) arrow, ChatColor.RED);
			ArrowMisc.setArrowDamage((Arrow) arrow, 125);
			cooldown.tryUse();
			updateActive();
		}
		return arrow;
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		if (damageFromBow(damage))
			dwarf.giveProc(ProcType.DRAGONSKIN);
	}
	
	@Override
	protected boolean canActivate() {
		return cooldown.isAvailable();
	}
}
