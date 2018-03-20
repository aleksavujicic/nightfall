package deimophobe.nightfall.dwarf.kit.accessory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.CancellableFinalGameDamage;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.plague.AssassinPlague;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 1/11/17.
 */
public class Resurrection extends AbstractPiece {
	
	private boolean used = false;
	
	public Resurrection(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void damageNotify(DwarfDamage damage) {
		super.damageNotify(damage);
		
		if (!used && Game.getGame().getPhase() == Phase.GAME) {
			damage.addPreDamageHandler(GameDamage.RESURRECTION_PRIORITY, resurrecter);
		}
	}
	
	private final Consumer<CancellableFinalGameDamage<GameEntity<?>, Dwarf>> resurrecter = damage -> {
		if (damage.willKill() && !Game.getGame().potionsDisabled()) {
			used = true;
			damage.softCancel();
			
			dwarf.getArmour().addModifier(ItemModifierType.HEALTH, -5, "Resurrection");
			dwarf.getArmour().repair(500);
			dwarf.regenMana(500);
			dwarf.healMax();
			dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 3 * 20, 5, true, false, true);
			
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
				Bukkit.getLogger().severe("Exception sending animation packet");
				e.printStackTrace();
			}
		}
	};
}
