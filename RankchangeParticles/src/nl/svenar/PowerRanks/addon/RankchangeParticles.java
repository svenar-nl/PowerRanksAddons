package nl.svenar.PowerRanks.addon;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import nl.svenar.PowerRanks.PowerRanks;
import nl.svenar.PowerRanks.addons.PowerRanksAddon;
import nl.svenar.PowerRanks.addons.PowerRanksPlayer;

public class RankchangeParticles extends PowerRanksAddon {

	@Override
	public String getAuthor() {
		return "svenar";
	}

	@Override
	public String getIdentifier() {
		return "RankchangeParticles";
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
		setupConfigfile();
		if (!getConfig().isSet("particles.on_general_change.enabled"))
			getConfig().set("particles.on_general_change.enabled", true);

		if (!getConfig().isSet("particles.on_general_change.particle_name"))
			getConfig().set("particles.on_general_change.particle_name", "explosion_huge");

		if (!getConfig().isSet("particles.on_general_change.particle_count"))
			getConfig().set("particles.on_general_change.particle_count", 10);

		if (!getConfig().isSet("particles.on_promote.enabled"))
			getConfig().set("particles.on_promote.enabled", true);

		if (!getConfig().isSet("particles.on_promote.particle_name"))
			getConfig().set("particles.on_promote.particle_name", "explosion_huge");

		if (!getConfig().isSet("particles.on_promote.particle_count"))
			getConfig().set("particles.on_promote.particle_count", 10);

		if (!getConfig().isSet("particles.on_demote.enabled"))
			getConfig().set("particles.on_demote.enabled", true);

		if (!getConfig().isSet("particles.on_demote.particle_name"))
			getConfig().set("particles.on_demote.particle_name", "explosion_huge");

		if (!getConfig().isSet("particles.on_demote.particle_count"))
			getConfig().set("particles.on_demote.particle_count", 10);

		registerCommandAutocomplete("testparticles");
		registerCommandAutocomplete("listparticles");
		registerPermission("powerranks.cmd.addon.rankchangeparticles");
	}

	@Override
	public void onPlayerRankChange(PowerRanksPlayer prPlayer, String oldRank, String newRank, RankChangeCause cause, boolean isPlayerOnline) {
		if (isPlayerOnline) {
			showParticles(prPlayer, cause);
		}
	}

	public boolean onPowerRanksCommand(PowerRanksPlayer prPlayer, boolean sendAsPlayer, String command, String[] arguments) {
		if (sendAsPlayer) {
			Player player = prPlayer.getPlayer();
			if (command.equalsIgnoreCase("testparticles")) {
				if (player.hasPermission("powerranks.cmd.addon.rankchangeparticles")) {
					if (arguments.length == 2) {
						String action = arguments[1];
						RankChangeCause cause = null;
						try {
							cause = RankChangeCause.valueOf(action.toUpperCase());
						} catch (Exception e) {
						}

						if (cause != null) {
							showParticles(prPlayer, cause);
							player.sendMessage(ChatColor.GREEN + "Showing " + action + " particles");
						} else
							player.sendMessage(ChatColor.RED + "Usage /pr testparticles <set/promote/demote>");
					} else {
						player.sendMessage(ChatColor.RED + "Usage /pr testparticles <set/promote/demote>");
					}
				} else {
					player.sendMessage(ChatColor.DARK_RED + "You don't have permission to perform this command!");
				}
				return true;
			} else if (command.equalsIgnoreCase("listparticles")) {
				player.sendMessage(ChatColor.DARK_AQUA + "--------" + ChatColor.DARK_BLUE + PowerRanks.pdf.getName() + ChatColor.DARK_AQUA + "--------");
				player.sendMessage(ChatColor.DARK_GREEN + "Available particle names:");
				for (Particle p : Particle.values()) {
					player.sendMessage(ChatColor.GREEN + "- " + p.name());
				}
				player.sendMessage(ChatColor.DARK_AQUA + "--------------------------");
				return true;
			}
		}
		return false;
	}

	private void showParticles(PowerRanksPlayer prPlayer, RankChangeCause cause) {
		String particle_name = "";
		int particle_count = 0;
		Particle particle = null;
		switch (cause) {
		case SET:
			if (getConfig().getBoolean("particles.on_general_change.enabled")) {
				particle_name = getConfig().getString("particles.on_general_change.particle_name");
				particle_count = getConfig().getInt("particles.on_general_change.particle_count");
			}
			break;

		case PROMOTE:
			if (getConfig().getBoolean("particles.on_promote.enabled")) {
				particle_name = getConfig().getString("particles.on_promote.particle_name");
				particle_count = getConfig().getInt("particles.on_promote.particle_count");
			}
			break;

		case DEMOTE:
			if (getConfig().getBoolean("particles.on_demote.enabled")) {
				particle_name = getConfig().getString("particles.on_demote.particle_name");
				particle_count = getConfig().getInt("particles.on_demote.particle_count");
			}
			break;

		default:
			break;
		}

		if (particle_name.length() == 0)
			return;

		try {
			particle = Particle.valueOf(particle_name.toUpperCase());
		} catch (Exception e) {
			PowerRanks.log.info("[" + getIdentifier() + "] Particle with name '" + particle_name + "' not found!");
		}

		Player player = prPlayer.getPlayer();
		player.spawnParticle(particle, player.getLocation(), particle_count);
	}
}
