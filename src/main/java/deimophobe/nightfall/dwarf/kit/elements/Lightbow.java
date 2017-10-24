package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.blocks.timedblock.LampBlock;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Lightbow extends AbstractBow {
	Lightbow(Dwarf dwarf) {
		super(dwarf);
		dwarf.makeBlindImmune();
	}
	
	private final static int POWER = 30;
	private final static CustomItem ITEM = DwarvenItems.getBow("lightbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "LIGHTBOW";}
	@Override public int getPower() {return POWER;}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromBow(damage)) {
			if (damage.getMonster() instanceof AIEntity) {
				if (ArrowMisc.getArrowForce(damage.getArrow()) >= 0.5)
					damage.instaKill();
			} else {
				damage.getArrowRes().timesMult(0.5);
			}
		}
	}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
		if (ArrowMisc.getArrowForce((Arrow) arrow) >= 0.8)
			TimedBlock.placeTimedBlock(new LampBlock(hitBlock, 10*20, dwarf));
	}
}
