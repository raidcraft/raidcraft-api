package de.raidcraft.api.mobs;

import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * @author Silthus
 */
public class Mobs {

    private static MobProvider mobProvider;
    private static Map<String, ConfigurationSection> queuedMobRegistrations = new CaseInsensitiveMap<>();
    private static Map<String, ConfigurationSection> queuedMobGroupRegistrations = new CaseInsensitiveMap<>();

    public static void enable(MobProvider provider) {

        mobProvider = provider;
        // lets register all queued groups and mobs
        for (Map.Entry<String, ConfigurationSection> entry : queuedMobRegistrations.entrySet()) {
            provider.registerMob(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ConfigurationSection> entry : queuedMobGroupRegistrations.entrySet()) {
            provider.registerMobGroup(entry.getKey(), entry.getValue());
        }
    }

    public static void disable() {

        mobProvider = null;
    }

    public static void registerMob(String id, ConfigurationSection data) {

        if (mobProvider == null) {
            queuedMobRegistrations.put(id, data);
            return;
        }
        mobProvider.registerMob(id, data);
    }

    public static void registerMobGroup(String id, ConfigurationSection data) {

        if (mobProvider == null) {
            queuedMobGroupRegistrations.put(id, data);
            return;
        }
        mobProvider.registerMobGroup(id, data);
    }
}
