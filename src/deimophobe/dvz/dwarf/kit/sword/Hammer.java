package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.PlayerOrAI;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.PlayerMonster;
import org.bukkit.Bukkit;
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
	
	@Override
	public void onHit(PlayerOrAI event) {
		//Location loc = event.getEntity().getLocation();
		//Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 1, 1, 1);
		//for (Entity entity : entities) {
		//	if (entity.getType() != EntityType.PLAYER && entity instanceof LivingEntity) {
		//		((LivingEntity) entity).damage(5, dwarf.getPlayer());
		//	}
		//}
		reduceCooldown(20);
	}
	
	@Override
	public void update() {
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
				dwarf.repairArmour(5);
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
