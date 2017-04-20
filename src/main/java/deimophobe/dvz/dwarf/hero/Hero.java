package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.Hat;
import deimophobe.dvz.Skin;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.KitElement;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class Hero extends Dwarf {
	private final Type type;
	
	protected Hero(Player player, Type type) {
		super(player, type.getData());
		
		this.type = type;
		
		Disguise disguise = type.getDisguise();
		if (disguise != null)
			DisguiseAPI.disguiseEntity(player, disguise);
		
		announceHero();
		makeBlindImmune();
		makePlagueImmune();
	}
	
	private void announceHero() {
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.LIGHT_PURPLE + " has become the dwarven hero " + player.getDisplayName());
	}
	
	public Disguise getDisguise() {
		return DisguiseAPI.getDisguise(player);
	}
	
	@Override
	public void showTrash() {}
	
	@Override
	protected int getNaturalRegenRate() {
		return 3;
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
	static {
		HERO_CONSUMABLES.put(ConsumableType.WIZARD_MORTAR, 32);
		HERO_CONSUMABLES.put(ConsumableType.MORTAR, 64);
		HERO_CONSUMABLES.put(ConsumableType.LAMP, 20);
		HERO_CONSUMABLES.put(ConsumableType.SOS, 3);
	}
	
	public enum Type {
		TUI("Tui the Lightbringer", Hat.TUI, "tui", "Tui", KitElementType.TUI_HAMMER, KitElementType.WILDFIRE) {
			@Override public Hero createHero(Player player) {return new Tui(player, this);}
		},
		NOSOVIN("Nosovin's Illustration", Hat.NOSOVIN, "tui", "Nosovin", KitElementType.TINDERFLAME, KitElementType.WAND, KitElementType.ROCKET_BOOTS){
			@Override public Hero createHero(Player player) {return new Nosovin(player, this);}
		},
		;
		
		private final DwarfData data;
		private final Skin skin;
		private final String nametag;
		
		Type(String title, Hat hat, String skin, String nametag, KitElementType... elements) {
			this(title, hat, Skin.getSkin(skin), nametag, elements);
		}
		
		Type(String title, Hat hat, Skin skin, String nametag, KitElementType... elements) {
			Set<KitElementType> allElements = new HashSet<>();
			allElements.add(KitElementType.HERO_ALE);
			allElements.add(KitElementType.HERO_SAFEFALL);
			
			allElements.addAll(Arrays.asList(elements));
			
			this.data = new DwarfData(title, true, hat, allElements,HERO_CONSUMABLES);
			this.skin = skin;
			this.nametag = nametag;
		}
		
		public DwarfData getData() {return data;}
		
		public Disguise getDisguise() {
			PlayerDisguise disguise = skin.getDisguise(ChatColor.GOLD + nametag);
			disguise.setKeepDisguiseOnPlayerDeath(false);
			disguise.setKeepDisguiseOnPlayerLogout(true);
			disguise.setViewSelfDisguise(false);
			disguise.setDisplayedInTab(true);
			return disguise;
		}
		
		public abstract Hero createHero(Player player);
		
		public static Type getHeroType(String arg) {
			for (Type type : values()) {
				if (type.toString().equalsIgnoreCase(arg))
					return type;
			}
			return null;
		}
	}
}
