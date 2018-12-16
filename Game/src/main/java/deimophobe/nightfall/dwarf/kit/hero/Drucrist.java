package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Drucrist extends AbstractItem implements CooldownPiece {


	public Drucrist(Dwarf dwarf) {
		super(dwarf);
	}

	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "drucrist");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	
	@Override public KitGiveType getGiveType() {
		return  KitGiveType.START;
	}

	private final ComplexCooldown cd = new ComplexCooldown(20*20);


	@Override
	public void update() {
		super.update();
		cd.update();
	}


	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isRightClick()) {
			if (cd.isAvailable()) {
				MonsterPlayer closestPlayerMonster = dwarf.getLookingAt(13, 2.5, MonsterManager.getManager().getAlivePlayerMobs());

				AIEntity closestAIMonster = dwarf.getLookingAt(13, 2.5, AIManager.getManager().getAIs());

				if (closestPlayerMonster != null) {
					Location monsterLoc = closestPlayerMonster.getLocation();

					Vector lookDir = monsterLoc.getDirection().setY(0);
					Location newLoc = monsterLoc.subtract(lookDir);

					if (!newLoc.getBlock().getType().isSolid()) {
						closestPlayerMonster.doDamage(dwarf, GameDamageType.TEMPORARY, 100, true);
						dwarf.teleportTo(newLoc);
						dwarf.playSound("entity.enderman.teleport", 1, 1, true);
						cd.reset();
					}
				}
				else if (closestAIMonster != null) {
					Location monsterLoc = closestAIMonster.getLocation();

					Vector lookDir = monsterLoc.getDirection().setY(0);
					Location newLoc = monsterLoc.subtract(lookDir);

					if (!newLoc.getBlock().getType().isSolid()) {
						closestAIMonster.doDamage(dwarf, GameDamageType.TEMPORARY, 40, true,true);
						dwarf.teleportTo(newLoc);
						dwarf.playSound("entity.enderman.teleport", 1, 1, true);
						cd.reset();
						cd.setMaxCD(10*20);
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onKill(MonsterDamage damage){
		cd.reduceCooldown(2*10);
	}

	@Override
	public float getCooldown() {
		return cd.getCooldown();
	}
}
