package deimophobe.nightfall.command;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.entity.GamePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 9/03/18.
 */
class MessageUtil {
	
	private static final Map<Class<?>, MessageResolver<?>> resolvers = new HashMap<>();
	
	static void initialise() {
		addResolver(String.class, TextComponent::new);
		addPrimitiveResolver(int.class);
		addPrimitiveResolver(long.class);
		addPrimitiveResolver(double.class);
		addPrimitiveResolver(float.class);
		addPrimitiveResolver(Integer.class);
		addPrimitiveResolver(Long.class);
		addPrimitiveResolver(Double.class);
		addPrimitiveResolver(Float.class);
		addResolver(Enum.class, arg -> {
			TextComponent text = new TextComponent(arg.name().toLowerCase().replace('_', '-'));
			text.setColor(ChatColor.GREEN);
			return text;
		});
		addResolver(GamePlayer.class, arg -> {
			String name = arg.getDisplayName();
			return Misc.textComponentFromString(name);
		});
		addResolver(Player.class, arg -> {
			TextComponent text = new TextComponent(arg.getName());
			text.setColor(ChatColor.LIGHT_PURPLE);
			return text;
		});
	}
	
	// Guarantees same value and key have same type parameter
	private static <T> void addPrimitiveResolver(Class<T> clazz) {
		addResolver(clazz, arg -> {
			TextComponent text = new TextComponent("" + arg);
			text.setColor(ChatColor.AQUA);
			return text;
		});
	}
	
	private static <T> void addResolver(Class<T> clazz, MessageResolver<T> resolver) {
		resolvers.put(clazz, resolver);
	}
	
	private static <T> MessageResolver<T> getResolver(Class<T> clazz) {
		return (MessageResolver<T>) resolvers.get(clazz);
	}
	
	
	static void sendMessage(CommandSender sender, Object... objects) {
		BaseComponent message = new TextComponent();
		message.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
		
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
		
		sender.spigot().sendMessage(message);
	}
}
