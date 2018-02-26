package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractCooldownItem;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Dagger extends AbstractCooldownItem {
	
	public Dagger(Dwarf dwarf) {
		super(dwarf, 240*20);
	}
	
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "dagger");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	@Override
	public void onKill(MonsterDamage damage) {
		reduceCooldown(60);
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
	}
	
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			MonsterPlayer closestMonster = dwarf.getLookingAt(5, 2.5, MonsterManager.getManager().getAlivePlayerMobs());
			
			if (closestMonster != null) {
				boolean success = closestMonster.doDamage(dwarf, GameDamageType.EVISCERATE, 200, true);
				if (success) {
					Location location = closestMonster.getPlayer().getEyeLocation();
					location.subtract(0, 0.5, 0);
					World world = location.getWorld();
					
					world.spawnParticle(Particle.SMOKE_NORMAL, location, 20, 0.3, 0.3, 0.3, 0.05);
					Misc.spawnColouredParticles(location, 20, 0.6, 0.6, 0.6, Color.fromRGB(250, 250, 250));
					dwarf.playSound("entity.wither.shoot", 1f, 1.5f, true);
					resetCooldown();
				}
			}
			return true;
		}
		return false;
	}
}
