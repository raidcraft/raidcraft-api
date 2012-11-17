package de.raidcraft;

import de.raidcraft.api.BasePlugin;

/**
 * @author Silthus
 */
public class RaidCraftPlugin extends BasePlugin {

    private static RaidCraftPlugin instance;

    public static RaidCraftPlugin getInstance() {

        return instance;
    }

    public RaidCraftPlugin() {

        instance = this;
    }

    @Override
    public void enable() {

        // this is just used as a dummy plugin to setup the api in the BasePlugin class
    }

    @Override
    public void disable() {

        // this is just used as a dummy plugin to setup the api in the BasePlugin class
    }
}
