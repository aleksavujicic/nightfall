package deimophobe.nightfall.util;

import com.comphenix.packetwrapper.WrapperPlayServerBlockAction;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import deimophobe.nightfall.NightfallPlugin;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 28/02/18.
 */
public class PacketUtil {
	public static void setChestOpen(Block block, boolean open) {
		WrapperPlayServerBlockAction packet = new WrapperPlayServerBlockAction();
		packet.setBlockType(Material.CHEST);
		packet.setLocation(new BlockPosition(block.getX(), block.getY(), block.getZ()));
		packet.setByte1(1);
		packet.setByte2(open ? 1 : 0);
		packet.broadcastPacket();
	}
	
	public static void setupListeners() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		
		// Hide arrows in offhand
		protocolManager.addPacketListener(new PacketAdapter(NightfallPlugin.getPlugin(), PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				EnumWrappers.ItemSlot slot = event.getPacket().getItemSlots().read(0);
				if (slot == EnumWrappers.ItemSlot.OFFHAND && event.getPacket().getItemModifier().read(0).getType() == Material.ARROW) {
					event.setCancelled(true);
				}
			}
		});
		
		// Disable certain sounds
		protocolManager.addPacketListener(new PacketAdapter(NightfallPlugin.getPlugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				Sound sound = event.getPacket().getSoundEffects().read(0);
				switch (sound) {
					case ENTITY_PLAYER_ATTACK_CRIT:
					case ENTITY_PLAYER_ATTACK_KNOCKBACK:
					case ENTITY_PLAYER_ATTACK_NODAMAGE:
					case ENTITY_PLAYER_ATTACK_STRONG:
					case ENTITY_PLAYER_ATTACK_SWEEP:
					case ENTITY_PLAYER_ATTACK_WEAK:
					case ITEM_ARMOR_EQUIP_CHAIN:
					case ITEM_ARMOR_EQUIP_DIAMOND:
					case ITEM_ARMOR_EQUIP_IRON:
					case ITEM_SHOVEL_FLATTEN:
					case ITEM_HOE_TILL:
					
					case ENTITY_ZOMBIE_DEATH:
					case ENTITY_SKELETON_DEATH:
						event.setCancelled(true);
				}
			}
		});
		
		// Prevent enderchests from self closing
		protocolManager.addPacketListener(new PacketAdapter(NightfallPlugin.getPlugin(), PacketType.Play.Server.BLOCK_ACTION) {
			@Override
			public void onPacketSending(PacketEvent event) {
				Material blockType = event.getPacket().getBlocks().read(0);
				if (blockType == Material.ENDER_CHEST) {
					event.setCancelled(true);
				}
			}
		});
	}
}
