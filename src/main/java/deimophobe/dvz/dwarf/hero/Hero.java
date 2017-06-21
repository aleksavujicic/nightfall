package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.Game;
import deimophobe.dvz.Hat;
import deimophobe.dvz.Skin;
import deimophobe.dvz.dwarf.armour.Armour;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.dwarf.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.glow.GlowAPI;

import java.util.*;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class Hero extends Dwarf {
	private final Type type;
	
	protected Hero(Player player, Type type) {
		super(player, type.getData(), Armour.Type.HERO);
		
		this.type = type;
		
		Disguise disguise = type.getDisguise();
		if (disguise != null)
			DisguiseAPI.disguiseEntity(player, disguise);
		
		announceHero();
		makeBlindImmune();
		makePlagueImmune();
		
		giveKitItems(KitGiveType.PICK);
		giveKitItems(KitGiveType.SHOVEL);
	}
	
	private void announceHero() {
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + entity.getName() + ChatColor.LIGHT_PURPLE + " has become the dwarven hero " + entity.getDisplayName() + ChatColor.LIGHT_PURPLE + "!");
	}
	
	public Disguise getDisguise() {
		return DisguiseAPI.getDisguise(entity);
	}
	
	@Override
	public void showTrash() {}
	
	@Override
	public void updateVisibility() {}
	
	@Override
	public void regenMana(int amt) {
		super.regenMana(amt/3);
	}
	
	
	@Override
	public void setGlowing(int duration, GlowAPI.Color color) {
		GlowAPI.setGlowing(entity, color, Bukkit.getOnlinePlayers());
		GlowAPI.setGlowing(getDisguise().getEntity(), color, Bukkit.getOnlinePlayers());
		new BukkitRunnable() {
			@Override
			public void run() {
				GlowAPI.setGlowing(entity, false, Bukkit.getOnlinePlayers());
				GlowAPI.setGlowing(getDisguise().getEntity(), false, Bukkit.getOnlinePlayers());
			}
		}.runTaskLater(Game.getGame().getPlugin(), duration);
	}
	
	
	private static final Map<ConsumableType, Integer> HERO_CONSUMABLES = new HashMap<>();
	private static final Map<ConsumableType, Integer> EXTRA_ARTHEA_CONSUMABLES = new HashMap<>();
	static {
		HERO_CONSUMABLES.put(ConsumableType.COBBLESTONE, 256);
		HERO_CONSUMABLES.put(ConsumableType.TORCH, 128);
		HERO_CONSUMABLES.put(ConsumableType.WIZARD_MORTAR, 32);
		HERO_CONSUMABLES.put(ConsumableType.MORTAR, 64);
		HERO_CONSUMABLES.put(ConsumableType.LAMP, 20);
		HERO_CONSUMABLES.put(ConsumableType.SOS, 1);
		
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.SOS, 1);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.LAMP, 44);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.WIZARD_MORTAR, 32);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.COBBLESTONE, 128);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.TORCH, 64);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.MORTAR, 32);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.HEAL_STATION, 12);
	}
	
	public enum Type {
		TUI("Tui the Lightbringer", Hat.TUI, "tui", "Tui",
				KitElementType.TUI_HAMMER,
				KitElementType.WILDFIRE),
		
		NOSOVIN("Nosovin's Illustration", Hat.NOSOVIN, "tui", "Nosovin",
				KitElementType.TINDERFLAME,
				KitElementType.WAND,
				KitElementType.ROCKET_BOOTS),
		
		ARTHEA("Arthea", Hat.ARTHEA, "arthea", "Arthea", EXTRA_ARTHEA_CONSUMABLES,
				KitElementType.HEALER_TOTEM,
				KitElementType.CADUCEUS,
				KitElementType.ELYSTRIA),
		
		VELVETINE("Velvetine", Hat.VELVETINE, "arthea", "Velvetine",
				KitElementType.GRB,
				KitElementType.DRAGONSKIN,
				KitElementType.HORN
				),
		
		HERANA("Herana", Hat.HERANA, "herana", "Herana",
				KitElementType.GRB,
				KitElementType.DRAGONSKIN,
				KitElementType.HORN
		),
		;
		
		private final DwarfData data;
		private final Skin skin;
		private final String nametag;
		
		Type(String title, Hat hat, String skin, String nametag, KitElementType... elements) {
			this(title, hat, skin, nametag, Collections.emptyMap(), elements);
		}
		
		Type(String title, Hat hat, String skin, String nametag, Map<ConsumableType, Integer> extraConsumables, KitElementType... elements) {
			Set<KitElementType> allElements = new HashSet<>();
			allElements.add(KitElementType.HERO_ALE);
			allElements.add(KitElementType.HERO_SAFEFALL);
			
			allElements.addAll(Arrays.asList(elements));
			
			this.data = new DwarfData(title, true, hat, allElements, HERO_CONSUMABLES);
			data.addConsumables(extraConsumables);
			
			this.skin = Skin.getSkin(skin);
			this.nametag = nametag;
		}
		
		public DwarfData getData() {return data;}
		
		public Disguise getDisguise() {
			PlayerDisguise disguise = skin.getDisguise(ChatColor.GOLD + nametag);
			disguise.setKeepDisguiseOnPlayerDeath(false);
			disguise.setViewSelfDisguise(false);
			disguise.setDisplayedInTab(true);
			return disguise;
		}
		
		public Hero createHero(Player player) {
			switch (this) {
				case TUI: return new Tui(player, this);
				case NOSOVIN: return new Nosovin(player, this);
				case ARTHEA: return new Arthea(player, this);
				case VELVETINE: return new Hero(player, this);
				case HERANA: return new Hero(player, this);
			}
			throw new IllegalArgumentException("Unknown hero: " + this);
		}
		
		public static Type getHeroType(String arg) {
			for (Type type : values()) {
				if (type.toString().equalsIgnoreCase(arg))
					return type;
			}
			return null;
		}
		
		public static Collection<String> getHeroList() {
			Set<String> heroes = new HashSet<>();
			for (Type type : values())
				heroes.add(type.toString().toLowerCase());
			return heroes;
		}
	}
}
