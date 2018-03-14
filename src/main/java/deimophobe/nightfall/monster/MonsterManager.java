package deimophobe.nightfall.monster;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.BooleanCooldown;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.GamePlayerManager;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.spawnmenu.SpawnEggMenuItem;
import deimophobe.nightfall.monster.spawnmenu.SpawnMenu;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class MonsterManager extends GamePlayerManager<MonsterPlayer> {
	public static MonsterManager getManager() {
		return Game.getGame().getMonsterManager();
	}
	
	private final AIManager aiManager;
	private final DoomManager doomManager;
	
	private final Set<UUID> plaguedPlayers = new HashSet<>();
	
	public AIManager getAiManager() {return aiManager;}
	public DoomManager getDoomManager() {return doomManager;}
	
	private int xpCount;
	
	public MonsterManager() {
		super(ChatColor.DARK_RED + "Monsters", "mobs", ChatColor.DARK_RED);
		
		getTeam().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		
		menu = new SpawnMenu();
		SpawnEggMenuItem.resetEggs();
		
		aiManager = new AIManager();
		doomManager = new DoomManager();
		
		aiManager.start();
	}
	
	@Override
	public void stop() {
		super.stop();
		aiManager.stop();
		doomManager.stop();
	}
	
	@Override
	protected MonsterPlayer createGamePlayerFromPlayer(Player player) {
		MonsterPlayer p = new MonsterPlayer(player);
		p.forceGainXP(xpCount);
		if (plaguedPlayers.contains(p.getUniqueId())) {
			p.forceGainXP(6000);
		}
		return p;
	}
	
	public Collection<MonsterPlayer> getAlivePlayerMobs() {
		Collection<MonsterPlayer> aliveMobs = new HashSet<>(getGamePlayers());
		aliveMobs.removeIf((MonsterPlayer m) -> !m.isAlive());
		return aliveMobs;
	}
	
	public Collection<MonsterEntity> getAliveMobsAndAIs() {
		Collection<MonsterEntity> entities = new ArrayList<>();
		entities.addAll(getAlivePlayerMobs());
		entities.addAll(AIManager.getManager().getAIs());
		return entities;
	}
	
	public Collection<MonsterPlayer> getDeadPlayers() {
		Collection<MonsterPlayer> deadMobs = new HashSet<>(getGamePlayers());
		deadMobs.removeIf(MonsterPlayer::isAlive);
		return deadMobs;
	}
	
	public void addPlaguedPlayer(GamePlayer player) {
		plaguedPlayers.add(player.getUniqueId());
	}
	
	
	public MonsterPlayer getNearestAlive(Location location) {
		return getNearest(location, MonsterPlayer::isAlive);
	}
	
	
	public void giveFutureXP(int amt) {
		xpCount += amt;
	}
	
	public void onMobRelease() {
		doomManager.start();
		new BukkitRunnable() {
			@Override
			public void run() {
				xpCount += 10;
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 30, 30);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				menu.updateEggs();
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 90*20, 60*20);
	}
	
	
	// --------------------------------------------------------
	//                   MENUS N STUFF
	// --------------------------------------------------------
	
	private final SpawnMenu menu;
	
	public void showMobMenu(MonsterPlayer monster) {
		menu.startSession(monster.getPlayer());
	}
	
	public void addSpawnEgg(int i, String egg) {
		menu.addSpawnEgg(i, egg);
	}
	
	public Set<String> getUpgradeSet(MobType type) {
		return menu.getUpgradeSet(type);
	}
	
	public SpawnMenu getSpawnMenu() {
		return menu;
	}
	
	
	// --------------------------------------------------------
	//                   DEATH MESSAGER
	// --------------------------------------------------------
	
	private static final int DEATH_MSG_UPDATE_FREQ = 15;
	private final Queue<String> deathMessages = new LinkedList<>();
	private final BooleanCooldown messager = new BooleanCooldown(15, this::sendMessages, this::resetMessager);
	
	private boolean sendMessages() {
		if (deathMessages.isEmpty()) return false;
		
		String message = deathMessages.poll();
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
		}
		//Bukkit.broadcastMessage(message);
		return true;
	}
	
	private void resetMessager() { messager.tryUse(); }
	
	@Override
	protected void update() {
		super.update();
		messager.update();
	}
	
	public void queueDeathMessage(String deathMsg) {
		deathMessages.offer(deathMsg);
		messager.tryUse();
	}
}
	
