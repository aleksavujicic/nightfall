package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.monster.MobCreator;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnRegistry;
import deimophobe.nightfall.monster.doom.DoomManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophbe on 19/01/17.
 */
public class SpawnEggMenuItem implements MenuItem<MonsterPlayer> {
	private final MobCreator<?> mobCreator;
	
	private final ItemStack item;
	
	private final boolean permanent;
	private boolean enabled;
	
	private int quantity;
	private int maxQuantity;
	private double spawnChance;
	private final String name;
	
	public static SpawnEggMenuItem fromConfig(ConfigurationSection section, String name) {
		CustomItem item = CustomItem.getItem(section.getConfigurationSection("egg"), "monster-egg");
		
		String creatorName = section.getString("mobtype");
		SpawnRegistry registry = SpawnRegistry.getRegistry();
		if (!registry.isValid(creatorName)) {
			throw new IllegalArgumentException("Unknown mob creator '" + creatorName + "' when creating spawnegg " + section.getName());
		}
		
		MobCreator<?> mobCreator = registry.getCreator(creatorName);
		
		int quantity = 0;
		int maxQuantity = section.getInt("quantity", 1);
		double spawnChance = section.getDouble("chance", 0.5);
		boolean permanent = section.getBoolean("permanent", false);
		
		boolean enabled = section.getBoolean("enabled", true);
		
		return new SpawnEggMenuItem(item, name, mobCreator, maxQuantity, spawnChance, permanent, enabled);
	}
	
	public SpawnEggMenuItem(CustomItem item, String name, MobCreator<?> mobCreator, int maxQuantity, double chance, boolean permanent, boolean enabled) {
		this.item = item.createItemStack();
		this.name = name;
		
		this.mobCreator = mobCreator;
		
		this.quantity = 0;
		this.maxQuantity = maxQuantity;
		this.spawnChance = chance;
		this.permanent = permanent;
		
		this.enabled = enabled;
	}
	
	public boolean tryRestock() {
		double rand = Math.random();
		if (rand <= spawnChance) {
			quantity = maxQuantity;
			return true;
		} else {
			return false;
		}
	}
	
	public void setMax(int max) { this.maxQuantity = max; }
	public void setSpawnChance(double chance) { this.spawnChance = chance; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	
	public String getName() {
		return name;
	}
	
	public void restock() {
		quantity = maxQuantity;
	}
	
	private boolean isAvailable() {
		return (enabled && (permanent || quantity != 0));
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
		if (permanent) {
			return item;
		}
		if (isAvailable()) {
			ItemStack newitem = item.clone();
			Player player = session.getPlayer();
			if (Game.getGame().isDebug(player)) newitem.setAmount(quantity);
			return newitem;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		if (!isAvailable()) return true; // Updates menu display
		
		MonsterPlayer monster = session.getData();
		
		if (!Game.getGame().getPhase().haveMonstersBeenReleased() && Game.getGame().getPhase() != Phase.STARTING) {
			monster.sendMessage(ChatColor.RED + "You must wait until the mobs are released!");
			return false;
		}
		
		if (DoomManager.getManager().isDoom()) {
			monster.sendMessage(ChatColor.RED + "You cannot spawn during doom!");
			return false;
		}
		
		if (monster.isMobAlive()) {
			monster.sendMessage(ChatColor.RED + "You have already spawned as a mob!");
			session.closeSession();
			return false;
		}

		monster.spawnMob(mobCreator);
		quantity -= 1;
		session.closeSession();
		return false;
	}
}
