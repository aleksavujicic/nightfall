package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
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
	
	private static final double MAX_RANGE = 40;
	private static final double THICKNESS = 1.5;
	private static final double MIN_DISTANCE_FROM_SHOOTER = 1;
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		Location location = dwarf.getPlayer().getEyeLocation();
		double yaw = location.getYaw() * Math.PI/180;
		location.add(-0.3*Math.cos(yaw), -0.3, -0.3*Math.sin(yaw));
		Vector direction = location.getDirection();

		if (!dwarf.hasArrows(2)) return null;
		dwarf.useArrows(2);
		
		double range = MAX_RANGE * force * force;
		
		// Show particles
		Vector delta = direction.clone().multiply(0.33);
		int times = (int) (range/0.33);
		Location particlePos = location.clone();
		World world = particlePos.getWorld();
		for (int i = 0; i<= times; i++) {
			particlePos.add(delta);
			world.spawnParticle(Particle.VILLAGER_HAPPY, particlePos, 4, 0.1, 0.1, 0.1);
			
			// Stop beam if it hits a block
			if (particlePos.getBlock().getType().isSolid()) {
				range = location.distance(particlePos);
				break;
			}
		}
		
		// Calculate collision
		for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
			// Skip if further than distance shot or too close
			Location monsterLocation = monster.getEyeLocation();
			double distance = location.distance(monsterLocation);
			if (MIN_DISTANCE_FROM_SHOOTER <= distance && distance <= range) {
				// Find if close enough to beam
				Vector monsterOffset = monsterLocation.clone().subtract(location).toVector();
				Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
				double radialOffset = radialPostion.subtract(monsterOffset).length();
				
				// If close enough damage mob
				if (radialOffset <= THICKNESS) {
					monster.doDamage(dwarf, CustomDamageType.EBOW, getPower()*force);
					dwarf.playSound("entity.arrow.hit_player", 0.8f, 0.5f, false);
				}
			}
		}
		
		boolean gaveProc = false;
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			// Dont give proc to self
			if (dwarf == this.dwarf) continue;
			
			// Skip if further than distance shot or too close
			Location dwarfLoc = dwarf.getEyeLocation();
			double distance = location.distance(dwarfLoc);
			if (MIN_DISTANCE_FROM_SHOOTER <= distance && distance <= range) {
				// Find if close enough to beam
				Vector monsterOffset = dwarfLoc.clone().subtract(location).toVector();
				Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
				double radialOffset = radialPostion.subtract(monsterOffset).length();
				
				// If close enough to give dwarf proc
				if (radialOffset <= THICKNESS) {
					gaveProc = true;
					dwarf.giveProc(ProcType.EBOW);
				}
			}
		}
		
		if (gaveProc) {
			Sounds.DWARF_ITEM_EBOW_GIVE_PROC.playSound(dwarf);
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
