package de.raidcraft.api.action.requirement.global;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RegionRequirement implements Requirement<Player> {

    @Information(
            value = "region",
            aliases = {"worldguard.region"},
            desc = "Checks if the player is in the given region.",
            conf = {
                    "region: the world guard region to check"
            }
    )
    @Override
    public boolean test(Player player, ConfigurationSection config) {

        if (RaidCraft.getWorldGuard() == null) return true;

        RegionManager regionManager = RaidCraft.getWorldGuard().getRegionManager(player.getWorld());
        return regionManager.getApplicableRegions(player.getLocation()).getRegions().stream()
                .map(ProtectedRegion::getId)
                .anyMatch(id -> id.equalsIgnoreCase(config.getString("region")));
    }
}
