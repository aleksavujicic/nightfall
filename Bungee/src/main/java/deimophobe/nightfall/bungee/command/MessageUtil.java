package deimophobe.nightfall.bungee.command;

import deimophobe.nightfall.bungee.map.GameMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Deimophobe on 9/03/18.
 */
class MessageUtil {
	
	private static final Map<Class<?>, MessageResolver<?>> resolvers = new HashMap<>();
	
	static void initialise() {
		addResolver(String.class, TextComponent::new);
		addPrimitiveResolver(int.class, arg -> arg >= 0);
		addPrimitiveResolver(long.class, arg -> arg >= 0);
		addPrimitiveResolver(double.class, arg -> arg >= 0);
		addPrimitiveResolver(float.class, arg -> arg >= 0);
		addPrimitiveResolver(Integer.class, arg -> arg >= 0);
		addPrimitiveResolver(Long.class, arg -> arg >= 0);
		addPrimitiveResolver(Double.class, arg -> arg >= 0);
		addPrimitiveResolver(Float.class, arg -> arg >= 0);
		addResolver(boolean.class, arg -> {
			TextComponent text;
			if (arg) {
				text = new TextComponent("enabled");
				text.setColor(ChatColor.GREEN);
			} else {
				text = new TextComponent("disabled");
				text.setColor(ChatColor.RED);
			}
			return text;
		});
		addResolver(Boolean.class, arg -> {
			TextComponent text;
			if (arg) {
				text = new TextComponent("enabled");
				text.setColor(ChatColor.GREEN);
			} else {
				text = new TextComponent("disabled");
				text.setColor(ChatColor.RED);
			}
			return text;
		});
		addResolver(Enum.class, arg -> {
			TextComponent text = new TextComponent(arg.name().toLowerCase().replace('_', '-'));
			text.setColor(ChatColor.GREEN);
			return text;
		});
		addResolver(Enum[].class, arg -> {
			TextComponent text = new TextComponent();
			int i = 0;
			for (Enum type : arg) {
				i++;
				TextComponent name = new TextComponent(type.name().toLowerCase().replace('_', '-'));
				name.setColor(ChatColor.GREEN);
				text.addExtra(name);
				
				if (i < arg.length)
					text.addExtra(", ");
			}
			return text;
		});
		addResolver(ProxiedPlayer.class, arg -> {
			TextComponent text = new TextComponent(arg.getName());
			text.setColor(ChatColor.DARK_GRAY);
			text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + arg.getName() + " "));
			return text;
		});
		addResolver(GameMap.class, arg -> {
			TextComponent text = new TextComponent(arg.getDisplayName());
			text.setColor(ChatColor.GREEN);
			return text;
		});
	}
	
	private static <T> void addPrimitiveResolver(Class<T> clazz, Function<T, Boolean> isPositive) {
		addResolver(clazz, arg -> {
			TextComponent text = new TextComponent("" + arg);
			if (isPositive.apply(arg)) {
				text.setColor(ChatColor.AQUA);
			} else {
				text.setColor(ChatColor.RED);
			}
			return text;
		});
	}
	
	// Guarantees same value and key have same type parameter
	private static <T> void addResolver(Class<T> clazz, MessageResolver<T> resolver) {
		resolvers.put(clazz, resolver);
	}
	
	private static <T> MessageResolver<T> getResolver(Class<T> clazz) {
		return (MessageResolver<T>) resolvers.get(clazz);
	}
	
	static void sendErrorMessage(CommandSender sender, Object... objects) {
		sendMessage(sender, ChatColor.RED, objects);
	}
	
	static void sendMessage(CommandSender sender, Object... objects) {
		sendMessage(sender, ChatColor.YELLOW, objects);
	}
	
	static void sendMessage(CommandSender sender, ChatColor colour, Object... objects) {
		BaseComponent message = new TextComponent();
		message.setColor(colour);
		
		for (Object object : objects) {
			BaseComponent nextComponent = null;
			MessageResolver<?> resolver = resolvers.get(object.getClass());
			if (resolver != null) {
				nextComponent = resolver.getUncheckedMessage(object);
			} else {
				boolean created = false;
				for (Map.Entry<Class<?>, MessageResolver<?>> entry : resolvers.entrySet()) {
					Class<?> clazz = entry.getKey();
					if (clazz.isInstance(object)) {
						nextComponent = entry.getValue().getUncheckedMessage(object);
						created = true;
						break;
					}
				}
				
				if (!created) {
					throw new IllegalArgumentException("Do not know how to process object " + object + " of class " + object.getClass().getName());
				}
			}
			message.addExtra(nextComponent);
		}
		
		sender.sendMessage(message);
	}
}
