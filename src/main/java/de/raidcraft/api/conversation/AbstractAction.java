package de.raidcraft.api.conversation;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractAction<T> implements Action<T> {

    private final Stage<T> stage;
    private final String name;

    protected AbstractAction(Stage<T> stage, ConfigurationSection config) {

        this.stage = stage;
        this.name = config.getName();
    }

    protected abstract void load(ConfigurationSection data);

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Stage<T> getStage() {

        return stage;
    }
}
