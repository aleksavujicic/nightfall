package deimophobe.nightfall.common.util;


import com.comphenix.protocol.events.PacketContainer;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.block.CraftBlock;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Deimophobe on 15/02/18.
 *
 * @deprecated Methods need to be updated each version, and may not always work. Use with caution
 */
@Deprecated
public class NMSUtil {
	public static int getPingOfPlayer(Player player) {
		if (player instanceof CraftPlayer) {
			return  ((CraftPlayer) player).getHandle().ping;
		}
		return 0;
	}
	
	public static void hideArrowsInPlayer(Player player) {
		// https://www.spigotmc.org/threads/removing-arrows.72723/#post-1666181
		if (player instanceof CraftPlayer) {
			((CraftPlayer) player).getHandle().getDataWatcher().set(new DataWatcherObject<>(10, DataWatcherRegistry.b), 0);
		}
	}
	
	public static HoverEvent createHoverEventForItem(ItemStack itemStack) {
		BaseComponent[] eventComponents = new BaseComponent[] {
				new TextComponent(convertItemStackToJson(itemStack))
		};
		return new HoverEvent(HoverEvent.Action.SHOW_ITEM, eventComponents);
	}
	
	private static String convertItemStackToJson(ItemStack itemStack) {
		net.minecraft.server.v1_13_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = new NBTTagCompound();
		compound = nmsItemStack.save(compound);
		return compound.toString();
	}
	
	public static void playBlockBreakSound(Block block) {
		double x = block.getX() + 0.5;
		double y = block.getY() + 0.5;
		double z = block.getZ() + 0.5;
		try {
			net.minecraft.server.v1_13_R2.Block nmsBlock = ((CraftBlock) block).getNMS().getBlock();
			
			Field soundEffectField = net.minecraft.server.v1_13_R2.Block.class.getDeclaredField("stepSound");
			soundEffectField.setAccessible(true);
			SoundEffectType effectType = (SoundEffectType) soundEffectField.get(nmsBlock);

			Field breakSoundField = SoundEffectType.class.getDeclaredField("q");
			breakSoundField.setAccessible(true);
			SoundEffect breakSound = (SoundEffect) breakSoundField.get(effectType);

			((CraftWorld) block.getWorld()).getHandle().a(null, x, y, z, breakSound, SoundCategory.BLOCKS, 1f, 0.8f);
		} catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
			NightfallCommonPlugin.logger().warning("Failed to play block break sound");
			e.printStackTrace();
		}
		
	}
	
	public static float getNumberAbsorptionHearts(LivingEntity entity) {
		return ((CraftLivingEntity) entity).getHandle().getAbsorptionHearts();
	}
	
	public static void setNumberAbsorptionHearts(LivingEntity entity, float hearts) {
		((CraftLivingEntity) entity).getHandle().setAbsorptionHearts(hearts);
	}
	
	public static void openBook(Player player, ItemStack book) {
		PlayerInventory inv = player.getInventory();
		ItemStack held = inv.getItemInMainHand();
		inv.setItemInMainHand(book);
		((CraftPlayer) player).getHandle().a(CraftItemStack.asNMSCopy(book), EnumHand.MAIN_HAND);
		inv.setItemInMainHand(held);
	}
	
	public static void updatePlayerHealth(Player player) {
		((CraftPlayer) player).updateScaledHealth();
	}
	
	
	public static MainHand getHandFromClientSettingsPacket(PacketContainer pc) {
		EnumMainHand hand = pc.getEnumModifier(EnumMainHand.class, 5).read(0);
		switch (hand) {
			case LEFT:
				return MainHand.LEFT;
			case RIGHT:
				return MainHand.RIGHT;
		}
		
		return null;
	}
	
	public static Byte getSkinSettingsOfPlayer(Player player) {
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		try {
			Field field = EntityHuman.class.getDeclaredField("bx");
			field.setAccessible(true);
			DataWatcherObject<Byte> dataWatcherObject = (DataWatcherObject<Byte>) field.get(null);
			
			return entityPlayer.getDataWatcher().get(dataWatcherObject);
		} catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
			NightfallCommonPlugin.logger().severe("Failed to get player skin settings of player '" + player.getName() + "'.");
			e.printStackTrace();
		}
		
		return null;
	}
}
