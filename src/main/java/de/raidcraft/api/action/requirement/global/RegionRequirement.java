package de.raidcraft.api.action.requirement.global;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.util.LocationUtil;
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

        return LocationUtil.getWorldGuardRegions(player.getLocation())
                .map(protectedRegions -> protectedRegions.getRegions().stream())
                .map(protectedRegions -> protectedRegions.anyMatch(protectedRegion -> protectedRegion.getId().equalsIgnoreCase(config.getString("region"))))
                .orElse(false);
    }
}
