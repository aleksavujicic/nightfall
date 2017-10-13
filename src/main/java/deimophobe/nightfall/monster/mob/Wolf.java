package deimophobe.nightfall.monster.mob;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 19/01/17.
 */
class Wolf extends AbstractMob {
		
	private final ComplexCooldown leapCD = new ComplexCooldown(200);
	
	private final ComplexCooldown furySound;
	
	protected Wolf(MonsterPlayer monster) {
		this(monster, MobType.WOLF);
	}
	
	protected Wolf(MonsterPlayer monster, MobType type) {
		super(monster, type);
		
		if (isHellhound()) {
			furySound = new ComplexCooldown(25, () -> {
				monster.playSound("entity.wolf.growl", 1f, 0.85f, true);
				if (Game.getGame().isNight())
					monster.playSound("entity.zombie_villager.converted", 1f, 1.45f, true);
			}, ComplexCooldown.DO_NOTHING);
		} else {
			furySound = new ComplexCooldown(20, () -> {
				monster.playSound("entity.wolf.growl", 1f, 1f, true);
				if (Game.getGame().isNight())
					monster.playSound("entity.zombie_villager.converted", 1f, 1.5f, true);
			}, ComplexCooldown.DO_NOTHING);
			
		}
	}
	
	@Override
	public float getCooldown() {
		return leapCD.fractionComplete();
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		furySound.update();
		leapCD.update();
		if (quadSec)
			packBuff();
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
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK && isPlayerHoldingWeapon()) {
			if (leapCD.tryUse()) {
				// Play leap sound really loud to wolf player, but much quieter to everyone else.
				String wolfHowl = "entity.wolf.howl";
				ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
				protocolManager.addPacketListener(new PacketAdapter(NightfallPlugin.getPlugin(), PacketType.Play.Server.CUSTOM_SOUND_EFFECT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						String sound = event.getPacket().getStrings().read(0);
						if (sound.equals(wolfHowl) && event.getPlayer() == monster.getPlayer()) {
							event.setCancelled(true);
							protocolManager.removePacketListener(this);
						}
					}
				});
				float pitch = (isHellhound() ? 0.85f : 1f);
				monster.playSound(wolfHowl, 1, pitch, true);
				monster.playSound(wolfHowl, 1000, pitch, false);
				
				double yaw = monster.getPlayer().getLocation().getYaw();
				double radYaw = yaw*Math.PI/180;
				Vector velocity;
				if (monster.getPlayer().isSneaking()) {
					velocity = new Vector(-2 * Math.sin(radYaw), 1.7, 2 * Math.cos(radYaw));
				} else {
					velocity = new Vector(-5 * Math.sin(radYaw), 1.5, 5 * Math.cos(radYaw));
				}
				
				monster.getPlayer().setVelocity(velocity);
			}
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		Dwarf dwarf = damage.getDwarf();
		if (dwarf != null) {
			double heal;
			if (isHellhound())
				heal = 6;
			else
				heal = 6;
			
			monster.heal(heal);
			monster.givePotionEffect(PotionEffectType.SPEED, 120, 3, true, false, true);
			
			damage.setManaDrain(10);
			
			furySound.tryUse();
		}
	}
	
	private boolean isHellhound() {
		return (this instanceof Hellhound);
	}
	
	private void packBuff() {
		int wolfCount = 0;
		for (MonsterPlayer monster : MonsterManager.getManager().getAlivePlayerMobs()) {
			if (monster == this.monster) continue;
			if (monster.getMob() instanceof Wolf) {
				if (monster.getLocation().distance(this.monster.getLocation()) <= 6) {
					wolfCount++;
					
					if (wolfCount == 5) break;
				}
			}
		}
		if (wolfCount == 0) return;
		monster.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, 5*20, wolfCount, true, true, true);
	}
}
