package deimophobe.nightfall.common.cosmetic;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.cosmetic.hat.Hat;
import deimophobe.nightfall.common.cosmetic.hat.HatMenu;
import deimophobe.nightfall.common.cosmetic.title.TitleMenu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class CosmeticManager {
	private static CosmeticManager manager = new CosmeticManager();
	public static CosmeticManager getManager() { return manager; }
	
	private CosmeticManager() {
		manager = this;
		
		ConfigurationSection config = NightfallCommonPlugin.getInternalFileConfig("hats.yml");
		for (String key : config.getKeys(false)) {
			Hat hat = new Hat(config.getConfigurationSection(key));
			hats.put(key, hat);
		}
		
		this.titleMenu = new TitleMenu();
		this.hatMenu = new HatMenu();
	}
	
	// ----- COSEMTICS -----
	private final Map<UUID, Cosmetics> cosmetics = new HashMap<>();
	public Cosmetics getCosmetic(Player player) {
		return cosmetics.computeIfAbsent(player.getUniqueId(), Cosmetics::new);
	}
	
	// ----- HATS -----
	private final Map<String, Hat> hats = new HashMap<>();
	public Hat getHat(String name) {
		return hats.get(name.toLowerCase());
	}
	
	// ----- MENUS ------
	private final TitleMenu titleMenu;
	public void openTitleMenu(Player player) { titleMenu.startSession(player); }
	private final HatMenu hatMenu;
	public void openHatMenu(Player player) { hatMenu.startSession(player); }
	

}
