package nl.svenar.PowerRanks.addon;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import nl.svenar.PowerRanks.PowerRanks;
import nl.svenar.PowerRanks.addons.PowerRanksAddon;
import nl.svenar.PowerRanks.addons.PowerRanksPlayer;

public class PowerRegion extends PowerRanksAddon {

	private ArrayList<Region> regions;
	protected HashMap<Player, Region> currentPlayerRegion = new HashMap<Player, Region>();
	private String msg_prefix = "&0[&b%plugin_name%&0]&r ";

	@Override
	public String getAuthor() {
		return "svenar";
	}

	@Override
	public String getIdentifier() {
		return "PowerRegion";
	}

	@Override
	public String getVersion() {
		return "1.1";
	}

	@Override
	public String minimalPowerRanksVersion() {
		return "1.0";
	}

	@Override
	public void setup() {
		msg_prefix = msg_prefix.replace("%plugin_name%", getIdentifier());

		setupConfigfile();
		if (!getConfig().isSet("regions")) {
			getConfig().set("regions", "");
		}

		registerCommandAutocomplete("regionhelp");
		registerCommandAutocomplete("createregion");
		registerCommandAutocomplete("deleteregion");
		registerCommandAutocomplete("listregions");
		registerCommandAutocomplete("regionsetpoint1");
		registerCommandAutocomplete("regionsetpoint2");
		registerPermission("powerregion.cmd.admin");
		registerPermission("powerregion.enter.<region_name>");

		loadRegions();
	}

	@Override
	public boolean onPlayerMove(PowerRanksPlayer prPlayer) {
		Player player = prPlayer.getPlayer();
		if (inRegion(player.getLocation())) {
			Region region = getRegion(player.getLocation());
			if (!player.hasPermission("powerregion.enter." + region.name)) {
				wallThrow(player);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "You may not enter region: " + region.name);
			} else {
				if (currentPlayerRegion.get(player) != region) {
					currentPlayerRegion.put(player, region);
					if (region.show_title)
						player.sendTitle(ChatColor.GREEN + region.name, "", 5, 20, 5);
				}
			}
		} else {
			if (currentPlayerRegion.get(player) != null) {
				Region last_region = currentPlayerRegion.get(player);
				currentPlayerRegion.put(player, null);
				if (last_region.show_title)
					player.sendTitle(ChatColor.GREEN + "Wilderness", "", 5, 20, 5);
			}
		}
		return false;
	}

	@Override
	public boolean onPowerRanksCommand(PowerRanksPlayer prPlayer, boolean sendAsPlayer, String command, String[] arguments) {
		if (sendAsPlayer) {
			Player player = prPlayer.getPlayer();
			if (command.equalsIgnoreCase("createregion")) {
				if (player.hasPermission("powerregion.cmd.admin")) {
					if (arguments.length == 2) {
						String region_name = arguments[1];
						if (!getConfig().isSet("regions." + region_name.toLowerCase())) {
							getConfig().set("regions." + region_name.toLowerCase() + ".x1", 0);
							getConfig().set("regions." + region_name.toLowerCase() + ".z1", 0);
							getConfig().set("regions." + region_name.toLowerCase() + ".x2", 0);
							getConfig().set("regions." + region_name.toLowerCase() + ".z2", 0);
							getConfig().set("regions." + region_name.toLowerCase() + ".show_title", true);

							loadRegions();

							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.GREEN + "A new region with name '" + region_name + "' created!");
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.GREEN + "Use the command '/pr regionsetpoint1 " + region_name.toLowerCase()
									+ "' to set the first location point to your current location");
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.GREEN + "Use the command '/pr regionsetpoint2 " + region_name.toLowerCase()
									+ "' to set the second location point to your current location");
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.GREEN + "Player's with the permission 'powerregion.enter." + region_name.toLowerCase() + "' can enter this region");
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "A region with name '" + region_name + "' already exists!");
						}
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "Usage /pr createregion <region_name>");
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.DARK_RED + "You don't have permission to perform this command!");
				}
				return true;
			} else if (command.equalsIgnoreCase("deleteregion")) {
				if (player.hasPermission("powerregion.cmd.admin")) {
					if (arguments.length == 2) {
						String region_name = arguments[1];
						if (getConfig().isSet("regions." + region_name.toLowerCase())) {
							getConfig().set("regions." + region_name.toLowerCase(), null);

							loadRegions();

							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.GREEN + "Region '" + region_name + "' has been deleted!");
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "A region with name '" + region_name + "' not found!");
						}
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "Usage /pr deleteregion <region_name>");
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.DARK_RED + "You don't have permission to perform this command!");
				}
				return true;
			} else if (command.equalsIgnoreCase("listregions")) {
				if (player.hasPermission("powerregion.cmd.admin")) {
					if (arguments.length == 1) {
						player.sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + PowerRanks.pdf.getName() + ChatColor.DARK_AQUA + "--------");
						player.sendMessage(ChatColor.DARK_GREEN + "Regions:");
						for (Region region : regions) {
							player.sendMessage(ChatColor.DARK_GREEN + "- " + ChatColor.GREEN + region.name + ChatColor.DARK_GREEN + " (" + ChatColor.GREEN + region.x1 + ChatColor.DARK_GREEN + ", " + ChatColor.GREEN + region.z1
									+ ChatColor.DARK_GREEN + ") (" + ChatColor.GREEN + region.x2 + ChatColor.DARK_GREEN + ", " + ChatColor.GREEN + region.z2 + ChatColor.DARK_GREEN + ")");
						}
						player.sendMessage(ChatColor.DARK_AQUA + "--------------------------");
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "Usage /pr listregions");
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.DARK_RED + "You don't have permission to perform this command!");
				}
				return true;
			} else if (command.equalsIgnoreCase("regionsetpoint1")) {
				if (player.hasPermission("powerregion.cmd.admin")) {
					if (arguments.length == 2) {
						String region_name = arguments[1];
						if (getConfig().isSet("regions." + region_name.toLowerCase())) {
							getConfig().set("regions." + region_name.toLowerCase() + ".x1", player.getLocation().getBlockX());
							getConfig().set("regions." + region_name.toLowerCase() + ".z1", player.getLocation().getBlockZ());

							loadRegions();

							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.GREEN + "Set the first point on '" + region_name + "' to (" + player.getLocation().getBlockX() + ", "
									+ player.getLocation().getBlockZ() + ")");

						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "A region with name '" + region_name + "' not found!");
						}
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "Usage /pr regionsetpoint1 <region_name>");
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.DARK_RED + "You don't have permission to perform this command!");
				}
				return true;
			} else if (command.equalsIgnoreCase("regionsetpoint2")) {
				if (player.hasPermission("powerregion.cmd.admin")) {
					if (arguments.length == 2) {
						String region_name = arguments[1];
						if (getConfig().isSet("regions." + region_name.toLowerCase())) {
							getConfig().set("regions." + region_name.toLowerCase() + ".x2", player.getLocation().getBlockX());
							getConfig().set("regions." + region_name.toLowerCase() + ".z2", player.getLocation().getBlockZ());

							loadRegions();

							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.GREEN + "Set the second point on '" + region_name + "' to (" + player.getLocation().getBlockX() + ", "
									+ player.getLocation().getBlockZ() + ")");

						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "A region with name '" + region_name + "' not found!");
						}
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "Usage /pr regionsetpoint2 <region_name>");
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.DARK_RED + "You don't have permission to perform this command!");
				}
				return true;
			} else if (command.equalsIgnoreCase("regionhelp")) {
				if (player.hasPermission("powerregion.cmd.admin")) {
					if (arguments.length == 1) {
						player.sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + PowerRanks.pdf.getName() + ChatColor.DARK_AQUA + "--------");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr regionhelp" + ChatColor.GREEN + " - Show the available commands.");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr createregion <region_name>" + ChatColor.GREEN + " - Create a new empty region.");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr deleteregion <region_name>" + ChatColor.GREEN + " - Delete a region.");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr listregions" + ChatColor.GREEN + " - List all regions.");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr regionsetpoint1 <region_name>" + ChatColor.GREEN + " - Set the first location of the region to your current location.");
						player.sendMessage(ChatColor.DARK_GREEN + "/pr regionsetpoint2 <region_name>" + ChatColor.GREEN + " - Set the second location of the region to your current location.");
						player.sendMessage(ChatColor.DARK_AQUA + "--------------------------");
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.RED + "Usage /pr regionhelp");
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg_prefix) + ChatColor.DARK_RED + "You don't have permission to perform this command!");
				}
				return true;
			}
		}
		return false;
	}

	public boolean inRegion(Location location) {
		Point player_pos = new Point(location.getBlockX(), location.getBlockZ());
		for (Region region : regions) {
			Rectangle region_rect = new Rectangle(region.x1, region.z1, region.x2, region.z2);
			if (region_rect.contains(player_pos))
				return true;
		}
		return false;
	}

	public Region getRegion(Location location) {
		Region r = null;
		Point player_pos = new Point(location.getBlockX(), location.getBlockZ());
		for (Region region : regions) {
			Rectangle region_rect = new Rectangle(region.x1, region.z1, region.x2, region.z2);
			if (region_rect.contains(player_pos)) {
				r = region;
				break;
			}
		}
		return r;
	}

	public static void wallThrow(Player player) {
		Location loc = player.getLocation();
		Vector vector = loc.getDirection().multiply(-1);
		vector.setX(vector.getX() - 0.2);
		vector.setY(0);
		vector.setZ(vector.getZ() - 0.2);
		player.setVelocity(vector);
	}

	public void loadRegions() {
		regions = new ArrayList<Region>();

		if (getConfig().getConfigurationSection("regions") != null) {
			try {
				for (String cs : getConfig().getConfigurationSection("regions").getKeys(false)) {
					Region region = new Region();
					region.x1 = getConfig().getInt("regions." + cs + ".x1");
					region.z1 = getConfig().getInt("regions." + cs + ".z1");
					region.x2 = getConfig().getInt("regions." + cs + ".x2");
					region.z2 = getConfig().getInt("regions." + cs + ".z2");
					region.show_title = getConfig().getBoolean("regions." + cs + ".show_title");
					region.name = cs;
					regions.add(region);
				}
			} catch (Exception e) {
			}
		}
	}
}

