package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Crossbow extends AbstractBow implements CooldownPiece {
	public Crossbow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final int POWER = 90;
	private static final int RAPID_POWER = 25;
	private static final String RAPID_META = "rapid";
	private static final CustomItem ITEM = getBow("crossbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	
	@Override public String getBowIdentifier() {return "CROSSBOW";}
	
	private ComplexCooldown arrowCD = new ComplexCooldown(35, this::fireNormalArrow);
	
	private boolean firing = false;
	private ComplexCooldown rapidCD = new ComplexCooldown(4, this::fireRapidArrow);
	private ComplexCooldown longRapid = new ComplexCooldown(30*20, this::startFiring);
	
	private final static int ARROW_COST = 2;
	
	@Override
	public void update() {
		arrowCD.update();
		rapidCD.update();
		longRapid.update();
		
		if (firing)
			rapidCD.tryUse();
	}
	
	@Override
	public float getCooldown() {
		return longRapid.getCooldown();
	}
	
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (firing) {
			return false;
		}
		
		
		if (click.isRightClick()) {
			if (dwarf.hasArrows(ARROW_COST)) {
				arrowCD.tryUse();
				return true;
			}
		} else if (dwarf.hasArrows(1)) {
			longRapid.tryUse();
			return true;
		}
		return false;
	}
	
	@Override
	public int getPower() {
		return POWER;
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isRangedDamageFromBow(damage) && damage.getArrow().hasMetadata(RAPID_META)) {
			damage.getMultiPartDamage().setBase(RAPID_POWER);
			damage.multiplyKnockback(0.5);
			damage.setNoDamageTicks(5);
		}
	}
	
	private void fireNormalArrow() {
		Arrow arrow = fireArrow(3f, 1, 0.05f);
		arrow.setCritical(true);
		dwarf.useArrows(ARROW_COST);
		dwarf.playSound("entity.arrow.shoot", 1f, 1.1f, true);
		dwarf.playSound("entity.shulker.shoot", 1f, 0.8f, true);
	}
	
	private void startFiring() {
		firing = true;
		fireRapidArrow();
		rapidCD.reset();
	}
	
	private void stopFiring() {
		firing = false;
	}
	
	private void fireRapidArrow() {
		if (dwarf.hasArrows(1) && isHoldingItem()) {
			dwarf.useArrow();
			Arrow arrow = fireArrow(3f, 1, 4f);
			arrow.setMetadata(RAPID_META, new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
			dwarf.playSound("entity.arrow.shoot", 1f, 0.9f, true);
		} else {
			stopFiring();
		}
	}
}
