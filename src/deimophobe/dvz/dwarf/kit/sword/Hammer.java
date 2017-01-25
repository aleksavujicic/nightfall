package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.PlayerOrAI;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.AIEntity;
import deimophobe.dvz.monster.MobManager;
import deimophobe.dvz.monster.PlayerMonster;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Hammer extends Sword {
	
	private int updateCount = 0;
	
	
	Hammer(Dwarf dwarf) {
		super(dwarf, SwordType.HAMMER, 60);
	}
	
	//TODO?
	//@Override
	//public void onKill() {}
	
	private boolean hasHit = false;
	private static final double AOE_RADIUS = 2.5;
	@Override
	public void onHit(PlayerOrAI monster) {
		if (hasHit) return;
		
		hasHit = true;
		final double monsterDmg = (dwarf.hasProc() ? 20 : 5);
		final double aiDmg = (dwarf.hasProc() ? 40 : 20);
		Location center = monster.getEntity().getLocation();
		for (PlayerMonster playerMonster : MobManager.getManager().getMobs()) {
			if (playerMonster == monster) continue;
			if (center.distance(playerMonster.getLocation()) <= AOE_RADIUS)
				playerMonster.damage(monsterDmg, dwarf);
		}
		for (AIEntity ai : MobManager.getManager().getAIs()) {
			if (center.distance(ai.getEntity().getLocation()) <= AOE_RADIUS)
				ai.getEntity().damage(aiDmg, dwarf.getPlayer());
		}
		reduceCooldown(20);
	}
	
	@Override
	public void update() {
		hasHit = false;
		if (!dwarf.getPlayer().isBlocking()) {
			if (cooldown > 0)
				cooldown -= 1;
		} else {
			cooldown += 1;
			if (cooldown > maxCooldown) cooldown = maxCooldown;
			dwarf.updateCooldownBar();
			
			updateCount += 1;
			if (updateCount < 5) return;
			updateCount = 0;
			
			if (cooldown == maxCooldown) {
				dwarf.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
				dwarf.repairArmour(3);
				dwarf.regenMana(1);
				
				if (cooldown >= maxCooldown) cooldown = maxCooldown;
			}
		}
	}
	
	@Override
	public float fractionComplete() {
		return (float)cooldown/60;
	}
}
