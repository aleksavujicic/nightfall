package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.LampBlock;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Lightbow extends AbstractBow {
	private final static int POWER = 40;
	private final static CustomItem ITEM = getBow("lightbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "LIGHTBOW";}
	@Override public int getPower() {return POWER;}
	
	public Lightbow(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
		dwarf.makeBlindImmune();
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (!isRangedDamageFromBow(damage)) return;
		if (!damage.hasArrow()) return;
		
		Arrow arrow = damage.getArrow();
		if (ArrowMisc.getArrowForce(arrow) > 0.8 && !damage.getReceiver().isAI()) {
			damage.addPostDamageHandler(() -> {
				damage.getReceiver().givePotionEffect(PotionEffectType.GLOWING, 3*20, 1, true, false, true);
			});
		}
	}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock, BlockFace hitFace) {
		if (ArrowMisc.getArrowForce((Arrow) arrow) >= 0.8)
			BlockManager.getManager().placeTimedBlock(new LampBlock(hitBlock, 10*20, dwarf, false));
	}
}
