package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.Game;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.CooldownItem;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.monster.PlayerMonster;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 16/01/17.
 */
public class Sword extends CooldownItem {
	
	private final SwordType type;
	
	Sword(Dwarf dwarf, SwordType type, int maxCooldown) {
		super(dwarf, getItem(type), maxCooldown);
		this.type = type;
	}
	
	public SwordType getType() {
		return type;
	}
	
	@Override
	protected boolean ability(Action type) {
		if (isRightClick(type)) {
			return ability();
		} else {
			return false;
		}
	}
	
	protected boolean ability() {return false;}
	public void onHit(PlayerMonster monster) {}
	public void onKill(PlayerMonster monster) {}
	
	@Override
	protected void playOffCDSound() {
		dwarf.playSound("offcd", 1, 1.5f, false);
		new BukkitRunnable() {
			@Override
			public void run() {
				dwarf.playSound("offcd", 1, 2f, false);
			}
		}.runTaskLater(Game.getGame().getPlugin(), 5);
	}
	
	public static Sword createSword(Dwarf dwarf, SwordType swordType) {
		switch (swordType) {
			case DRB:
				return new Sword(dwarf, SwordType.DRB, -1);
			case GRB:
				return new GRB(dwarf);
			case AXE_OF_MALICE:
				return new AxeOfMalice(dwarf);
			case HAMMER:
				return new Hammer(dwarf);
			case DAGGER:
				return new Dagger(dwarf);
			case TOMBMAKER:
				return new Tombmaker(dwarf);
		}
		return null;
	}
	
	private static final Map<SwordType, ItemStack> swords = new HashMap<>();
	static {
		ConfigurationSection swordSection = DwarfManager.getManager().getConfig().getConfigurationSection("sword");
		
		swords.put(SwordType.DRB,ItemCreator.createItem(swordSection.getConfigurationSection("drb"), Slot.MAIN_HAND));
		swords.put(SwordType.GRB,ItemCreator.createItem(swordSection.getConfigurationSection("grb"), Slot.MAIN_HAND));
		swords.put(SwordType.AXE_OF_MALICE,ItemCreator.createItem(swordSection.getConfigurationSection("axe"), Slot.MAIN_HAND));
		swords.put(SwordType.HAMMER,ItemCreator.createItem(swordSection.getConfigurationSection("hammer"), Slot.MAIN_HAND));
		swords.put(SwordType.DAGGER,ItemCreator.createItem(swordSection.getConfigurationSection("dagger"), Slot.MAIN_HAND));
		swords.put(SwordType.TOMBMAKER,ItemCreator.createItem(swordSection.getConfigurationSection("tombmaker"), Slot.MAIN_HAND));
	}
	public static ItemStack getItem(SwordType swordType) {
		return swords.get(swordType);
	}
}
