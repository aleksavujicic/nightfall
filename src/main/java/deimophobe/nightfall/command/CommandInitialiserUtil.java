package deimophobe.nightfall.command;

import co.aikar.commands.*;
import co.aikar.commands.contexts.ContextResolver;
import co.aikar.commands.contexts.IssuerAwareContextResolver;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfData;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.hero.HeroType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.map.MapManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.doom.DoomType;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.spawnmenu.SpawnEggMenuItem;
import deimophobe.nightfall.plague.PlagueType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

/**
 * Static initialiser class for ACF. (https://github.com/aikar/commands)
 *
 * Created by Deimophobe on 4/03/18.
 */
public class CommandInitialiserUtil {
	private CommandInitialiserUtil() {}
	
	public static void initialiseCommands(NightfallPlugin plugin) {
		BukkitCommandManager bcm = new BukkitCommandManager(plugin);
		bcm.enableUnstableAPI("help");
		
		
		registerCompletions(bcm);
		registerContexts(bcm);
		registerConditions(bcm);
		
		bcm.registerCommand(new AICommand());
		bcm.registerCommand(new DoomCommand());
		bcm.registerCommand(new DwarfCommand());
		bcm.registerCommand(new FixCommand());
		bcm.registerCommand(new GameCommand());
		bcm.registerCommand(new ItemCommand());
		bcm.registerCommand(new MapCommand());
		bcm.registerCommand(new MiscCommands());
		bcm.registerCommand(new MobCommand());
		bcm.registerCommand(new ShrineCommand());
	}
	
	private static void registerCompletions(BukkitCommandManager bcm) {
		final CommandCompletions<BukkitCommandCompletionContext> completions = bcm.getCommandCompletions();
		
		completions.registerCompletion("dwarves", c -> DwarfManager.getManager().getGamePlayerNames());
		completions.registerCompletion("monsters", c -> MonsterManager.getManager().getGamePlayerNames());
		completions.registerCompletion("gameplayers", c -> Game.getGame().getGamePlayerNames());
		
		completions.registerCompletion("heroes", getCompletionHandlerForEnum(HeroType.values()));
		completions.registerCompletion("procs", getCompletionHandlerForEnum(ProcType.values()));
		completions.registerCompletion("consumables", getCompletionHandlerForEnum(ConsumableType.values()));
		completions.registerCompletion("kitgives", getCompletionHandlerForEnum(KitGiveType.values()));
		completions.registerCompletion("mobtypes", getCompletionHandlerForEnum(MobType.getSpawnableMobs()));
		completions.registerCompletion("plagues", getCompletionHandlerForEnum(PlagueType.values()));
		completions.registerCompletion("plague-status", getCompletionHandlerForEnum(Dwarf.PlagueStatus.values()));
		completions.registerCompletion("dooms", getCompletionHandlerForEnum(DoomType.values()));
		
		completions.registerCompletion("spawneggs", c -> SpawnEggMenuItem.getEggNames());
		completions.registerCompletion("items", c -> ItemManager.getManager().getNames());
		completions.registerCompletion("maps", c -> MapManager.getManager().getMaps());
		
		completions.registerCompletion("kitpieces", c -> {
			Collection<String> pieces = KitPieceType.getPieceNames();
			String extras = c.getConfig("extra");
			switch (extras) {
				case "loadout":
					pieces.add("loadoutall");
				case "all":
					pieces.add("all");
					break;
				default: break;
			}
			return pieces;
		});
	}
	
	private static void registerContexts(BukkitCommandManager bcm) {
		final CommandContexts<BukkitCommandExecutionContext> contexts = bcm.getCommandContexts();
		// Note these are suppliers rather than constants otherwise they will break when new games are created
		contexts.registerIssuerAwareContext(Dwarf.class, getContextResolverOfGamePlayer(name -> DwarfManager.getManager().getGamePlayer(name), "dwarf"));
		contexts.registerIssuerAwareContext(MonsterPlayer.class, getContextResolverOfGamePlayer(name -> MonsterManager.getManager().getGamePlayer(name), "monster"));
		contexts.registerIssuerAwareContext(GamePlayer.class, getContextResolverOfGamePlayer(name -> Game.getGame().getGamePlayer(name), "game player"));
		
		// KitPieceType, KitPieceType[], and DwarfData context resolvers (depend on each other in reverse order).
		ContextResolver<KitPieceType, BukkitCommandExecutionContext> kitPieceTypeResolver = getContextResolverOfEnum(KitPieceType.values(), "kit piece", false);
		ContextResolver<KitPieceType[], BukkitCommandExecutionContext> arrayPieceResolver = context -> {
			// If first arg is all, then just add all pieces
			if (context.getFirstArg() != null && context.getFirstArg().equalsIgnoreCase("all")) {
				return KitPieceType.values();
			}
			
			// Otherwise process one by one
			List<KitPieceType> pieceTypes = new ArrayList<>();
			while (context.getFirstArg() != null) {
				pieceTypes.add(kitPieceTypeResolver.getContext(context));
			}
			return pieceTypes.toArray(new KitPieceType[0]);
		};
		contexts.registerContext(KitPieceType.class, kitPieceTypeResolver);
		contexts.registerContext(KitPieceType[].class, arrayPieceResolver);
		contexts.registerContext(DwarfData.class, context -> {
			String firstArg = context.getFirstArg();
			if (firstArg.equalsIgnoreCase("kit")) {
				Player player = (Player) context.getResolvedArg(Player.class);
				return DwarfData.getData(player);
			} else if (firstArg.equalsIgnoreCase("loadoutall")) {
				DwarfData data = new DwarfData();
				LoadoutManager.getManager().modifyAll(data);
				return data;
			} else {
				KitPieceType[] pieces = arrayPieceResolver.getContext(context);
				Set<KitPieceType> setPieces = new HashSet<>(Arrays.asList(pieces));
				return new DwarfData(setPieces, null);
			}
		});
		
		contexts.registerContext(HeroType.class, getContextResolverOfEnum(HeroType.values(), "hero", true));
		contexts.registerContext(ProcType.class, getContextResolverOfEnum(ProcType.values(), "proc", true));
		contexts.registerContext(ConsumableType.class, getContextResolverOfEnum(ConsumableType.values(), "consumable", true));
		contexts.registerContext(KitGiveType.class, getContextResolverOfEnum(KitGiveType.values(), "give type", true));
		contexts.registerContext(MobType.class, getContextResolverOfEnum(MobType.getSpawnableMobs(), "mob", true));
		contexts.registerContext(PlagueType.class, getContextResolverOfEnum(PlagueType.values(), "plague", true));
		contexts.registerContext(Dwarf.PlagueStatus.class, getContextResolverOfEnum(Dwarf.PlagueStatus.values(), "plague status", true));
		contexts.registerContext(DoomType.class, getContextResolverOfEnum(DoomType.values(), "doom", true));
		
		contexts.registerContext(CustomItem.class, context -> {
			String arg = context.popFirstArg();
			CustomItem item = ItemManager.getManager().getItem(arg);
			
			if (item == null) throw new InvalidCommandArgument(ChatColor.RED + "Unknown item '" + ChatColor.YELLOW + arg + ChatColor.RED + "'.");
			
			return item;
		});
		
		contexts.registerContext(SpawnEggMenuItem.class, context -> {
			String arg = context.popFirstArg();
			SpawnEggMenuItem spawnEgg = SpawnEggMenuItem.getEgg(arg);
			
			if (spawnEgg == null) throw new InvalidCommandArgument(ChatColor.RED + "Unknown spawn egg '" + ChatColor.YELLOW + arg + ChatColor.RED + "'.");
			
			return spawnEgg;
		});
	}
	
	private static void registerConditions(BukkitCommandManager bcm) {
		final CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> conditions = bcm.getCommandConditions();
		conditions.addCondition(Dwarf.class, "reg-armour", (context, execContext, dwarf) -> {
			if (dwarf == null) throw new ConditionFailedException("Dwarf must not be null");
			if (!(dwarf.getArmour() instanceof DwarvenArmour)) throw new ConditionFailedException("Dwarf must have regular dwarven armour.");
		});
		conditions.addCondition(String.class, "map", (context, execContext, map) -> {
			if (!MapManager.getManager().getMaps().contains(map)) throw new ConditionFailedException("String must be a valid map.");
		});
		conditions.addCondition(Player.class, "lobby", (context, execContext, player) -> {
			if (!Game.getGame().isLobbyPlayer(player)) throw new ConditionFailedException("Player must be a lobby player (set gamemode to adventure).");
		});
		
		conditions.addCondition("pre-build", context -> {
			if (!Game.getGame().getPhase().isBefore(Phase.BUILD)) throw new ConditionFailedException("The game has already started.");
		});
		conditions.addCondition("pre-plague", context -> {
			if (!Game.getGame().getPhase().isBefore(Phase.PLAGUE)) throw new ConditionFailedException("The plague has already occured.");
		});
		conditions.addCondition("build-phase", context -> {
			if (Game.getGame().getPhase() != Phase.BUILD) throw new ConditionFailedException("Must be in build phase.");
		});
		conditions.addCondition("monster-release", context -> {
			if (!Game.getGame().getPhase().isAfter(Phase.PLAGUE)) throw new ConditionFailedException("The monsters have not be released.");
		});
		conditions.addCondition("main-game-phase", context -> {
			if (Game.getGame().getPhase().isBefore(Phase.GAME)) throw new ConditionFailedException("The monsters have not be released.");
			if (Game.getGame().getPhase().isAfter(Phase.GAME)) throw new ConditionFailedException("The game is over.");
		});
		
		conditions.addCondition("map-enabled", context -> {
			if (!MapManager.getManager().isEnabled()) throw new ConditionFailedException("Map loading must be enabled.");
		});
	}
	
	
	// ----- HELPER METHODS ------
	
	private static <T extends GamePlayer> IssuerAwareContextResolver<T, BukkitCommandExecutionContext> getContextResolverOfGamePlayer(Function<String, T> resolver, String playerTypeName) {
		return c -> {
			String name = c.popFirstArg();
			T gamePlayer;
			
			if (name == null && !c.isOptional()) throw new InvalidCommandArgument(ChatColor.RED + "Please provide a " + playerTypeName);
			
			boolean setSelfAsGamePlayer = name == null || name.equals(".") || name.equals("~");
			if (setSelfAsGamePlayer) {
				gamePlayer = resolver.apply(c.getPlayer().getName());
				if (gamePlayer == null) throw new InvalidCommandArgument(ChatColor.RED + "You are not a " + playerTypeName);
			} else {
				gamePlayer = resolver.apply(name);
				if (gamePlayer == null) throw new InvalidCommandArgument(ChatColor.RED + "Player '" + ChatColor.YELLOW + name + ChatColor.RED + "' is not a " + playerTypeName);
			}
			return gamePlayer;
		};
	}
	
	private static <T extends Enum<T>> ContextResolver<T, BukkitCommandExecutionContext> getContextResolverOfEnum(T[] values, String simpleName, boolean displayAll) {
		// Build mapper from string to enum values
		Map<String, T> names = new HashMap<>();
		List<String> nameList = new ArrayList<>();
		for (T value : values) {
			String name = value.name().toLowerCase().replace('_', '-');
			names.put(name, value);
			nameList.add(name);
			
		}
		
		// Get string of all values - used for displaying error message
		Collections.sort(nameList);
		StringBuilder valuesBuilder = new StringBuilder();
		for (String name : nameList) {
			valuesBuilder.append(ChatColor.GREEN + name + ChatColor.WHITE + ", ");
		}
		if (valuesBuilder.length() >= 2) valuesBuilder.setLength(valuesBuilder.length() - 2);
		String allValues = valuesBuilder.toString();
		
		// Create error message
		String preErrorMsg = ChatColor.RED + "Unknown " + simpleName + " '" + ChatColor.YELLOW;
		String postErrorMsg;
		if (displayAll) {
			postErrorMsg = ChatColor.RED + "' - Must be one of: " + allValues;
		} else {
			postErrorMsg = ChatColor.RED + "'.";
		}
		
		// Create resolver
		return context -> {
			String arg = context.popFirstArg().toLowerCase().replace('_','-');
			T value = names.get(arg);
			if (value == null) {
				throw new InvalidCommandArgument(preErrorMsg + arg + postErrorMsg, false);
			} else {
				return value;
			}
		};
	}
	
	private static <T extends Enum<T>> CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> getCompletionHandlerForEnum(T[] values) {
		Set<String> names = new HashSet<>();
		for (T type : values) {
			names.add(type.toString().toLowerCase().replace('_', '-'));
		}
		return context -> names;
	}
}
