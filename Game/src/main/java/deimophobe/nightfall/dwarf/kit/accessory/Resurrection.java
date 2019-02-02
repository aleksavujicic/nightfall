package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.PreDamagePriority;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.game.entity.ShieldSource;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 1/11/17.
 */
public class Resurrection extends AbstractPiece implements ArmourPiece {
	
	private boolean used = false;
	
	public Resurrection(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		
		if (!used && Game.getGame().getPhase() == Phase.GAME) {
			damage.addPreDamageHandler(PreDamagePriority.RESURRECTION, () -> {
				if (damage.willKill() && !Game.getGame().potionsDisabled()) {
					used = true;
					damage.softCancel();
					
					Armour armour = dwarf.getArmour();
					if (armour instanceof DwarvenArmour && !armour.isArmoured()) {
						((DwarvenArmour) armour).putOn();
					}
					
					armour.addModifier(ItemModifierType.HEALTH, -4, "Resurrection");
					armour.repair(1000);
					dwarf.regenMana(1000);
					dwarf.healMax();
					dwarf.addMaxShields(ShieldSource.RESURRECTION);
					dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 5, true, false, true);
					dwarf.givePotionEffect(PotionEffectType.REGENERATION, 8 * 20, 4, true, false, false);
					dwarf.giveProc(ProcType.RESURRECTION);
					
					dwarf.playSound("item.totem.use", 1f, 1f, true);
					dwarf.addUpdateable(
						new LifetimeExpireable(40) {
							@Override
							public void update() {
								super.update();
								World world = dwarf.getWorld();
								world.spawnParticle(Particle.END_ROD, dwarf.getEyeLocation().subtract(0, 0.3, 0), 1, 0.5, 0.5, 0.5, 0.1);
							}
						}
					);
					dwarf.setEntityStatus((byte) 35);
				}
			});
		}
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.RESURRECTION, 1);
	}
}
