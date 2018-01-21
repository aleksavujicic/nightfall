package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Deimophobe on 26/01/17.
 */
class Doom {
	
	private final String title;
	private final List<String> subtitle;
	
	private final int titleCycleTime;
	
	private final List<MobType> specialMobs = new ArrayList<>();
	private final List<MobType> regularMobs = new ArrayList<>();
	
	protected Doom(ConfigurationSection section) {
		title = section.getString("title");
		subtitle = section.getStringList("subtitle");
		if (subtitle.size() == 0)
			subtitle.add(section.getString("subtitle"));
		
		titleCycleTime = section.getInt("cycle-time", 40);

		for (String special : section.getStringList("mobs.special")) {
			try {
				MobType type = MobType.getMobType(special);
				specialMobs.add(type);
			} catch (UnknownEnumElementException e) {
				Bukkit.getLogger().severe("Unknown (special) mob of type: " + special + " for doom " + title);
				e.printStackTrace();
			}
		}
		
		for (String regular : section.getStringList("mobs.regular")) {
			try {
				MobType type = MobType.getMobType(regular);
				regularMobs.add(type);
			} catch (UnknownEnumElementException e) {
				Bukkit.getLogger().severe("Unknown (regular) mob of type: " + regular + " for doom " + title);
				e.printStackTrace();
			}
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
					player.sendTitle(null, ChatColor.GOLD + subtitle.get(index), 0, titleCycleTime + 20, 20);
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), titleCycleTime*(i+1));
		}
	}
	
	protected void spawnMobs() {
		List<MonsterPlayer> monsterList = new ArrayList<>(MonsterManager.getManager().getDeadPlayers());
		Collections.shuffle(monsterList);
		
		MobSelector selector = new MobSelector();
		for (MonsterPlayer monster : monsterList) {
			monster.spawnMob(selector.get());
		}
	}
	
	private class MobSelector implements Supplier<MobType> {
		private final Iterator<MobType> iterator = specialMobs.iterator();
		private final boolean spawnSpecials;
		
		private MobSelector() {
			// TODO Should do a better selection than this
			spawnSpecials = (Game.getGame().getNumPlayers() >= 15);
		}
		
		@Override
		public MobType get() {
			if (iterator.hasNext() && spawnSpecials) {
				return iterator.next();
			} else {
				return Misc.getRandom(regularMobs);
			}
		}
	}
}
