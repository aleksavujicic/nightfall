package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/06/17.
 */
public class TestMob extends AbstractMob {
	
	protected TestMob(MonsterPlayer monster) {
		super(monster, MobType.TESTMOB);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		ItemStack item = ItemManager.getManager().getItem("mob.krungor.weapon").createItemStack();
		
		TextComponent text = new TextComponent("Scary Item");
		text.setColor(ChatColor.RED);
		
		BaseComponent[] eventComponents = new BaseComponent[] {
				new TextComponent(convertItemStackToJson(item))
		};
		HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, eventComponents);
		text.setHoverEvent(event);
		
		monster.getPlayer().spigot().sendMessage(text);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.softCancel();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.softCancel();
	}
	
	public String convertItemStackToJson(ItemStack itemStack) {
		net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
		net.minecraft.server.v1_12_R1.NBTTagCompound compound = new NBTTagCompound();
		compound = nmsItemStack.save(compound);
		return compound.toString();
	}
}
