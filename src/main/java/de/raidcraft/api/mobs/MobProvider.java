package de.raidcraft.api.mobs;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@Deprecated
public interface MobProvider {

    void registerMob(String id, ConfigurationSection data);

    void registerMobGroup(String id, ConfigurationSection data);

    String getFriendlyName(String id);
}
