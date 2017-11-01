package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 1/11/17.
 */
class Resurrection extends AbstractElement {
	
	private boolean used = false;
	
	public Resurrection(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void damageNotify(DwarfDamage damage) {
		super.damageNotify(damage);
		
		if (!used && !canJJHeal(damage) && damage.willKill()) {
			used = true;
			
			dwarf.getArmour().addModifier(ItemModifierType.HEALTH, -5, "Resurrection");
			dwarf.getArmour().repair(500);
			dwarf.regenMana(500);
			dwarf.healMax();
			dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 3*20, 5, true, false, true);
			
			dwarf.playSound("item.totem.use", 1f, 1f, true);
			new BukkitRunnable() {
				private int life = 40;
				@Override
				public void run() {
					World world = dwarf.getWorld();
					world.spawnParticle(Particle.END_ROD, dwarf.getEyeLocation().subtract(0,0.3,0), 1, 0.5, 0.5, 0.5, 0.1);
					world.spawnParticle(Particle.TOTEM, dwarf.getEyeLocation().subtract(0,0.3,0), 5, 0.5, 0.5, 0.5, 0.1);
					
					life--;
					if (life <= 0)
						this.cancel();
				}
			}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 1);
			
			damage.softCancel();
		}
	}
	
	private boolean canJJHeal(DwarfDamage damage) {
		return dwarf.hasKitElement(KitElementType.JIMMY_JUICE) && dwarf.hasMana(120) && damage.getFinalDamage() < dwarf.getMaxHealth();
	}
}
