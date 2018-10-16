package deimophobe.nightfall.common.command;

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
public class MessageUtil {
	
	private static final Map<Class<?>, MessageResolver<?>> resolvers = new HashMap<>();
	private static final BaseComponent NULL_COMPONENT = getNullComponent();
	
	private static BaseComponent getNullComponent() {
		BaseComponent nullComponent = new TextComponent("null");
		nullComponent.setColor(ChatColor.RED);
		nullComponent.setItalic(true);
		return nullComponent;
	}
	
	static void initialise() {
		addResolver(String.class, TextComponent::new);
		addResolver(BaseComponent.class, arg -> arg);
		
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
		addResolver(Player.class, arg -> {
			TextComponent text = new TextComponent(arg.getName());
			text.setColor(ChatColor.DARK_GRAY);
			text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + arg.getName() + " "));
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
	public static <T> void addResolver(Class<T> clazz, MessageResolver<T> resolver) {
		resolvers.put(clazz, resolver);
	}
	
	public static <T> MessageResolver<T> getResolver(Class<T> clazz) {
		return (MessageResolver<T>) resolvers.get(clazz);
	}
	
	public static void sendErrorMessage(CommandSender sender, Object... objects) {
		sendMessage(sender, ChatColor.RED, objects);
	}
	
	public static void sendMessage(CommandSender sender, Object... objects) {
		sendMessage(sender, ChatColor.YELLOW, objects);
	}
	
	static void sendMessage(CommandSender sender, ChatColor colour, Object... objects) {
		BaseComponent message = new TextComponent();
		message.setColor(colour);
		
		for (Object object : objects) {
			BaseComponent nextComponent = (object == null ? NULL_COMPONENT : getComponentFromObject(object));
			message.addExtra(nextComponent);
		}
		
		sender.spigot().sendMessage(message);
	}
	
	private static BaseComponent getComponentFromObject(Object object) {
		BaseComponent component = null;
		MessageResolver<?> resolver = resolvers.get(object.getClass());
		if (resolver != null) {
			component = resolver.getUncheckedMessage(object);
		} else {
			for (Map.Entry<Class<?>, MessageResolver<?>> entry : resolvers.entrySet()) {
				Class<?> clazz = entry.getKey();
				if (clazz.isInstance(object)) {
					component = entry.getValue().getUncheckedMessage(object);
					break;
				}
			}
			
			if (component == null) {
				throw new IllegalArgumentException("Do not know how to process object " + object + " of class " + object.getClass().getName());
			}
		}
		return component;
	}
}
