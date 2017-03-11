package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.Hat;
import deimophobe.dvz.Skin;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import deimophobe.dvz.shrine.ShrineManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
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
	
	private final Type type;
	
	public Hero(Player player, Type type) {
		super(player, type.getData());
		
		this.type = type;
		
		Disguise disguise = type.getDisguise();
		if (disguise != null)
			DisguiseAPI.disguiseEntity(player, disguise);
		
		announceHero();
	}
	
	private void announceHero() {
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.LIGHT_PURPLE + " has become the dwarven hero " + player.getDisplayName());
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
		return 0.84;
	}
	
	
	private static final Map<ConsumableType, Integer> HERO_CONSUMABLES = new HashMap<>();
	private static final Set<Passive> HERO_PASSIVES = new HashSet<>();
	static {
		HERO_CONSUMABLES.put(ConsumableType.WIZARD_MORTAR, 32);
		HERO_CONSUMABLES.put(ConsumableType.MORTAR, 64);
		HERO_CONSUMABLES.put(ConsumableType.LAMP, 20);
		HERO_CONSUMABLES.put(ConsumableType.SOS, 3);
		HERO_PASSIVES.add(Passive.HERO_SAFEFALL);
	}
	
	public enum Type {
		TUI("Tui the Lightbringer", Hat.TUI, SwordType.TUI_HAMMER, BowType.WILDFIRE, "tui", "Tui"),
		NOSOVIN("Nosovin's Illustration", Hat.TUI, SwordType.TINDERFLAME, BowType.WILDFIRE, "tui", "Nosovin"),
		;
		
		private final DwarfData data;
		private final String skin;
		private final String nametag;
		
		Type(String name, Hat hat, SwordType sword, BowType bow, String skin, String nametag) {
			this.data = new DwarfData(name, true, hat, sword, bow, AleType.HERO, null, HERO_CONSUMABLES, HERO_PASSIVES);
			this.skin = skin;
			this.nametag = nametag;
		}
		
		public DwarfData getData() {return data;}
		
		public Disguise getDisguise() {
			PlayerDisguise disguise = Skin.getDisguiseWithSkin(skin, ChatColor.GOLD + nametag);
			disguise.setKeepDisguiseOnPlayerDeath(false);
			disguise.setKeepDisguiseOnPlayerLogout(true);
			disguise.setViewSelfDisguise(false);
			disguise.setDisplayedInTab(true);
			return disguise;
		}
		
		public static Type getHeroType(String arg) {
			for (Type type : values()) {
				if (type.toString().equalsIgnoreCase(arg))
					return type;
			}
			return null;
		}
	}
}
