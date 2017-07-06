package deimophobe.nightfall;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.dwarf.kit.elements.Horn;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import deimophobe.nightfall.dwarf.loadout.DwarfData;
import deimophobe.nightfall.dwarf.loadout.Loadout;
import deimophobe.nightfall.dwarf.loadout.LoadoutMenu;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.ItemManager;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.MapManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.doom.DoomType;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.plague.Plague;
import deimophobe.nightfall.plague.PlagueType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class NightfallPlugin extends JavaPlugin {
	
	private static NightfallPlugin plugin;
	private GameListener gl;
	
	public static NightfallPlugin getPlugin() {return plugin;}
	
	private Game game;
	private DwarfManager dm;
	private MonsterManager mm;
	
	@Override
	public void onEnable() {
		//Bukkit.getLogger().info("AYYYY LMAO");
		plugin = this;
		
		setupPacketEvents();
		Loadout.setupLoadouts();
		
		gl = new GameListener();
		
		Game.createNewGame();
		
		Bukkit.getPluginManager().registerEvents(gl, NightfallPlugin.getPlugin());
	}

	@Override
	public void onDisable() {
		Loadout.saveLoadouts();
		game.stop();
		Misc.removeAllTeams();
	}
	
	public void updateManagers() {
		game = Game.getGame();
		dm = DwarfManager.getManager();
		mm = MonsterManager.getManager();
		gl.updateManagers();
	}
	
	private void setupPacketEvents() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(NightfallPlugin.getPlugin(), PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				EnumWrappers.ItemSlot slot = event.getPacket().getItemSlots().read(0);
				if (slot == EnumWrappers.ItemSlot.OFFHAND) {
					event.setCancelled(true);
				}
			}
		});
		
		protocolManager.addPacketListener(new PacketAdapter(NightfallPlugin.getPlugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				Sound sound = event.getPacket().getSoundEffects().read(0);
				switch (sound) {
					case ENTITY_PLAYER_ATTACK_CRIT:
					case ENTITY_PLAYER_ATTACK_KNOCKBACK:
					case ENTITY_PLAYER_ATTACK_NODAMAGE:
					case ENTITY_PLAYER_ATTACK_STRONG:
					case ENTITY_PLAYER_ATTACK_SWEEP:
					case ENTITY_PLAYER_ATTACK_WEAK:
						event.setCancelled(true);
				}
			}
		});
	}
	
	
	// ~~~~~ COMMAND STUFF ~~~~~
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		String name = command.getName();
		if (name.equalsIgnoreCase("setdwarf")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a dwarf.");
				return false;
			} else if (args.length == 1) {
				dm.removeGamePlayer(args[0], true);
				mm.removeGamePlayer(args[0], true);
				boolean success = (dm.addGamePlayer(args[0]) != null);
				if (success) {
					sender.sendMessage(ChatColor.AQUA + "Added " + ChatColor.DARK_AQUA + args[0] + ChatColor.AQUA + " as a dwarf!");
				} else {
					sender.sendMessage(ChatColor.RED + "Could not add " + ChatColor.DARK_AQUA + args[0] + ChatColor.RED + " as a dwarf!");
				}
				return true;
			} else {
				Player player = Bukkit.getPlayer(args[0]);
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "Could not find player: " + ChatColor.DARK_AQUA + args[0] + ChatColor.RED + "!");
					return true;
				}
				
				dm.removeGamePlayer(args[0], true);
				mm.removeGamePlayer(args[0], true);
				Set<KitElementType> elements = new HashSet<>();
				if (args[1].equalsIgnoreCase("all")) {
					sender.sendMessage(ChatColor.YELLOW + "Adding all elements!");
					for (KitElementType type : KitElementType.values())
						elements.add(type);
				} else {
					for (int i = 1; i < args.length; i++) {
						if (!KitElementType.isElement(args[i])) {
							sender.sendMessage(ChatColor.RED + "Unknown kit item: " + ChatColor.DARK_AQUA + args[i] + ChatColor.RED + "!");
							continue;
						}
						elements.add(KitElementType.get(args[i]));
					}
				}
				dm.createDwarf(player, new DwarfData(null, false, null, elements, null));
				return true;
			}
		}
		if (name.equalsIgnoreCase("sethero")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a player.");
				return false;
			} else if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a hero.");
				return false;
			} else {
				Hero.Type type = Hero.Type.getHeroType(args[1]);
				if (type == null) {
					sender.sendMessage(ChatColor.RED + "Unknown hero type: " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
					return true;
				} else {
					dm.removeGamePlayer(args[0], true);
					mm.removeGamePlayer(args[0], true);
					dm.addHero(args[0], type);
					return true;
				}
			}
		}
		if (name.equalsIgnoreCase("setmob")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a monster.");
				return false;
			} else {
				dm.removeGamePlayer(args[0], true);
				mm.removeGamePlayer(args[0], true);
				boolean success = (mm.addGamePlayer(args[0]) != null);
				if (success) {
					sender.sendMessage(ChatColor.AQUA + "Added " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a monster!");
					if (args.length >= 2) {
						MonsterPlayer monster = mm.getGamePlayer(args[0]);
						MobType type = MobType.getMobType(args[1]);
						if (type == null) {
							sender.sendMessage(ChatColor.RED + "Unknown mob type: " + ChatColor.YELLOW + args[1] + ChatColor.RED + "!");
							return true;
						} else {
							monster.kill();
							monster.spawnMobType(type);

							sender.sendMessage(ChatColor.AQUA + "Spawned " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a " + ChatColor.YELLOW + args[1] + ChatColor.AQUA + "!");
							return true;
						}
					}
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "Could not add " + ChatColor.DARK_RED + args[0] + ChatColor.RED + " as a monster!");
					return true;
				}
			}
		}
		if (name.equalsIgnoreCase("spawnmob")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a monster.");
				return false;
			} else if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a mob.");
				return false;
			} else {
				MonsterPlayer monster = mm.getGamePlayer(args[0]);
				if (monster == null) {
					sender.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a monster!");
					return true;
				}
				
				MobType type = MobType.getMobType(args[1]);
				if (type == null) {
					sender.sendMessage(ChatColor.RED + "Unknown mob type: " + ChatColor.YELLOW + args[1] + ChatColor.RED + "!");
					return true;
				} else {
					monster.kill();
					monster.spawnMobType(type);
					
					sender.sendMessage(ChatColor.AQUA + "Spawned " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a " + ChatColor.YELLOW + args[1] + ChatColor.AQUA + "!");
					return true;
				}
			}
		}
		if (name.equalsIgnoreCase("removeplayer")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a player.");
				return false;
			} else {
				boolean success = dm.removeGamePlayer(args[0], true);
				if (success) {
					sender.sendMessage(ChatColor.AQUA + "Removed " + ChatColor.DARK_AQUA + args[0] + ChatColor.AQUA + " as a dwarf!");
					return true;
				} else {
					success = mm.removeGamePlayer(args[0], true);
					if (success) {
						sender.sendMessage(ChatColor.AQUA + "Removed " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a monster!");
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "Could not remove " + ChatColor.GOLD + args[0] + ChatColor.RED + " as a player!");
						return true;
					}
				}
			}
		}
		if (name.equalsIgnoreCase("toggleais")) {
			if (AIManager.getManager().toggleAISpawn())
				sender.sendMessage(ChatColor.AQUA + "AIs are now " + ChatColor.GOLD + "ENABLED");
			else
				sender.sendMessage(ChatColor.AQUA + "AIs are now " + ChatColor.RED + "DISABLED");
			return true;
		}
		if (name.equalsIgnoreCase("toggledoom")) {
			if (DoomManager.getManager().toggleDoom())
				sender.sendMessage(ChatColor.AQUA + "Doom is now " + ChatColor.GOLD + "ENABLED");
			else
				sender.sendMessage(ChatColor.AQUA + "Doom is now " + ChatColor.RED + "DISABLED");
			return true;
		}
		if (name.equalsIgnoreCase("horn")) {
			Horn.tootHorn();
			return true;
		}
		if (name.equalsIgnoreCase("giveitem")) {
			if (sender instanceof Player) {
				if (args.length == 0) return false;
				
				for (String arg : args) {
					CustomItem item = ItemManager.getManager().getItem(arg);
					if (item == null) {
						sender.sendMessage(ChatColor.RED + "Unknown item: " + ChatColor.YELLOW + arg + ChatColor.RED + "!");
					} else {
						((Player) sender).getInventory().addItem(item.createItemStack());
						sender.sendMessage(ChatColor.AQUA + "Giving one of '" + ChatColor.YELLOW + arg + ChatColor.AQUA + "'.");
					}
				}
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("trash")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null)
					dwarf.showTrash();
				else
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("chest")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null)
					dwarf.showSharedChest();
				else
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("compass")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null)
					dwarf.giveCompass();
				else
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("armour")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null) {
					if (args.length == 1) {
						if (args[0].equals("equip")) {
							dwarf.getArmour().putOn();
							return true;
						}
					} else if (args.length == 2) {
						int amt;
						try {
							amt = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
							sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[0] + ChatColor.RED + " is not a number!");
							return false;
						}
						switch (args[0]) {
							case "damage":
								dwarf.getArmour().damage(amt);
								return true;
							case "repair":
								dwarf.getArmour().repair(amt);
								return true;
						}
					}
					return false;
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				}
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("xp")) {
			if (sender instanceof Player) {
				MonsterPlayer monster = mm.getGamePlayer((Player) sender);
				if (monster != null) {
					if (args.length >= 1) {
						try {
							int amt = Integer.parseInt(args[0]);
							monster.forceGainXP(amt);
							sender.sendMessage(ChatColor.YELLOW + "Giving you " + ChatColor.GREEN + amt + ChatColor.YELLOW + " xp!");
							return true;
						} catch (NumberFormatException e) {
							sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[0] + ChatColor.RED + " is not a number!");
							return false;
						}
					}
					return false;
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a monster to do that");
				}
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("damage")) {
			try {
				if (args.length == 1 && sender instanceof Player) {
					double dmg = Double.parseDouble(args[0]);
					((Player) sender).damage(dmg, (Entity) sender);
					sender.sendMessage(ChatColor.YELLOW + "Damaged you for " + ChatColor.GREEN + dmg + ChatColor.YELLOW + " damage.");
					return true;
				} else if (args.length >= 2) {
					double dmg = Double.parseDouble(args[1]);
					Player target = Bukkit.getPlayer(args[0]);
					if (target == null) return false;
					
					GamePlayer gp = Game.getGame().getGamePlayer(target);
					if (gp == null) {
						if (sender instanceof Entity)
							target.damage(dmg, (Entity) sender);
						else
							target.damage(dmg);
					} else {
						gp.customDamage(null, DamageType.COMMAND, dmg);
					}
					
					sender.sendMessage(ChatColor.YELLOW + "Damaged " + target.getDisplayName() + ChatColor.YELLOW + " for " + ChatColor.GREEN + dmg + ChatColor.YELLOW + " damage.");
					target.sendMessage(ChatColor.YELLOW + "You got damaged by " + ChatColor.RED +  sender.getName() + ChatColor.YELLOW + " for " + ChatColor.GREEN + dmg + ChatColor.YELLOW + " damage.");
					return true;
				} else {
					return false;
				}
			} catch (NumberFormatException e) {
				sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[0] + ChatColor.RED + " is not a number!");
				return false;
			}
		}
		if (name.equalsIgnoreCase("plagueimmune")) {
			if (args.length == 0) {
				Player player;
				if (sender instanceof Player) {
					player = (Player) sender;
				} else {
					sender.sendMessage(ChatColor.RED + "You must choose a player");
					return true;
				}
				
				Dwarf dwarf = dm.getGamePlayer(player);
				if (dwarf == null) {
					sender.sendMessage(ChatColor.RED + "You are not a dwarf!");
					return true;
				}
				
				boolean nowImmune = dwarf.togglePlagueImmunity();
				if (nowImmune) {
					dwarf.sendMessage(ChatColor.YELLOW + "You are now immune to plague!");
				} else {
					dwarf.sendMessage(ChatColor.GREEN + "You are no longer immune to plague!");
				}
				return true;
			} else {
				Player player = Bukkit.getPlayer(args[0]);
				
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "Could not find player: " + ChatColor.DARK_AQUA + args[0] + ChatColor.RED + "!");
					return true;
				}
				
				Dwarf dwarf = dm.getGamePlayer(player);
				if (dwarf == null) {
					sender.sendMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.RED + " is not a dwarf!");
					return true;
				}
				
				boolean nowImmune = dwarf.togglePlagueImmunity();
				if (nowImmune) {
					sender.sendMessage(dwarf.getDisplayName() + ChatColor.YELLOW + " is now immune to plague!");
					dwarf.sendMessage(ChatColor.YELLOW + "You are now immune to plague!");
				} else {
					sender.sendMessage(dwarf.getDisplayName() + ChatColor.GREEN + " is no longer immune to plague!");
					dwarf.sendMessage(ChatColor.GREEN + "You are no longer immune to plague!");
				}
				return true;
			}
		}

		if (name.equalsIgnoreCase("plague")) {
			Player player;
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				sender.sendMessage(ChatColor.RED + "You must choose a player");
				return true;
			}
			Dwarf dwarf = dm.getGamePlayer(player);
			if (dwarf == null) {
				sender.sendMessage(ChatColor.RED + "You are not a dwarf!");
				return true;
			}

			boolean nowSick = dwarf.togglePlagued();
			if (nowSick) {
				if (dwarf.isPlagueImmune()) {
					dwarf.togglePlagueImmunity();
				}
				dwarf.sendMessage(ChatColor.GREEN + "You are now plagued!");
			} else {
				dwarf.sendMessage(ChatColor.YELLOW + "You now have an ordinary chance of being plagued!");
			}
			return true;
		}


		if (name.equalsIgnoreCase("loadout")) {
			if (sender instanceof Player) {
				LoadoutMenu.getMenu().startSession((Player) sender);
				
				return true;
			}
		}
		if (name.equalsIgnoreCase("stuck")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.getGameMode() == GameMode.ADVENTURE) {
					Game.getGame().resetPlayer(player);
				}
				
				return true;
			}
		}
		if (name.equalsIgnoreCase("explore")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (Game.getGame().isLobbyPlayer(player)) {
					player.teleport(GameMap.getCurrentMap().getDwarfSpawn());
				}
				
				return true;
			}
		}
		if (name.equalsIgnoreCase("hat")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				PlayerInventory inv = player.getInventory();
				ItemStack item = inv.getItemInMainHand();
				ItemStack hat = inv.getHelmet();
				inv.setHelmet(item);
				inv.setItemInMainHand(hat);
				return true;
			}
		}
		if (name.equalsIgnoreCase("forcestart")) {
			game.startGame();
			return true;
		}
		if (name.equalsIgnoreCase("forceplague")) {
			if (args.length == 0) {
				game.startPlague();
				return true;
			} else {
				try {
					Plague plague = PlagueType.valueOf(args[0].toUpperCase()).getPlague();
					game.startPlague(plague);
					return true;
				} catch (IllegalStateException e) {
					sender.sendMessage(ChatColor.RED + "Unknown plague: " + ChatColor.YELLOW + args[0]);
					return false;
				}
			}
		}
		if (name.equalsIgnoreCase("forcedoom")) {
			if (game.getPhase() == Phase.GAME)
				DoomManager.getManager().reduceDoom(10000);
			return true;
		}
		if (name.equalsIgnoreCase("summondoom")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a doom.");
				return false;
			}
			
			DoomType type = DoomType.getDoomType(args[0]);
			if (type == null) {
				sender.sendMessage(ChatColor.RED + "Unknown doom type: " + ChatColor.YELLOW + args[0] + ChatColor.RED + "!");
			} else {
				DoomManager.getManager().spawnDoom(type);
			}
			
			return true;
		}
		if (name.equalsIgnoreCase("who")) {
			sender.sendMessage(dm.getPlayerList() + "\n" +  mm.getPlayerList());
			return true;
		}
		
		if (name.equalsIgnoreCase("map")) {
			if (args.length == 0)
				return false;
			
			MapManager man = MapManager.getManager();
			
			switch (args[0]) {
				case "enable":
					try {
						man.setMapsEnabled(true);
					} catch (IOException e) {
						e.printStackTrace();
						sender.sendMessage(ChatColor.RED + "Failed to enable map loading.");
						return true;
					}
					sender.sendMessage(ChatColor.GOLD + "Enabled map loading. This will make reloads take longer.");
					sender.sendMessage(ChatColor.GREEN + "You must reload before changes will take effect.");
					return true;
					
				case "disable":
					try {
						man.setMapsEnabled(false);
					} catch (IOException e) {
						e.printStackTrace();
						sender.sendMessage(ChatColor.RED + "Failed to disable map loading.");
						return true;
					}
					sender.sendMessage(ChatColor.GOLD + "Disabled map loading.");
					sender.sendMessage(ChatColor.GREEN + "You must reload before changes will take effect.");
					return true;
			}
			if (!man.isEnabled()) {
				sender.sendMessage(ChatColor.RED + "GameMap loading is currently disabled.");
				return true;
			}
			
			switch (args[0]) {
				case "reload":
					man.reloadConfig();
					sender.sendMessage(ChatColor.GOLD + "Reloaded map config. " + ChatColor.GRAY + ChatColor.ITALIC + "(Enabling/disabling requires a reload).");
					return true;
				case "list":
					List<String> mapList = man.getMapQueue();
					if (mapList.isEmpty()) {
						sender.sendMessage(ChatColor.GOLD + "No maps queued!");
						return true;
					}
					String maps = StringUtils.join(mapList, ChatColor.RESET + ", " + ChatColor.GREEN);
					sender.sendMessage(ChatColor.GOLD + "Current map list:");
					sender.sendMessage(ChatColor.GREEN + "  " + maps);
					return true;
				case "clear":
					man.clearMapQueue();
					sender.sendMessage(ChatColor.GOLD + "Cleared map queue.");
					return true;
				case "next":
					sender.sendMessage(ChatColor.GOLD + "Starting new game. Map will be: " + ChatColor.GREEN + man.peekMap());
					Game.createNewGame();
					sender.sendMessage(ChatColor.GOLD + "Game loaded");
					return true;
			}
			
			if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a map.");
				return false;
			}
			String map = args[1].toLowerCase();
			switch (args[0]) {
				case "queue":
					boolean success = man.tryEnqueueMap(map);
					if (success)
						sender.sendMessage(ChatColor.GOLD + "Successfully queued map " + ChatColor.GREEN +  map);
					else
						sender.sendMessage(ChatColor.RED + "Unknown map " + ChatColor.YELLOW + map);
					return true;
				case "play":
				case "load":
					boolean success2 = man.tryInsertMap(map);
					if (success2) {
						sender.sendMessage(ChatColor.GOLD + "Starting new game on map " + ChatColor.GREEN + map);
						Game.createNewGame();
						sender.sendMessage(ChatColor.GOLD + "Game loaded");
					} else {
						sender.sendMessage(ChatColor.RED + "Unknown map " + ChatColor.YELLOW + map);
					}
					return true;
			}
			return false;
		}
		if (name.equalsIgnoreCase("kills")) {
			if (sender instanceof Player) {
				((Player) sender).kickPlayer("Don't be toxic.");
			}
			return true;
		}
		if (name.equalsIgnoreCase("shrine")) {
			if (game.getPhase() != Phase.GAME) {
				sender.sendMessage(ChatColor.RED + "The game has not yet begun! Use /forcestart and /forceplague.");
				return true;
			}
			
			GameMap map = GameMap.getCurrentMap();
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("kill")) {
					map.damageShrine(100000);
					return true;
				} else {
					return false;
				}
			} else if (args.length == 2) {
				int percent = 0;
				try{
					percent = Integer.parseInt(args[1]);
				}catch(NumberFormatException e) {
					sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[0] + ChatColor.RED + " is not a number!");
					return false;
				}
				switch (args[0]) {
					case "kill":
						map.damageShrine(100000);
						return true;
					case "damage":
						map.damageShrine(percent);
						return true;
					case "recover":
						map.recoverShrine(percent);
						return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		String name = command.getName();
		if (name.equalsIgnoreCase("map")) {
			if (args.length == 1) {
				return startsWithPrefix(args[0], "enable", "disable", "reload", "list", "clear", "next", "queue", "play", "load");
			} else if (args.length == 2) {
				switch (args[0]) {
					case "queue":
					case "play":
					case "load":
						return startsWithPrefix(args[1], MapManager.getManager().getMaps());
				}
			}
		}
		
		if (name.equalsIgnoreCase("setdwarf") && args.length >= 2) {
			Collection<String> elements = KitElementType.getElementNames();
			if (args.length == 2) elements.add("all");
			return startsWithPrefix(args[args.length-1], elements);
		}
		
		if (name.equalsIgnoreCase("sethero") && args.length == 2) {
			return startsWithPrefix(args[args.length-1], Hero.Type.getHeroList());
		}

		if (name.equalsIgnoreCase("setmob") && args.length == 2) {
			return startsWithPrefix(args[args.length-1], MobType.getAllMobTypes());
		}
		
		if (name.equalsIgnoreCase("forceplague") && args.length == 1) {
			return startsWithPrefix(args[args.length-1], PlagueType.getPlagues());
		}

		if (name.equalsIgnoreCase("spawnmob") && args.length == 2) {
			return startsWithPrefix(args[args.length-1], MobType.getAllMobTypes());
		}
		
		if (name.equalsIgnoreCase("summondoom") && args.length == 1) {
			return startsWithPrefix(args[args.length-1], DoomType.getAllTypes());
		}
		
		if (name.equalsIgnoreCase("armour") && args.length == 1) {
			return startsWithPrefix(args[args.length-1], "damage", "repair", "equip");
		}
		
		if (name.equalsIgnoreCase("giveitem")) {
			return startsWithPrefix(args[args.length-1], ItemManager.getManager().getNames());
		}

		if (name.equalsIgnoreCase("shrine") && args.length == 1) {
			return startsWithPrefix(args[args.length-1], "kill", "damage", "recover");
		}

		return null;
	}
	
	private static List<String> startsWithPrefix(String prefix, String... strings) {
		return startsWithPrefix(prefix, Arrays.asList(strings));
	}
	
	private static List<String> startsWithPrefix(String prefix, Collection<String> strings) {
		List<String> matchStrings = new ArrayList<>();
		for (String string : strings) {
			if (string.toLowerCase().startsWith(prefix.toLowerCase()))
				matchStrings.add(string);
		}
		return matchStrings;
	}
}
