package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.skin.PlayerSkin;
import deimophobe.nightfall.skin.Skin;
import deimophobe.nightfall.skin.SkinManager;
import deimophobe.nightfall.common.ConfigValidator;
import deimophobe.nightfall.common.MalformedConfigurationException;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfData;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.Kit;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
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
	
	public HeroData(ConfigurationSection config, HeroType type) throws MalformedConfigurationException {
		this.type = type;
		
		
		// Full name
		ConfigValidator.checkChildExists(config, "full-name");
		this.fullName = config.getString("full-name");
		
		// Descriptor
		ConfigValidator.checkChildExists(config, "descriptor");
		this.descriptor = config.getString("descriptor");
		
		
		
		// Hat
		ConfigValidator.checkChildExists(config, "hat");
		this.hat = CustomItem.getItem(config.getConfigurationSection("hat"), LoreTemplate.DWARF_HERO);
		
		// Skin
		ConfigValidator.checkChildExists(config, "skin");
		String skinName = config.getString("skin");
		if (!Skin.skinExists(skinName)) {
			throw new MalformedConfigurationException("No skin with name: " + skinName);
		}
		
		ConfigValidator.checkChildExists(config,"nametag");
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
				throw new MalformedConfigurationException("Unknown colour: " + colourName, e);
			}
			
			if (!glowColour.isColor()) {
				throw new MalformedConfigurationException("ChatColor '" + colourName + "' is not a colour");
			}
		}
		
		
		// Items
		ConfigValidator.checkChildExists(config, "items");
		for (String item : config.getStringList("items")) {
			try {
				addPiece(KitPieceType.fromString(item));
			} catch (UnknownEnumElementException e) {
				throw new MalformedConfigurationException("Unknown KitPiece item: " + item, e);
			}
		}
		addPiece(KitPieceType.HERO_BASE);
		
		
		// Consumables
		ConfigValidator.checkChildExists(config, "consumables");
		ConfigurationSection consumablesConfig = config.getConfigurationSection("consumables");
		for (String key : consumablesConfig.getKeys(false)) {
			String consumable = key.toLowerCase();
			int quantity = consumablesConfig.getInt(key);
			
			try {
				incrementConsumable(ConsumableType.fromString(consumable), quantity);
			} catch (UnknownEnumElementException e) {
				throw new MalformedConfigurationException("Unknown ConsumableType: " + consumable, e);
			}
		}
		
		
		String className = config.getString("class");
		if (className == null) {
			this.heroCreator = (p) -> new Hero(p, type);
		} else {
			try {
				Class<?> clazz = Class.forName(className);
				if (!Hero.class.isAssignableFrom(clazz)) {
					throw new MalformedConfigurationException("Class '" + className + "' does not inherit from the Hero class");
				}
				
				Constructor<?> constructor = clazz.getDeclaredConstructor(Player.class, HeroType.class);
				this.heroCreator = (p) -> {
					try {
						return (Hero) constructor.newInstance(p, type);
					} catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
						//NightfallPlugin.logger().severe("Failed to create hero '" + nametag + "' for player '" + p.getName() + "'");
						//e.printStackTrace();
						throw new RuntimeException("Failed to create hero '" + nametag + "' for player '" + p.getName() + "'", e);
					}
				};
			} catch (ClassNotFoundException e) {
				throw new MalformedConfigurationException("No class called: " + className, e);
			} catch (NoSuchMethodException e) {
				throw new MalformedConfigurationException("No valid constructor (Player, HeroType) for class: " + className, e);
			}
			
		}
		
		
	}
	
	Hero createHero(Player player) {
		return heroCreator.apply(player);
	}
	
	private String getTeamName() {
		return "hero" + type.ordinal();
	}
	
	public Team getTeam() {
		Scoreboard scoreboard = Game.getGame().getScoreboard();
		
		Team team = scoreboard.getTeam(getTeamName());
		if (team == null) {
			team = scoreboard.registerNewTeam(getTeamName());
			team.setColor(glowColour);
			team.setPrefix(glowColour.toString());
		}
		
		return team;
	}
	
	@Override
	public Kit createKitAndApplyToDwarf(Dwarf dwarf) {
		Kit kit = super.createKitAndApplyToDwarf(dwarf);
		kit.giveItems(PickupType.PICK);
		kit.giveItems(PickupType.SHOVEL);
		
		dwarf.setTitle(ChatColor.GOLD, fullName, true);
		getTeam().addEntry(skin.getNametag());
		
		SkinManager.getManager().addSkinChange(dwarf, skin);
		
		Bukkit.broadcastMessage(
				ChatColor.DARK_AQUA + dwarf.getName()
				+ ChatColor.LIGHT_PURPLE + " has become the " + descriptor + " "
				+ ChatColor.GOLD + fullName + ChatColor.LIGHT_PURPLE + "!"
		);
		
		return kit;
	}
}
