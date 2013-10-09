package de.raidcraft.api.mobs;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public interface MobProvider {

    public void registerMob(String id, ConfigurationSection data);

    public void registerMobGroup(String id, ConfigurationSection data);

    public String getFriendlyName(String id);
}
