package de.raidcraft;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.commands.ConfirmCommand;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

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

        registerEvents(new RaidCraft());
        registerCommands(ConfirmCommand.class);
        RaidCraft.registerComponent(RaidCraftPlugin.class, this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(PlayerPlacedBlock.class);
        return classes;
    }

    @Override
    public void disable() {

    }
}
