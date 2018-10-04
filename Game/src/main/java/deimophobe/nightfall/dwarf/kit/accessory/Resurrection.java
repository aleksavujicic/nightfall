package deimophobe.nightfall.dwarf.kit.accessory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.PreDamagePriority;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Deimophobe on 1/11/17.
 */
public class Resurrection extends AbstractPiece {
	
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
					dwarf.addShields(3);
					dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 5, true, false, true);
					dwarf.givePotionEffect(PotionEffectType.REGENERATION, 8 * 20, 4, true, false, false);
					dwarf.giveProc(ProcType.RESURRECTION);
					
					dwarf.playSound("item.totem.use", 1f, 1f, true);
					new BukkitRunnable() {
						private int life = 40;
						
						@Override
						public void run() {
							if (!dwarf.isOnline()) {
								cancel();
								return;
							}
							
							World world = dwarf.getWorld();
							world.spawnParticle(Particle.END_ROD, dwarf.getEyeLocation().subtract(0, 0.3, 0), 1, 0.5, 0.5, 0.5, 0.1);
							world.spawnParticle(Particle.TOTEM, dwarf.getEyeLocation().subtract(0, 0.3, 0), 5, 0.5, 0.5, 0.5, 0.1);
							
							life--;
							if (life <= 0)
								this.cancel();
						}
					}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 1);
					
					// Send animation packet
					ProtocolManager pm = ProtocolLibrary.getProtocolManager();
					PacketContainer pc = pm.createPacket(PacketType.Play.Server.ENTITY_STATUS);
					pc.getIntegers().write(0, dwarf.getPlayer().getEntityId());
					pc.getBytes().write(0, (byte) 35);
					try {
						pm.sendServerPacket(dwarf.getPlayer(), pc);
					} catch (InvocationTargetException e) {
						NightfallPlugin.logger().severe("Exception sending resurrection animation packet");
						e.printStackTrace();
					}
				}
			});
		}
	}
}
