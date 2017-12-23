package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.HeroArmour;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class Hero extends Dwarf {
	protected final HeroType type;
	
	protected Hero(Player player, HeroType type) {
		super(player, type.getData());
		
		this.type = type;
		HeroData data = type.getData();
		
		setArmour(new HeroArmour(this, data.getHat()));
		
		Bukkit.broadcastMessage(
				ChatColor.DARK_AQUA + player.getName()
				+ ChatColor.LIGHT_PURPLE + " has become the "
				+ data.getDescriptor() + " " + player.getDisplayName()
				+ ChatColor.LIGHT_PURPLE + "!"
		);
		
		makeBlindImmune();
		makePlagueImmune();
		
		giveKitItems(KitGiveType.PICK);
		giveKitItems(KitGiveType.SHOVEL);
		
		SkinManager.getManager().addSkinChange(this, data.getSkin());
	}
	
	@Override
	public void onRemove() {
		super.onRemove();
		SkinManager.getManager().removeSkinChange(this);
	}
	
	@Override
	public void showTrash() {}
	
	@Override
	public void updateVisibility() {}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.multiplyManaDrain(0.2);
	}
	
	@Override
	public void regenMana(int amt) {
		super.regenMana(amt/3);
	}
}
