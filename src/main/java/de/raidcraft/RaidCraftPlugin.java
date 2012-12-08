package de.raidcraft;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.commands.ConfirmCommand;

/**
 * @author Silthus
 */
public class RaidCraftPlugin extends BasePlugin implements Component {

    private static RaidCraftPlugin instance;

    public static RaidCraftPlugin getInstance() {

        return instance;
    }

    public RaidCraftPlugin() {

        instance = this;
    }

    @Override
    public void enable() {

        registerCommands(ConfirmCommand.class);
        RaidCraft.registerComponent(RaidCraftPlugin.class, this);
    }

    @Override
    public void disable() {

        // this is just used as a dummy plugin to setup the api in the BasePlugin class
    }
}
