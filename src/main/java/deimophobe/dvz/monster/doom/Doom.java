package deimophobe.dvz.monster.doom;

import deimophobe.dvz.DvZPlugin;
import deimophobe.dvz.Misc;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Deimophobe on 26/01/17.
 */
class Doom {
	
	private final String title;
	private final List<String> subtitle;
	
	private final List<MobType> specialMobs = new ArrayList<>();
	private final List<MobType> regularMobs = new ArrayList<>();
	
	protected Doom(ConfigurationSection section) {
		title = section.getString("title");
		subtitle = section.getStringList("subtitle");
		if (subtitle.size() == 0)
			subtitle.add(section.getString("subtitle"));
		
		for (String special : section.getStringList("mobs.special")) {
			MobType type = MobType.getMobType(special);
			if (type != null)
				specialMobs.add(type);
			else
				Bukkit.getLogger().severe("Unknown mob of type: " + special + " for doom " + title);
		}
		
		for (String regular : section.getStringList("mobs.regular")) {
			MobType type = MobType.getMobType(regular);
			if (type != null)
				regularMobs.add(type);
			else
				Bukkit.getLogger().severe("Unknown mob of type: " + regular + " for doom " + title);
		}
		
		if (regularMobs.size() == 0)
			Bukkit.getLogger().severe("Doom "  + title + " has no regular mobs.");
	}
	
	void startDoom() {
		for (Player player : Bukkit.getOnlinePlayers())
			showTitle(player);
		spawnMobs();
	}
	
	protected void showTitle(Player player) {
		player.sendTitle(ChatColor.RED + title, "", 20, 100, 20);
		
		for (int i=0; i < subtitle.size(); i++) {
			final int index = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					player.sendTitle(null, ChatColor.GOLD + subtitle.get(index), 0, 60, 20);
				}
			}.runTaskLater(DvZPlugin.getPlugin(), 40 + i*40);
		}
	}
	
	protected void spawnMobs() {
		List<MonsterPlayer> monsterList = new ArrayList<>(MonsterManager.getManager().getDeadPlayers());
		Collections.shuffle(monsterList);
		
		Iterator<MobType> iterator = specialMobs.iterator();
		for (MonsterPlayer monster : monsterList) {
			if (iterator.hasNext()) {
				monster.spawnMobType(iterator.next());
			} else {
				monster.spawnMobType(Misc.getRandom(regularMobs));
			}
		}
	}
}
