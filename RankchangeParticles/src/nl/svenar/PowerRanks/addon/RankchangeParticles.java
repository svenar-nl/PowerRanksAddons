package nl.svenar.PowerRanks.addon;

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
		return "1.0";
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
	}

	@Override
	public void onPlayerRankChange(PowerRanksPlayer prPlayer, String oldRank, String newRank, RankChangeCause cause, boolean isPlayerOnline) {
		if (isPlayerOnline) {
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
}