package deimophobe.nightfall.command;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.entity.GamePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
		addResolver(Enum.class, arg -> {
			TextComponent text = new TextComponent(arg.name().toLowerCase().replace('_', '-'));
			text.setColor(ChatColor.GREEN);
			return text;
		});
		addResolver(GamePlayer.class, arg -> {
			BaseComponent text = Misc.textComponentFromString(arg.getDisplayName());
			text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + arg.getName()));
			return text;
		});
		addResolver(Player.class, arg -> {
			TextComponent text = new TextComponent(arg.getName());
			text.setColor(ChatColor.DARK_GRAY);
			text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + arg.getName()));
			return text;
		});
		final NumberFormat formatter = new DecimalFormat("#.##");
		addResolver(Location.class, arg -> {
			TextComponent text = new TextComponent();
			text.setColor(ChatColor.GOLD);
			
			TextComponent xText = new TextComponent(formatter.format(arg.getX()));
			xText.setColor(ChatColor.AQUA);
			TextComponent yText = new TextComponent(formatter.format(arg.getY()));
			yText.setColor(ChatColor.AQUA);
			TextComponent zText = new TextComponent(formatter.format(arg.getZ()));
			zText.setColor(ChatColor.AQUA);
			
			text.addExtra("(");
			text.addExtra(xText);
			text.addExtra(", ");
			text.addExtra(yText);
			text.addExtra(", ");
			text.addExtra(zText);
			text.addExtra(")");
			
			return text;
		});
	}
	
	// Guarantees same value and key have same type parameter
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
