package nl.svenar.PowerRanks.addon;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import nl.svenar.PowerRanks.PowerRanks;
import nl.svenar.PowerRanks.addons.PowerRanksAddon;
import nl.svenar.PowerRanks.addons.PowerRanksPlayer;

public class PowerScheduler extends PowerRanksAddon {

	private String addon_prefix = ChatColor.BLACK + "[" + ChatColor.DARK_AQUA + getIdentifier() + ChatColor.BLACK + "] ";
	private int TICKS_PER_SECOND = 20;
	public Instant start;

	@Override
	public String getAuthor() {
		return "svenar";
	}

	@Override
	public String getIdentifier() {
		return "PowerScheduler";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String minimalPowerRanksVersion() {
		return "1.6.1";
	}

	@Override
	public void setup(PowerRanks powerranks) {
		setupConfigfile();

		if (!getConfig().isSet("config.check_interval")) {
			getConfig().set("config.check_interval", 600);
		}

		if (!getConfig().isSet("actions")) {
			getConfig().set("actions", new ArrayList<String>());
		}

		registerCommandAutocomplete("scheduler");
		registerPermission("powerscheduler.admin");
		registerPermission("powerscheduler.admin.bypass");

		setup_scheduler(powerranks);
		PowerRanks.log.info("Running " + getIdentifier() + " every " + getConfig().getInt("config.check_interval") + " second.");
	}

	private void setup_scheduler(PowerRanks powerranks) {
		start = Instant.now();
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(powerranks, new Runnable() {
			public void run() {
				Instant finish = Instant.now();

				long timeElapsed = Duration.between(start, finish).getSeconds(); // in millis
				PowerRanks.log.warning("Ran for " + timeElapsed + " seconds");
				start = finish;
			}
		}, 0L, getConfig().getInt("config.check_interval") * this.TICKS_PER_SECOND);
	}

	@Override
	public boolean onPowerRanksCommand(PowerRanksPlayer prPlayer, boolean sendAsPlayer, String command, String[] arguments) {
		if (command.equalsIgnoreCase("scheduler")) {
			if (arguments.length == 1) {
				if (sendAsPlayer) {
					Player player = prPlayer.getPlayer();
					if (player.hasPermission("powerscheduler.admin")) {
						player.sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + getIdentifier() + ChatColor.DARK_AQUA + "--------");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr scheduler info " + ChatColor.GREEN + "Info about how to use actions");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr scheduler list " + ChatColor.GREEN + "List all actions");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr scheduler create <action_name> " + ChatColor.GREEN + "Create a new empty action");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr scheduler delete <action_name> " + ChatColor.GREEN + "Delete a action");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr scheduler enable <action_name> " + ChatColor.GREEN + "Enable a action");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr scheduler disable <action_name> " + ChatColor.GREEN + "Disable a action");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr scheduler setcondition <action_name> <condition> " + ChatColor.GREEN + "Set the condition which needs to be met before triggering");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr scheduler setaction <action_name> <action> " + ChatColor.GREEN + "Set the action which will be triggered when a condition is met");
						player.sendMessage(ChatColor.DARK_AQUA + "------------------------------");
					}
				} else {
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + getIdentifier() + ChatColor.DARK_AQUA + "--------");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "/pr scheduler info " + ChatColor.GREEN + "Info about how to use actions");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "/pr scheduler list " + ChatColor.GREEN + "List all actions");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "/pr scheduler create <action_name> " + ChatColor.GREEN + "Create a new empty action");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "/pr scheduler delete <action_name> " + ChatColor.GREEN + "Delete a action");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "/pr scheduler enable <action_name> " + ChatColor.GREEN + "Enable a action");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "/pr scheduler disable <action_name> " + ChatColor.GREEN + "Disable a action");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "/pr scheduler setcondition <action_name> <condition> " + ChatColor.GREEN + "Set the condition which needs to be met before triggering");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "/pr scheduler setaction <action_name> <action> " + ChatColor.GREEN + "Set the action which will be triggered when a condition is met");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "------------------------------");
				}
			} else {
				String subcommand = arguments[1];
				if (subcommand.equalsIgnoreCase("list")) {
					if (sendAsPlayer) {
						Player player = prPlayer.getPlayer();
						if (!player.hasPermission("powerscheduler.admin")) {
							player.sendMessage(addon_prefix + ChatColor.DARK_RED + "You don't have permission to execute this command");
							return true;
						}

						player.sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + getIdentifier() + ChatColor.DARK_AQUA + "--------");
						ConfigurationSection actions = getConfig().getConfigurationSection("actions");
						if (actions != null) {
							for (String action : actions.getKeys(false)) {
								boolean action_active = getConfig().getBoolean("actions." + action + ".active");

								String action_condition = getConfig().getString("actions." + action + ".condition");

								String action_action = getConfig().getString("actions." + action + ".action");

								player.sendMessage(ChatColor.DARK_GREEN + "Action: " + ChatColor.GREEN + action);
								player.sendMessage(ChatColor.GREEN + "- " + ChatColor.DARK_GREEN + "Active: " + (action_active ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
								player.sendMessage(ChatColor.GREEN + "- " + ChatColor.DARK_GREEN + "Condition: " + ChatColor.GREEN + action_condition);
								player.sendMessage(ChatColor.GREEN + "- " + ChatColor.DARK_GREEN + "Action: " + ChatColor.GREEN + action_action);
								player.sendMessage("");

							}
						}
						player.sendMessage(ChatColor.DARK_AQUA + "------------------------------");
					} else {
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + getIdentifier() + ChatColor.DARK_AQUA + "--------");
						ConfigurationSection actions = getConfig().getConfigurationSection("actions");
						if (actions != null) {
							for (String action : actions.getKeys(false)) {
								boolean action_active = getConfig().getBoolean("actions." + action + ".active");

								String action_condition = getConfig().getString("actions." + action + ".condition");

								String action_action = getConfig().getString("actions." + action + ".action");

								Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Action: " + ChatColor.GREEN + action);
								Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- " + ChatColor.DARK_GREEN + "Active: " + (action_active ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
								Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- " + ChatColor.DARK_GREEN + "Condition: " + ChatColor.GREEN + action_condition);
								Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- " + ChatColor.DARK_GREEN + "Action: " + ChatColor.GREEN + action_action);
								Bukkit.getConsoleSender().sendMessage("");

							}
						}
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "------------------------------");
					}

				} else if (subcommand.equalsIgnoreCase("info")) {
					if (sendAsPlayer) {
						Player player = prPlayer.getPlayer();
						if (!player.hasPermission("powerscheduler.admin")) {
							player.sendMessage(addon_prefix + ChatColor.DARK_RED + "You don't have permission to execute this command");
							return true;
						}

						player.sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + getIdentifier() + ChatColor.DARK_AQUA + "--------");
						player.sendMessage(ChatColor.DARK_GREEN + "Available Conditions:");
						player.sendMessage(ChatColor.GREEN + "- PLAYER_PLAYTIME");
						player.sendMessage(ChatColor.GREEN + "- PLAYER_RANK");
						player.sendMessage(ChatColor.GREEN + "- PLAYER_BALANCE (requires Vault)");
						player.sendMessage(ChatColor.GREEN + "- PLAYER_HEALTH");
						player.sendMessage("");
						player.sendMessage(ChatColor.DARK_GREEN + "Available checks:");
						player.sendMessage(ChatColor.GREEN + "- is equal to (number)");
						player.sendMessage(ChatColor.GREEN + "- is greater than (number)");
						player.sendMessage(ChatColor.GREEN + "- is less than (number)");
//						player.sendMessage(ChatColor.GREEN + "- has changed");
						player.sendMessage("");
						player.sendMessage(ChatColor.DARK_GREEN + "Condition format: " + ChatColor.GREEN + "{condition} {check}");
						player.sendMessage(ChatColor.DARK_GREEN + "Example condition: " + ChatColor.GREEN + "PLAYER_PLAYTIME is greater than 7d 4h");
						player.sendMessage("");
						player.sendMessage(ChatColor.DARK_AQUA + "------------------------------");
						player.sendMessage("");
						player.sendMessage(ChatColor.DARK_GREEN + "Available actions:");
						player.sendMessage(ChatColor.GREEN + "- PROMOTE_RANK");
						player.sendMessage(ChatColor.GREEN + "- DEMOTE_RANK");
						player.sendMessage(ChatColor.GREEN + "- SET_RANK {RankName}");
						player.sendMessage(ChatColor.GREEN + "- SET_USERTAG {UserTag}");
						player.sendMessage(ChatColor.GREEN + "- ADD_SUBRANK {RankName}");
						player.sendMessage(ChatColor.GREEN + "- DEL_SUBRANK {RankName}");
//						player.sendMessage(ChatColor.GREEN + "- SPAWN_PARTICLES {ParticleName} {Count} {Time}");
						player.sendMessage("");
						player.sendMessage(ChatColor.DARK_GREEN + "Example action: " + ChatColor.GREEN + "SET_RANK Member");
						player.sendMessage(ChatColor.DARK_AQUA + "------------------------------");
					} else {
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + getIdentifier() + ChatColor.DARK_AQUA + "--------");
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Available Conditions:");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- PLAYER_PLAYTIME");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- PLAYER_RANK");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- PLAYER_BALANCE (requires Vault)");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- PLAYER_HEALTH");
						Bukkit.getConsoleSender().sendMessage("");
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Available checks:");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- is equal to (number)");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- is greater than (number)");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- is less than (number)");
//						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- has changed");
						Bukkit.getConsoleSender().sendMessage("");
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Condition format: " + ChatColor.GREEN + "{condition} {check}");
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Example condition: " + ChatColor.GREEN + "PLAYER_PLAYTIME is greater than 7d 4h");
						Bukkit.getConsoleSender().sendMessage("");
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "------------------------------");
						Bukkit.getConsoleSender().sendMessage("");
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Available actions:");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- PROMOTE_RANK");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- DEMOTE_RANK");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- SET_RANK {RankName}");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- SET_USERTAG {UserTag}");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- ADD_SUBRANK {RankName}");
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- DEL_SUBRANK {RankName}");
//						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "- SPAWN_PARTICLES {ParticleName} {Count} {Time}");
						Bukkit.getConsoleSender().sendMessage("");
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Example action: " + ChatColor.GREEN + "SET_RANK Member");
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "------------------------------");
					}

				} else if (subcommand.equalsIgnoreCase("create")) {
					if (arguments.length == 3) {
						String action_name = arguments[2];
						boolean action_exists = false;
						ConfigurationSection actions = getConfig().getConfigurationSection("actions");
						if (actions != null) {
							for (String action : actions.getKeys(false)) {
								if (action.equalsIgnoreCase(action_name)) {
									action_exists = true;
									break;
								}
							}
						}

						if (!action_exists) {
							getConfig().set("actions." + action_name + ".active", false);
							getConfig().set("actions." + action_name + ".condition", "NONE");
							getConfig().set("actions." + action_name + ".action", "NONE");
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								if (player.hasPermission("powerscheduler.admin")) {
									player.sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' created!");
								}
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' created!");
							}
						} else {
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								if (player.hasPermission("powerscheduler.admin")) {
									player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' already exists!");
								}
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' already exists!");
							}
						}
					} else {
						if (sendAsPlayer) {
							Player player = prPlayer.getPlayer();
							if (player.hasPermission("powerscheduler.admin")) {
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler create <action_name>");
							}
						} else {
							Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler create <action_name>");
						}
					}
				} else if (subcommand.equalsIgnoreCase("delete")) {
					if (sendAsPlayer) {
						Player player = prPlayer.getPlayer();
						if (!player.hasPermission("powerscheduler.admin")) {
							player.sendMessage(addon_prefix + ChatColor.DARK_RED + "You don't have permission to execute this command");
							return true;
						}
					}
					if (arguments.length == 3) {
						String action_name = arguments[2];
						boolean action_exists = false;
						ConfigurationSection actions = getConfig().getConfigurationSection("actions");
						if (actions != null) {
							for (String action : actions.getKeys(false)) {
								if (action.equalsIgnoreCase(action_name)) {
									action_exists = true;
									break;
								}
							}
						}

						if (action_exists) {
							getConfig().set("actions." + action_name, null);
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' deleted!");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' deleted!");
							}
						} else {
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							}
						}
					} else {
						if (sendAsPlayer) {
							Player player = prPlayer.getPlayer();
							if (player.hasPermission("powerscheduler.admin")) {
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler delete <action_name>");
							}
						} else {
							Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler delete <action_name>");
						}
					}
				} else if (subcommand.equalsIgnoreCase("enable")) {
					if (sendAsPlayer) {
						Player player = prPlayer.getPlayer();
						if (!player.hasPermission("powerscheduler.admin")) {
							player.sendMessage(addon_prefix + ChatColor.DARK_RED + "You don't have permission to execute this command");
							return true;
						}
					}
					if (arguments.length == 3) {
						String action_name = arguments[2];
						boolean action_exists = false;
						ConfigurationSection actions = getConfig().getConfigurationSection("actions");
						if (actions != null) {
							for (String action : actions.getKeys(false)) {
								if (action.equalsIgnoreCase(action_name)) {
									action_exists = true;
									break;
								}
							}
						}

						if (action_exists) {
							getConfig().set("actions." + action_name + ".active", true);
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' enabled!");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' enabled!");
							}
						} else {
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							}
						}
					} else {
						if (sendAsPlayer) {
							Player player = prPlayer.getPlayer();
							if (player.hasPermission("powerscheduler.admin")) {
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler delete <action_name>");
							}
						} else {
							Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler delete <action_name>");
						}
					}
				} else if (subcommand.equalsIgnoreCase("disable")) {
					if (sendAsPlayer) {
						Player player = prPlayer.getPlayer();
						if (!player.hasPermission("powerscheduler.admin")) {
							player.sendMessage(addon_prefix + ChatColor.DARK_RED + "You don't have permission to execute this command");
							return true;
						}
					}
					if (arguments.length == 3) {
						String action_name = arguments[2];
						boolean action_exists = false;
						ConfigurationSection actions = getConfig().getConfigurationSection("actions");
						if (actions != null) {
							for (String action : actions.getKeys(false)) {
								if (action.equalsIgnoreCase(action_name)) {
									action_exists = true;
									break;
								}
							}
						}

						if (action_exists) {
							getConfig().set("actions." + action_name + ".active", false);
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' disabled!");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' disabled!");
							}
						} else {
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							}
						}
					} else {
						if (sendAsPlayer) {
							Player player = prPlayer.getPlayer();
							if (player.hasPermission("powerscheduler.admin")) {
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler delete <action_name>");
							}
						} else {
							Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler delete <action_name>");
						}
					}

				} else if (subcommand.equalsIgnoreCase("setcondition")) {
					if (sendAsPlayer) {
						Player player = prPlayer.getPlayer();
						if (!player.hasPermission("powerscheduler.admin")) {
							player.sendMessage(addon_prefix + ChatColor.DARK_RED + "You don't have permission to execute this command");
							return true;
						}
					}
					if (arguments.length >= 4) {
						String action_name = arguments[2];
						boolean action_exists = false;
						ConfigurationSection actions = getConfig().getConfigurationSection("actions");
						if (actions != null) {
							for (String action : actions.getKeys(false)) {
								if (action.equalsIgnoreCase(action_name)) {
									action_exists = true;
									break;
								}
							}
						}

						if (action_exists) {
							String condition = "";
							for (int i = 3; i < arguments.length; i++) {
								condition += arguments[i] + " ";
							}
							condition = condition.substring(0, condition.length() - 1);
							getConfig().set("actions." + action_name + ".condition", condition);
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' changed!");
								player.sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Set the condition to: '" + condition + "'");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' changed!");
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Set the condition to: '" + condition + "'");
							}
						} else {
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							}
						}
					} else {
						if (sendAsPlayer) {
							Player player = prPlayer.getPlayer();
							if (player.hasPermission("powerscheduler.admin")) {
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler setcondition <action_name> <condition>");
							}
						} else {
							Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler setcondition <action_name> <condition>");
						}
					}

				} else if (subcommand.equalsIgnoreCase("setaction")) {
					if (sendAsPlayer) {
						Player player = prPlayer.getPlayer();
						if (!player.hasPermission("powerscheduler.admin")) {
							player.sendMessage(addon_prefix + ChatColor.DARK_RED + "You don't have permission to execute this command");
							return true;
						}
					}
					if (arguments.length >= 4) {
						String action_name = arguments[2];
						boolean action_exists = false;
						ConfigurationSection actions = getConfig().getConfigurationSection("actions");
						if (actions != null) {
							for (String action : actions.getKeys(false)) {
								if (action.equalsIgnoreCase(action_name)) {
									action_exists = true;
									break;
								}
							}
						}

						if (action_exists) {
							String action = "";
							for (int i = 3; i < arguments.length; i++) {
								action += arguments[i] + " ";
							}
							action = action.substring(0, action.length() - 1);
							getConfig().set("actions." + action_name + ".action", action);
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' changed!");
								player.sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Set the condition to: '" + action + "'");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Action '" + action_name + "' changed!");
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_GREEN + "Set the action to: '" + action + "'");
							}
						} else {
							if (sendAsPlayer) {
								Player player = prPlayer.getPlayer();
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							} else {
								Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Action '" + action_name + "' does not exists!");
							}
						}
					} else {
						if (sendAsPlayer) {
							Player player = prPlayer.getPlayer();
							if (player.hasPermission("powerscheduler.admin")) {
								player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler setaction <action_name> <action>");
							}
						} else {
							Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Usage: /pr scheduler setaction <action_name> <action>");
						}
					}

				} else {
					if (sendAsPlayer) {
						Player player = prPlayer.getPlayer();
						if (player.hasPermission("powerscheduler.admin")) {
							player.sendMessage(addon_prefix + ChatColor.DARK_RED + "Unknown command: " + subcommand);
						}
					} else {
						Bukkit.getConsoleSender().sendMessage(addon_prefix + ChatColor.DARK_RED + "Unknown command: " + subcommand);
					}
				}
			}
			return true;
		}
		return false;
	}
}