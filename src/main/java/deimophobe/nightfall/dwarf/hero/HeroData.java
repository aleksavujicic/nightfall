package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.PlayerSkin;
import deimophobe.nightfall.Skin;
import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfData;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.Kit;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class HeroData extends DwarfData {
	
	private final HeroType type;
	
	private final String fullName;
	private final String descriptor;
	
	private final CustomItem hat;
	private final PlayerSkin skin;
	private final ChatColor glowColour;
	
	private final Function<Player, Hero> heroCreator;
	
	public CustomItem getHat() { return hat; }
	
	public HeroData(ConfigurationSection config, HeroType type) throws InvalidConfigurationException {
		this.type = type;
		
		
		// Full name
		Misc.checkConfigStringExists(config, "full-name");
		this.fullName = config.getString("full-name");
		
		// Descriptor
		Misc.checkConfigStringExists(config, "descriptor");
		this.descriptor = config.getString("descriptor");
		
		
		
		// Hat
		Misc.checkConfigStringExists(config, "hat");
		this.hat = CustomItem.getItem(config.getConfigurationSection("hat"), LoreTemplate.DWARF_HERO, Slot.HEAD);
		
		// Skin
		Misc.checkConfigStringExists(config, "skin");
		String skinName = config.getString("skin");
		if (!Skin.skinExists(skinName)) {
			throw new InvalidConfigurationException("No skin with name: " + skinName);
		}
		
		Misc.checkConfigStringExists(config,"nametag");
		String nametag = config.getString("nametag");
		
		this.skin = new PlayerSkin(ChatColor.GOLD + nametag, skinName);
		
		
		// Glow Colour
		String colourName = config.getString("colour");
		if (colourName == null) {
			this.glowColour = ChatColor.DARK_AQUA;
		} else {
			try {
				this.glowColour = Misc.getEnumMemberFromString(colourName, ChatColor.values(), "ChatColor");
			} catch (UnknownEnumElementException e) {
				throw new InvalidConfigurationException("Unknown colour: " + colourName, e);
			}
			
			if (!glowColour.isColor()) {
				throw new InvalidConfigurationException("ChatColor '" + colourName + "' is not a colour");
			}
		}
		
		
		// Items
		Misc.checkConfigStringExists(config, "items");
		for (String item : config.getStringList("items")) {
			try {
				addElement(KitElementType.fromString(item));
			} catch (UnknownEnumElementException e) {
				throw new InvalidConfigurationException("Unknown KitPiece item: " + item, e);
			}
		}
		addElement(KitElementType.HERO_ALE);
		
		
		// Consumables
		Misc.checkConfigStringExists(config, "consumables");
		ConfigurationSection consumablesConfig = config.getConfigurationSection("consumables");
		for (String key : consumablesConfig.getKeys(false)) {
			String consumable = key.toLowerCase();
			int quantity = consumablesConfig.getInt(key);
			
			try {
				incrementConsumable(ConsumableType.fromString(consumable), quantity);
			} catch (UnknownEnumElementException e) {
				throw new InvalidConfigurationException("Unknown ConsumableType: " + consumable, e);
			}
		}
		
		
		String className = config.getString("class");
		if (className == null) {
			this.heroCreator = (p) -> new Hero(p, type);
		} else {
			try {
				Class<?> clazz = Class.forName(className);
				if (!Hero.class.isAssignableFrom(clazz)) {
					throw new InvalidConfigurationException("Class '" + className + "' does not inherit from the Hero class");
				}
				Class<Hero> heroClass = (Class<Hero>) clazz;
				
				Constructor<Hero> constructor = heroClass.getDeclaredConstructor(Player.class, HeroType.class);
				this.heroCreator = (p) -> {
					try {
						return constructor.newInstance(p, type);
					} catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
						//Bukkit.getLogger().severe("Failed to create hero '" + nametag + "' for player '" + p.getName() + "'");
						//e.printStackTrace();
						throw new RuntimeException("Failed to create hero '" + nametag + "' for player '" + p.getName() + "'", e);
					}
				};
			} catch (ClassNotFoundException e) {
				throw new InvalidConfigurationException("No class called: " + className, e);
			} catch (NoSuchMethodException e) {
				throw new InvalidConfigurationException("No valid constructor (Player, HeroType) for class: " + className, e);
			}
			
		}
		
		
	}
	
	Hero createHero(Player player) {
		return heroCreator.apply(player);
	}
	
	Team createTeam() {
		String name = type.name().toLowerCase();
		Team team = Game.getGame().getNewTeam("hero" + name);
		team.setColor(glowColour);
		team.setPrefix(glowColour.toString());
		team.addEntry(name);
		
		return team;
	}
	
	@Override
	public Kit createKitAndApplyToDwarf(Dwarf dwarf) {
		Kit kit = super.createKitAndApplyToDwarf(dwarf);
		kit.giveItems(KitGiveType.PICK);
		kit.giveItems(KitGiveType.SHOVEL);
		
		dwarf.setTitle(ChatColor.GOLD, fullName, true);
		
		SkinManager.getManager().addSkinChange(dwarf, skin);
		
		Bukkit.broadcastMessage(
				ChatColor.DARK_AQUA + dwarf.getName()
				+ ChatColor.LIGHT_PURPLE + " has become the " + descriptor + " "
				+ ChatColor.GOLD + fullName + ChatColor.LIGHT_PURPLE + "!"
		);
		
		return kit;
	}
}
