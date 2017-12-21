package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Skin;
import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.common.loadout.DwarfData;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.HeroArmour;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class Hero extends Dwarf {
	protected final Type type;
	
	protected Hero(Player player, Type type) {
		super(player, type.getData());
		
		this.type = type;
		
		setArmour(new HeroArmour(this, type.hat));
		
		Disguise disguise = type.getDisguise();
		if (disguise != null)
			DisguiseAPI.disguiseEntity(player, disguise);
		
		announceHero();
		makeBlindImmune();
		makePlagueImmune();
		
		giveKitItems(KitGiveType.PICK);
		giveKitItems(KitGiveType.SHOVEL);
		
		if (type.skin != null) {
			String name = type.skin.getName();
			Team team = Game.getGame().getNewTeam("hero" + name);
			team.setColor(type.glowColour);
			team.setPrefix(type.glowColour.toString());
			team.addEntry(name);
		}
		
		SkinManager.getManager().addSkinChange(this, type.skin);
	}
	
	@Override
	public void onRemove() {
		super.onRemove();
		SkinManager.getManager().silentlyRemoveSkinChange(this);
		if (!player.isDead())
			SkinManager.getManager().updateSkin(this);
	}
	
	@Override
	public void notifyDeath(Dwarf dwarf) {
		super.notifyDeath(dwarf);
		if (dwarf == this) {
			// Bit of a hack but should rarely be a problem
			new BukkitRunnable() {
				@Override
				public void run() {
					SkinManager.getManager().updateSkin(Hero.this);
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), 20);
		}
	}
	
	private void announceHero() {
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.LIGHT_PURPLE + " has become the " + type.getDescriptor() + " " + player.getDisplayName() + ChatColor.LIGHT_PURPLE + "!");
	}
	
	public Disguise getDisguise() {
		return DisguiseAPI.getDisguise(player);
	}
	
	@Override
	public void goOnline(Player newPlayer) {
		super.goOnline(newPlayer);
		
		Disguise disguise = type.getDisguise();
		if (disguise != null)
			DisguiseAPI.disguiseEntity(newPlayer, disguise);
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
	
	
	private static final Map<ConsumableType, Integer> HERO_CONSUMABLES = new HashMap<>();
	private static final Map<ConsumableType, Integer> EXTRA_ARTHEA_CONSUMABLES = new HashMap<>();
	static {
		HERO_CONSUMABLES.put(ConsumableType.COBBLESTONE, 256);
		HERO_CONSUMABLES.put(ConsumableType.WIZARD_MORTAR, 16);
		HERO_CONSUMABLES.put(ConsumableType.MORTAR, 64);
		HERO_CONSUMABLES.put(ConsumableType.LAMP, 24);
		HERO_CONSUMABLES.put(ConsumableType.SLAB, 3);
		HERO_CONSUMABLES.put(ConsumableType.SOS, 1);
		
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.SOS, 1);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.LAMP, 40);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.SLAB, 2);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.WIZARD_MORTAR, 16);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.COBBLESTONE, 128);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.TORCH, 64);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.MORTAR, 32);
		EXTRA_ARTHEA_CONSUMABLES.put(ConsumableType.HEAL_STATION, 12);
	}
	
	public enum Type {
		TUI("Tui the Lightbringer", "tui", "Dwarven Hero", ChatColor.GOLD,
				KitElementType.TUI_HAMMER,
				KitElementType.WILDFIRE),
		
		NOSOVIN("Nosovin's Illustration", "nosovin", "Dwarven Hero",
				KitElementType.TINDERFLAME,
				KitElementType.WAND,
				KitElementType.ROCKET_BOOTS),
		
		ARTHEA("Arthea", "arthea", "Dwarven Hero", ChatColor.DARK_RED,
				EXTRA_ARTHEA_CONSUMABLES,
				KitElementType.HEALER_TOTEM,
				KitElementType.CADUCEUS,
				KitElementType.ELYSTRIA,
		        KitElementType.LUMINOUS),
		
		VELVETINE("Velvetine", "velvetine", "Dwarven Hero", ChatColor.DARK_PURPLE,
				KitElementType.VELSWORD,
				KitElementType.VELBOW,
				KitElementType.HORN
				),
		
		HERANA("Herana", "herana", "Mermaid Queen", ChatColor.AQUA,
				KitElementType.GRB,
				KitElementType.DRAGONSKIN,
				KitElementType.HORN
				),
		
		OXYSIS("Oxysis", "oxysis", "Pixie Hero",
				KitElementType.DRUCRIST,
				KitElementType.WILDE_STAFF
				)
		{
			@Override
			public Disguise getDisguise() {
				Disguise disguise = new MobDisguise(DisguiseType.VEX);
				disguise.setKeepDisguiseOnPlayerDeath(false);
				disguise.setViewSelfDisguise(false);
				return disguise;
			}
		},
		
		
		;
		
		private final DwarfData data;
		protected final String hat;
		private final Skin skin;
		private final ChatColor glowColour;
		private final String descriptor;
		
		Type(String title, String skin, String descriptor, KitElementType... elements) {
			this(title, skin, descriptor, ChatColor.DARK_AQUA, Collections.emptyMap(), elements);
		}
		
		Type(String title, String skin, String descriptor, ChatColor glowColour, KitElementType... elements) {
			this(title, skin, descriptor, glowColour, Collections.emptyMap(), elements);
		}
		
		Type(String title, String skin, String descriptor, Map<ConsumableType, Integer> extraConsumables, KitElementType... elements) {
			this(title, skin, descriptor, ChatColor.DARK_AQUA, extraConsumables, elements);
		}
		
		Type(String title, String skin, String descriptor, ChatColor glowColour, Map<ConsumableType, Integer> extraConsumables, KitElementType... elements) {
			this.descriptor = descriptor;
			Set<KitElementType> allElements = new HashSet<>();
			allElements.add(KitElementType.HERO_ALE);
			allElements.add(KitElementType.HERO_SLOWFALL);
			
			allElements.addAll(Arrays.asList(elements));
			
			this.data = new DwarfData(title, true, null, allElements, HERO_CONSUMABLES);
			data.addConsumables(extraConsumables);
			
			if (skin == null) {
				this.skin = null;
			} else {
				this.skin = Skin.getSkin(skin);
			}
			this.hat = skin;
			this.glowColour = glowColour;
		}
		
		public DwarfData getData() {return data;}
		
		public Disguise getDisguise() { return null; }
		/*
		public Disguise getDisguise() {
			PlayerDisguise disguise = skin.getDisguise(ChatColor.GOLD + nametag);
			disguise.setKeepDisguiseOnPlayerDeath(false);
			disguise.setViewSelfDisguise(false);
			disguise.setDisplayedInTab(true);
			return disguise;
		}
		*/
		
		public String getDescriptor() {
			return descriptor;
		}
		
		public Hero createHero(Player player) {
			switch (this) {
				case TUI: return new Tui(player, this);
				case NOSOVIN: return new Nosovin(player, this);
				case ARTHEA: return new Arthea(player, this);
				case VELVETINE: return new Hero(player, this);
				case HERANA: return new Hero(player, this);
				case OXYSIS: return new Hero(player, this);
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
