package nl.svenar.PowerRanks.addon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.svenar.PowerRanks.PowerRanks;
import nl.svenar.PowerRanks.Data.Users;
import nl.svenar.PowerRanks.addons.PowerRanksAddon;
import nl.svenar.PowerRanks.addons.PowerRanksPlayer;

public class PowerBroadcast extends PowerRanksAddon {

	private String msg_prefix = "&0[&b%plugin_name%&0]&r ";

	@Override
	public String getAuthor() {
		return "svenar";
	}

	@Override
	public String getIdentifier() {
		return "PowerBroadcast";
	}

	@Override
	public String getVersion() {
		return "1.1";
	}

	@Override
	public String minimalPowerRanksVersion() {
		return "1.6";
	}

	@Override
	public void setup() {
		msg_prefix = msg_prefix.replace("%plugin_name%", getIdentifier());
		setupConfigfile();
		
		if (!getConfig().isSet("broadcast.join"))
			getConfig().set("broadcast.join", "&8[&2+&8] %prefix% &7%player% %suffix% has joined the server.");
		
		if (!getConfig().isSet("broadcast.leave"))
			getConfig().set("broadcast.leave", "&8[&4-&8] %prefix% &7%player% %suffix% has left the server.");
		
		registerCommandAutocomplete("broadcast");
		registerPermission("powerranks.cmd.addon.broadcast.admin");
	}

	@Override
	public void onPlayerJoin(PowerRanksPlayer prPlayer) {
		Player player = prPlayer.getPlayer();
		
		String output_message = getConfig().getString("broadcast.join");
		output_message = output_message.replace("%player%", player.getDisplayName());
		output_message = output_message.replace("%rank%", prPlayer.getRank());
		output_message = output_message.replace("%prefix%", new Users(prPlayer.getPowerRanks()).getPrefix(prPlayer.getRank()));
		output_message = output_message.replace("%suffix%", new Users(prPlayer.getPowerRanks()).getSuffix(prPlayer.getRank()));
		output_message = PowerRanks.chatColor(output_message, true);
		Bukkit.broadcastMessage(output_message);
	}

	@Override
	public void onPlayerLeave(PowerRanksPlayer prPlayer) {
		Player player = prPlayer.getPlayer();
		
		String output_message = getConfig().getString("broadcast.leave");
		output_message = output_message.replace("%player%", player.getDisplayName());
		output_message = output_message.replace("%rank%", prPlayer.getRank());
		output_message = output_message.replace("%prefix%", new Users(prPlayer.getPowerRanks()).getPrefix(prPlayer.getRank()));
		output_message = output_message.replace("%suffix%", new Users(prPlayer.getPowerRanks()).getSuffix(prPlayer.getRank()));
		output_message = PowerRanks.chatColor(output_message, true);
		Bukkit.broadcastMessage(output_message);
	}

	@Override
	public boolean onPowerRanksCommand(PowerRanksPlayer prPlayer, boolean sendAsPlayer, String command, String[] arguments) {
		if (sendAsPlayer) {
			Player player = prPlayer.getPlayer();
			if (command.equalsIgnoreCase("broadcast")) {
				if (player.hasPermission("powerranks.cmd.addon.broadcast.admin")) {
					player.sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + getIdentifier() + ChatColor.DARK_AQUA + "--------");
					player.sendMessage(ChatColor.GREEN + getIdentifier() + " can be configured from the config file of this addon.");
					player.sendMessage(ChatColor.DARK_GREEN + "/plugins/PowerRanks/Addons/" + getIdentifier() + ".yml");
					player.sendMessage(ChatColor.DARK_AQUA + "--------------------------");
				} else {
					player.sendMessage(ChatColor.DARK_RED + "You don't have permission to perform this command!");
				}
				return true;
			}
		}
		return false;
	}
}
