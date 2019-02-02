package deimophobe.nightfall.common.player.settings;


import deimophobe.nightfall.common.database.namedstorer.NamedStorable;
import deimophobe.nightfall.common.database.namedstorer.NoInverseException;
import deimophobe.nightfall.common.database.namedstorer.TypeBijection;
import deimophobe.nightfall.common.player.cosmetic.Hat;
import deimophobe.nightfall.common.player.cosmetic.HatStore;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 31/01/19.
 */
public class Setting<V, S> extends NamedStorable<V, S> {
	// ---- ALL SETTINGS -----
	
	private static final Map<String, Setting<?, ?>> allSettings = new HashMap<>();
	public static Setting<?, ?> getSetting(String key) {
		return allSettings.get(key);
	}
	
	
	// ---- SETTINGS -----
	
	public static final Setting<Boolean, Boolean>   HERO_ENABLED              = newBooleanSetting("hero-enabled", true);
	public static final Setting<Boolean, Boolean>   MOB_HERO_ENABLED          = newBooleanSetting("mob-hero-enabled", true);
	public static final Setting<Boolean, Boolean>   CHAT_MOB_DEATH_MESSAGES   = newBooleanSetting("mob-death-messages", false);
	public static final Setting<Boolean, Boolean>   JIT_HEAL                  = newBooleanSetting("jit-heal", true);
	public static final Setting<Hat, String>        HAT                       = newHatSetting    ("cosmetic-hat");
	public static final Setting<String, String>     TITLE                     = newStringSetting ("cosmetic-title", "");
	
	
	
	
	// ---- SETTING FACTORIES ----
	
	private static Setting<Boolean, Boolean> newBooleanSetting(String key, boolean defaultValue) {
		return new Setting<>(key, TypeBijection.BOOLEAN, defaultValue);
	}
	
	private static Setting<String, String> newStringSetting(String key, String defaultValue) {
		return new Setting<>(key, TypeBijection.STRING, defaultValue);
	}
	
	
	private static Setting<Hat, String> newHatSetting(String key) {
		return new Setting<>(key, new TypeBijection<Hat, String>() {
			@Override
			public String mapForward(Hat input) {
				return input.getIdentifier();
			}
			
			@Override
			public Hat mapBackward(String output) throws NoInverseException {
				try {
					return HatStore.getStore().getHat(output);
				} catch (IllegalArgumentException e) {
					throw new NoInverseException(e);
				}
			}
		}, Hat.class);
	}
	
	
	private Setting(@NotNull String key, @NotNull TypeBijection<V, S> resolver, @NotNull Class<V> valueType) {
		super(key, resolver, valueType);
		allSettings.put(key, this);
	}
	
	private Setting(@NotNull String key, @NotNull TypeBijection<V, S> resolver, @NotNull V defaultValue) {
		super(key, resolver, defaultValue);
		allSettings.put(key, this);
	}
}
