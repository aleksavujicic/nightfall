package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import deimophobe.dvz.effects.GameEffect;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class Hero extends Dwarf {
	
	public Hero(Player player, Type type) {
		super(player, type.getData());
		
		
		
		announceHero();
	}
	
	private void announceHero() {
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.LIGHT_PURPLE + " has become the hero " + player.getDisplayName());
	}
	
	@Override
	protected void giveStartingItems(Map<ConsumableType, Integer> consumables) {
		getKit().giveAllItems();
		super.giveStartingItems(consumables);
	}
	
	@Override
	protected void naturalManaRegen() {
		boolean inShrine = ShrineManager.getManager().getShrineRegion().continsGameEntity(this);
		boolean shrineHasGold = ShrineManager.getManager().hasGold();
		int regenRate;
		if (inShrine && shrineHasGold) {
			regenRate = 10;
		} else {
			regenRate = 2;
		}
		regenMana(regenRate);
	}
	
	@Override
	public void updateVisibility() {}
	
	
	// TODO: put into dwarf subclass
	@Override
	public boolean isArmoured() { return true; }
	@Override
	public void putOnArmour() {}
	@Override
	public void damageArmour(int dmg) {}
	@Override
	public void repairArmour(int amt) {}
	@Override
	public boolean isMaxArmour() {
		return true;
	}
	@Override
	public void updateArmour() {
		updateArmourBar();
	}
	@Override
	public void updateArmourBar() {
		player.setFoodLevel((int) Math.ceil(20f));
	}
	
	@Override
	protected double getDamageReduction() {
		return 0.92;
	}
	
	
	private static final Map<ConsumableType, Integer> HERO_CONSUMABLES = new HashMap<>();
	private static final Set<Passive> HERO_PASSIVES = new HashSet<>();
	static {
		HERO_CONSUMABLES.put(ConsumableType.WIZARD_MORTAR, 30);
		HERO_PASSIVES.add(Passive.HERO_SAFEFALL);
	}
	
	public enum Type {
		HAMMER_HERO("Riemann The Ploodin", null, SwordType.HAMMER, BowType.LIGHTBOW),
		;
		
		private final DwarfData data;
		
		Type(String name, ItemStack hat, SwordType sword, BowType bow) {
			this.data = new DwarfData(name, true, hat, sword, bow, AleType.HERO, null, HERO_CONSUMABLES, HERO_PASSIVES);
		}
		
		public DwarfData getData() {return data;}
		
		public static Type getHeroType(String arg) {
			for (Type type : values()) {
				if (type.toString().equalsIgnoreCase(arg))
					return type;
			}
			return null;
		}
	}
}
