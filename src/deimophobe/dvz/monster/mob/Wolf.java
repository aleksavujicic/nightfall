package deimophobe.dvz.monster.mob;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 19/01/17.
 */
class Wolf extends Mob {
	private static final int LEAP_MAX_CD = 0;//140;
	private int leapCd = 0;
	
	private final boolean dire;
	Wolf(MonsterPlayer monster, boolean dire) {
		super(monster, (dire ? MobType.DIREWOLF : MobType.WOLF ));
		this.dire = dire;
		
		Disguise disguise = getDisguise();
		FlagWatcher watcher = disguise.getWatcher();
		if (watcher instanceof WolfWatcher) {
			((WolfWatcher) watcher).setAngry(dire);
		} else {
			Bukkit.getLogger().warning("Wolf not disguised as wolf?");
		}
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float)leapCd/LEAP_MAX_CD;
	}
	
	@Override
	public void update() {
		leapCd--;
		if (leapCd <= 0) leapCd = 0;
	}
	
	@Override
	public void onShift(boolean sneaking) {
		Disguise disguise = DisguiseAPI.getDisguise(monster.getPlayer());
		FlagWatcher watcher = disguise.getWatcher();
		if (watcher instanceof WolfWatcher) {
			((WolfWatcher) watcher).setSitting(sneaking);
		} else {
			Bukkit.getLogger().warning("Wolf not disguised as wolf?");
		}
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			if (leapCd == 0 && isPlayerHoldingItem(0)) {
				leapCd = LEAP_MAX_CD;
				
				// Play leap sound really loud to wolf player, but much quieter to everyone else.
				String wolfHowl = "entity.wolf.howl";
				ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
				protocolManager.addPacketListener(new PacketAdapter(Game.getGame().getPlugin(), PacketType.Play.Server.CUSTOM_SOUND_EFFECT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						String sound = event.getPacket().getStrings().read(0);
						if (sound.equals(wolfHowl) && event.getPlayer() == monster.getPlayer()) {
							event.setCancelled(true);
							protocolManager.removePacketListener(this);
						}
					}
				});
				monster.playSound(wolfHowl, 2, 1, true);
				monster.playSound(wolfHowl, 10, 1, false);
				
				double yaw = monster.getPlayer().getLocation().getYaw();
				double radYaw = yaw*Math.PI/180;
				Vector velocity;
				if (monster.getPlayer().isSneaking()) {
					velocity = new Vector(-2 * Math.sin(radYaw), 1.7, 2 * Math.cos(radYaw));
				} else {
					velocity = new Vector(-5 * Math.sin(radYaw), 1.5, 5 * Math.cos(radYaw));
				}
				
				//monster.getPlayer().setVelocity(velocity);
			}
		}
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		if (dwarf != null) {
			monster.heal(5);
			monster.playSound("entity.wolf.growl", 3, 1, true);
			monster.givePotionEffect(PotionEffectType.SPEED, 140, 3, true, true, true);
		}
		return damage;
	}
			
}
