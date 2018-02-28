package deimophobe.nightfall;

import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.cosmetic.CosmeticManager;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.common.loadout.LoadoutMenu;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfData;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.hero.HeroType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.hero.Horn;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.MapManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.doom.DoomType;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.spawnmenu.SpawnEggMenuItem;
import deimophobe.nightfall.plague.Plague;
import deimophobe.nightfall.plague.PlagueType;
import deimophobe.nightfall.util.PacketUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	
	private boolean disabling = false;
	public boolean isDisabling() { return disabling; }
	
	@Override
	public void onEnable() {
		plugin = this;
		
		PacketUtil.setupListeners();
		
		gl = new GameListener();
		
		Game.createNewGame();
		
		Bukkit.getPluginManager().registerEvents(gl, NightfallPlugin.getPlugin());
	}

	@Override
	public void onDisable() {
		disabling = true;
		game.stop();
	}
	
	public void updateManagers() {
		game = Game.getGame();
		dm = DwarfManager.getManager();
		mm = MonsterManager.getManager();
		gl.updateManagers();
	}
	
	public static YamlConfiguration getInternalFileConfig(String name) {
		InputStream stream = getPlugin().getResource(name);
		if (stream == null) throw new IllegalArgumentException("Unknown config file: " + name);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
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
				
				if (args[1].equalsIgnoreCase("loadoutall")) {
					DwarfData data = new DwarfData();
					LoadoutManager.getManager().modifyAll(data);
					dm.createDwarf(player, data);
					return true;
				}
				
				Set<KitPieceType> elements = new HashSet<>();
				if (args[1].equalsIgnoreCase("all")) {
					elements.addAll(Arrays.asList(KitPieceType.values()));
				} else {
					for (int i = 1; i < args.length; i++) {
						try {
							elements.add(KitPieceType.fromString(args[i]));
						} catch (UnknownEnumElementException e) {
							sender.sendMessage(ChatColor.RED + "Unknown kit item: " + ChatColor.DARK_AQUA + args[i] + ChatColor.RED + "!");
						}
					}
				}
				dm.createDwarf(player, new DwarfData(elements, null));
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
				try {
					HeroType type = HeroType.fromString(args[1]);
					dm.removeGamePlayer(args[0], true);
					mm.removeGamePlayer(args[0], true);
					dm.addHero(args[0], type);
					return true;
				} catch (UnknownEnumElementException e) {
					sender.sendMessage(ChatColor.RED + "Unknown hero type: " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
					return false;
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
						try {
							MobType type = MobType.getMobType(args[1]);
							
							if (!type.isSpawnable()) {
								sender.sendMessage(ChatColor.RED + "Unknown mob type: " + ChatColor.YELLOW + args[1] + ChatColor.RED + "!");
								return false;
							}
							
							monster.spawnMob(type);
							
							sender.sendMessage(ChatColor.AQUA + "Spawned " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a " + ChatColor.YELLOW + args[1] + ChatColor.AQUA + "!");
							return true;
						} catch (UnknownEnumElementException e) {
							sender.sendMessage(ChatColor.RED + "Unknown mob type: " + ChatColor.YELLOW + args[1] + ChatColor.RED + "!");
							return false;
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
				
				try {
					MobType type = MobType.getMobType(args[1]);
					
					if (!type.isSpawnable()) {
						sender.sendMessage(ChatColor.RED + "Unknown mob type: " + ChatColor.YELLOW + args[1] + ChatColor.RED + "!");
						return false;
					}
					
					monster.spawnMob(type);
					
					sender.sendMessage(ChatColor.AQUA + "Spawned " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a " + ChatColor.YELLOW + args[1] + ChatColor.AQUA + "!");
					return true;
				} catch (UnknownEnumElementException e) {
					sender.sendMessage(ChatColor.RED + "Unknown mob type: " + ChatColor.YELLOW + args[1] + ChatColor.RED + "!");
					return false;
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
		if (name.equalsIgnoreCase("resetegg")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a monster.");
				return false;
			} else {
				String eggName = args[0];
				SpawnEggMenuItem egg = SpawnEggMenuItem.getEgg(eggName);
				if (egg == null) {
					sender.sendMessage(ChatColor.RED + "No egg called '" + ChatColor.YELLOW + eggName + ChatColor.RED + "'");
					return false;
				}
				egg.restock();
				sender.sendMessage(ChatColor.GREEN + "Successfully restocked egg '" + ChatColor.YELLOW + eggName + ChatColor.GREEN + "'");
				return true;
			}
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
					dwarf.giveChesto();
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
		if (name.equalsIgnoreCase("clock")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null)
					dwarf.giveClock();
				else
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("addkititem")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify an item.");
				return false;
			} else {
				if (sender instanceof Player) {
					Dwarf dwarf = dm.getGamePlayer((Player)sender);
					if (dwarf != null) {
						for (String arg : args) {
							try {
								dwarf.giveKitItem(KitPieceType.fromString(arg));
							} catch (UnknownEnumElementException e) {
								sender.sendMessage(ChatColor.RED + "Unknown kit item: " + ChatColor.DARK_AQUA + arg + ChatColor.RED + "!");
							}
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
					}
					
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
					return false;
				}
			}
		}
		if (name.equalsIgnoreCase("kitlist")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(ChatColor.AQUA);
					sb.append("You have the following kit items:\n");
					sb.append(ChatColor.RESET);
					for (KitPieceType type : dwarf.getKitElementTypes()) {
						sb.append(type.toString().toLowerCase());
						sb.append(", ");
					}
					sb.setLength(sb.length() - 2);
					sender.sendMessage(sb.toString());
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				}
				
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				return false;
				}
		}
		
		if (name.equalsIgnoreCase("armour")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null) {
					Armour armour = dwarf.getArmour();
					if (!(armour instanceof DwarvenArmour)) {
						sender.sendMessage(ChatColor.RED + "You need to have regular armour to use /armour");
						return false;
					}
					DwarvenArmour darmour = (DwarvenArmour) armour;
					
					if (args.length == 0) {
						darmour.putOn();
						return true;
					} else if (args.length == 1) {
						switch (args[0]) {
							case "equip":
								darmour.putOn();
								return true;
							case "amount":
								double value = darmour.getValue();
								sender.sendMessage("" + ChatColor.YELLOW + "You have " + ChatColor.AQUA + (int)value + ChatColor.YELLOW + " armour left.");
								return true;
						}
					} else if (args.length == 2) {
						int amt;
						try {
							amt = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
							sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[1] + ChatColor.RED + " is not a number!");
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
		if (name.equalsIgnoreCase("givearrow")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null) {
					int amt;
					
					if (args.length == 0) {
						amt = 1;
					} else {
						try {
							amt = Integer.parseInt(args[0]);
						} catch (NumberFormatException e) {
							sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[1] + ChatColor.RED + " is not a number!");
							return false;
						}
					}
					dwarf.giveArrows(amt);
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				}
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("mana")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null) {
					int amt;
					
					if (args.length == 0) {
						amt = 1;
					} else {
						try {
							amt = Integer.parseInt(args[0]);
						} catch (NumberFormatException e) {
							sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[1] + ChatColor.RED + " is not a number!");
							return false;
						}
					}
					dwarf.regenMana(amt);
					return true;
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
					
					GamePlayer gp = Game.getGame().getGamePlayer((Player) sender);
					if (gp == null) {
						((Player) sender).damage(dmg, (Entity) sender);
					} else {
						gp.doDamage(null, GameDamageType.COMMAND, dmg, true);
					}
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
						gp.doDamage(null, GameDamageType.COMMAND, dmg, true);
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

		if (name.equalsIgnoreCase("gold")) {
			try {
				if (args.length == 1) {
					int gold = Integer.parseInt(args[0]);
					GameMap.getCurrentMap().addGold(gold);
					return true;
				}
				else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("shrine")) {
						int gold = Integer.parseInt(args[1]);
						GameMap.getCurrentMap().addGold(gold);
						return true;
					} else if (args[0].equalsIgnoreCase("vault")) {
						int gold = Integer.parseInt(args[1]);
						GameMap.getCurrentMap().addVaultGold(gold);
						return true;
					} else {
						return false;
					}
				}
				else {
					return false;
				}
			}
			catch (NumberFormatException e) {
				sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[0] + ChatColor.RED + " is not an integer!");
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
		
		if (name.equalsIgnoreCase("title")) {
			if (sender instanceof Player) {
				CosmeticManager.getManager().openTitleMenu((Player) sender);
				
				return true;
			}
		}
		
		if (name.equalsIgnoreCase("hat")) {
			if (sender instanceof Player) {
				CosmeticManager.getManager().openHatMenu((Player) sender);
				return true;
			}
		}

		if (name.equalsIgnoreCase("stuck")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				Game game = Game.getGame();
				if (game.isLobbyPlayer(player)) {
					game.resetPlayer(player);
				} else  {
					Dwarf dwarf = DwarfManager.getManager().getGamePlayer(player);
					if (dwarf != null && game.getPhase() == Phase.BUILD) {
						dwarf.sendTitleMessage(ChatColor.YELLOW + "Teleporting in 10 seconds...");
						new BukkitRunnable() {
							@Override
							public void run() {
								if (dwarf.isOnline() && Game.getGame().getPhase() == Phase.BUILD) {
									dwarf.teleportTo(GameMap.getCurrentMap().getDwarfSpawn());
								}
							}
						}.runTaskLater(this, 10*20);
					}
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

		if (name.equalsIgnoreCase("ready")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (game.isLobbyPlayer(player)) {
					if (!game.isReady(player)) {
						game.readyPlayer(player);
					} else {
						game.unreadyPlayer(player, false);
					}
				}
				return true;
			}
		}
		
		if (name.equalsIgnoreCase("readylist")) {
			sender.sendMessage(game.readyList());
			return true;
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
					Plague plague = PlagueType.getPlagueType(args[0]).createPlague();
					game.startPlague(plague);
					return true;
				} catch (UnknownEnumElementException e) {
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
			
			try {
				DoomType type = DoomType.getDoomType(args[0]);
				DoomManager.getManager().spawnDoom(type);
			} catch (UnknownEnumElementException e) {
				sender.sendMessage(ChatColor.RED + "Unknown doom type: " + ChatColor.YELLOW + args[0] + ChatColor.RED + "!");
			}
			
			return true;
		}
		if (name.equalsIgnoreCase("spawnai")) {
			if (sender instanceof Player) {
				int num;
				if (args.length == 0) {
					num = 1;
				} else {
					try {
						num = Integer.parseInt(args[0]);
					} catch (NumberFormatException e) {
						sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[0] + ChatColor.RED + " is not a number!");
						return false;
					}
					num = Math.min(num, 300);
				}
				
				AIManager.getManager().spawnAIs(((Player) sender).getLocation(), num);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
			}
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

		/*
		if (name.equalsIgnoreCase("kills")) {
			if (sender instanceof Player) {
				((Player) sender).kickPlayer("Don't be toxic.");
			}
			return true;
		}
		*/

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
				int value;
				try{
					value = Integer.parseInt(args[1]);
				}catch(NumberFormatException e) {
					sender.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + args[0] + ChatColor.RED + " is not a number!");
					return false;
				}
				switch (args[0]) {
					case "kill":
						map.damageShrine(100000);
						return true;
					case "damage":
						map.damageShrine(value);
						return true;
					case "recover":
						map.recoverShrine(value);
						return true;
				}
			}
		}
		
		if (name.equalsIgnoreCase("fixplayers")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				for (Player other : Bukkit.getOnlinePlayers()) {
					if (player.canSee(other)) {
						player.hidePlayer(other);
						player.showPlayer(other);
					}
				}
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
				return true;
			}
		}
		
		if (name.equalsIgnoreCase("fixhearts")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1, 10), true);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
				return true;
			}
		}
		
		if (name.equalsIgnoreCase("debug")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				boolean enabled = game.toggleDebug(player);
				if (enabled)
					sender.sendMessage(ChatColor.GREEN + "Debug mode enabled.");
				else
					sender.sendMessage(ChatColor.RED + "Debug mode disabled.");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
				return true;
			}
		}
		
		if (name.equalsIgnoreCase("test")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				open = !open;
				PacketUtil.setChestOpen(player.getLocation().add(1, 0.5, 0).getBlock(), open);
				sender.sendMessage(open ? "Open" : "Close");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
				return true;
			}
		}
		
		return false;
	}
	private static boolean open = false;
	
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
			Collection<String> elements = KitPieceType.getPieceNames();
			if (args.length == 2) {
				elements.add("all");
				elements.add("loadoutall");
			}
			return startsWithPrefix(args[args.length-1], elements);
		}
		
		if (name.equalsIgnoreCase("sethero") && args.length == 2) {
			return startsWithPrefix(args[args.length-1], HeroType.getHeroList());
		}

		if (name.equalsIgnoreCase("setmob") && args.length == 2) {
			return startsWithPrefix(args[args.length-1], MobType.getAllMobTypes());
		}
		
		if (name.equalsIgnoreCase("addkititem") && args.length >= 1) {
			Collection<String> elements = KitPieceType.getPieceNames();
			return startsWithPrefix(args[args.length-1], elements);
		}
		
		if (name.equalsIgnoreCase("resetegg") && args.length == 1) {
			return startsWithPrefix(args[args.length-1], SpawnEggMenuItem.getEggNames());
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
			return startsWithPrefix(args[args.length-1], "damage", "repair", "equip", "amount");
		}
		
		if (name.equalsIgnoreCase("giveitem")) {
			return startsWithPrefix(args[args.length-1], ItemManager.getManager().getNames());
		}

		if (name.equalsIgnoreCase("shrine") && args.length == 1) {
			return startsWithPrefix(args[args.length-1], "kill", "damage", "recover");
		}

		if (name.equalsIgnoreCase("gold") && args.length == 1) {
			return startsWithPrefix(args[args.length-1], "shrine", "vault");
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
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new VoidChunkGenerator();
	}
}
