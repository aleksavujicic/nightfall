package deimophobe.nightfall.bungee.command;

import co.aikar.commands.*;
import co.aikar.commands.contexts.ContextResolver;
import com.google.common.collect.ImmutableSet;
import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import deimophobe.nightfall.bungee.map.GameMap;
import deimophobe.nightfall.bungee.map.MapManager;
import deimophobe.nightfall.bungee.util.Util;
import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.function.Function;

/**
 * Static initialiser class for ACF. (https://github.com/aikar/commands)
 *
 * Created by Deimophobe on 4/03/18.
 */
public class CommandInitialiserUtil {
	private CommandInitialiserUtil() {}
	
	private static final String RANDOM_ENUM = "$r";
	private static final String RANDOM_PLAYER = "@r";
	private static final String ALL_PLAYER = "@a";
	
	
	public static void initialiseCommands(NightfallBungeePlugin plugin) {
		MessageUtil.initialise();
		
		BungeeCommandManager bcm = new BungeeCommandManager(plugin);
		bcm.enableUnstableAPI("help");
		
		
		registerCompletions(bcm);
		registerContexts(bcm);
		registerConditions(bcm);
		
		bcm.registerCommand(new GamesCommand());
	}
	
	private static void registerCompletions(BungeeCommandManager bcm) {
		final CommandCompletions<BungeeCommandCompletionContext> completions = bcm.getCommandCompletions();
		
		ImmutableSet<String> booleans = ImmutableSet.of("true", "false");
		completions.registerCompletion("boolean", c -> booleans);
	}
	
	private static void registerContexts(BungeeCommandManager bcm) {
		final CommandContexts<BungeeCommandExecutionContext> contexts = bcm.getCommandContexts();
		
		contexts.registerContext(GameMap.class, getSimpleContextResolver(name -> MapManager.getManager().getMap(name), "map"));
	}
	
	private static void registerConditions(BungeeCommandManager bcm) {
		final CommandConditions<BungeeCommandIssuer, BungeeCommandExecutionContext, BungeeConditionContext> conditions = bcm.getCommandConditions();
	}
	
	
	// ----- HELPER METHODS ------
	
	private static <T> ContextResolver<T, BungeeCommandExecutionContext> getSimpleContextResolver(Function<String,T> resolver, String simpleName) {
		return context -> {
			String arg = context.popFirstArg();
			T t = resolver.apply(arg);
			
			if (t == null) throw new InvalidCommandArgument(ChatColor.RED + "Unknown " + simpleName + " '" + ChatColor.YELLOW + arg + ChatColor.RED + "'.");
			
			return t;
		};
	}
	
	private static <T extends Enum<T>> ContextResolver<T, BungeeCommandExecutionContext> getContextResolverOfEnum(T[] values, String simpleName, boolean displayAll) {
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
			
			if (arg.equals(RANDOM_ENUM)) {
				return Util.getRandom(names.values());
			}
			
			T value = names.get(arg);
			if (value == null) {
				throw new InvalidCommandArgument(preErrorMsg + arg + postErrorMsg, false);
			} else {
				return value;
			}
		};
	}
	
	private static <T extends Enum<T>> CommandCompletions.CommandCompletionHandler<BungeeCommandCompletionContext> getCompletionHandlerForEnum(T[] values) {
		Set<String> names = new HashSet<>();
		for (T type : values) {
			names.add(type.toString().toLowerCase().replace('_', '-'));
		}
		return context -> names;
	}
	
	
	
	
	static List<String> finalArgCompletion(String[] args, Collection<String> strings) {
		String finalArg = args[args.length - 1].toLowerCase();
		List<String> matchStrings = new ArrayList<>();
		for (String string : strings) {
			if (string.toLowerCase().startsWith(finalArg))
				matchStrings.add(string);
		}
		return matchStrings;
	}
}
