package deimophobe.dvz.dwarf.kit.elements;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.ProcType;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.MonsterManager;
import org.bukkit.*;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Ebow extends AbstractBow {
	Ebow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 10;
	private final static CustomItem ITEM = DwarvenItems.getBow("ebow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.BOW; }
	@Override public String getBowIdentifier() {return "EBOW";}
	@Override public int getPower() {return POWER;}
	
	private static final double MAX_RANGE = 25;
	private static final double THICKNESS = 1.5;
	private static final double MIN_DISTANCE_FROM_SHOOTER = 1;
	private static final double PROC_RADIUS = 3;
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		Location dwarfLocation = dwarf.getPlayer().getEyeLocation();
		double yaw = dwarfLocation.getYaw() * Math.PI/180;
		dwarfLocation.add(-0.4*Math.cos(yaw), -0.4, 0.4*Math.sin(yaw));
		Vector direction = dwarfLocation.getDirection();
		
		double range = MAX_RANGE * force * force;
		
		// Show particles
		Vector delta = direction.clone().multiply(0.5);
		int times = (int) (range/0.5);
		Location particlePos = dwarfLocation.clone();
		World world = particlePos.getWorld();
		for (int i = 0; i<= times; i++) {
			particlePos.add(delta);
			world.spawnParticle(Particle.VILLAGER_HAPPY, particlePos, 3, 0.1, 0.1, 0.1);
			
			// Stop beam if it hits a block
			if (particlePos.getBlock().getType().isSolid()) {
				range = dwarfLocation.distance(particlePos);
				break;
			}
		}
		
		// Calculate collision
		for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
			// Skip if further than distance shot or too close
			Location monsterLocation = monster.getEyeLocation();
			double distance = dwarfLocation.distance(monsterLocation);
			if (MIN_DISTANCE_FROM_SHOOTER <= distance && distance <= range) {
				// Find if close enough to beam
				Vector monsterOffset = monsterLocation.clone().subtract(dwarfLocation).toVector();
				Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
				double radialOffset = radialPostion.subtract(monsterOffset).length();
				
				// If close enough damage mob
				if (radialOffset <= THICKNESS) {
					monster.customDamage(dwarf, DamageType.EBOW, getPower()*force);
					
					for (Dwarf procDwarf : DwarfManager.getManager().getGamePlayers()) {
						if (procDwarf != dwarf && monsterLocation.distance(procDwarf.getLocation()) <= PROC_RADIUS)
							procDwarf.giveProc(ProcType.EBOW);
					}
				}
			}
		}
		
		/*
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(Game.getGame().getPlugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				Sound sound = event.getPacket().getSoundEffects().read(0);
				if (sound == Sound.ENTITY_ARROW_SHOOT) {
					event.getPacket().getSoundEffects().write(0, Sound.ENTITY_LEASHKNOT_BREAK);
					protocolManager.removePacketListener(this);
				}
			}
		});
		*/
		
		return null;
	}
}
