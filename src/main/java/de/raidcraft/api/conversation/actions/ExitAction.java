package de.raidcraft.api.conversation.actions;

import de.raidcraft.api.conversation.AbstractAction;
import de.raidcraft.api.conversation.RunningConversation;
import de.raidcraft.api.conversation.Stage;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class ExitAction<T> extends AbstractAction<T> {

    protected ExitAction(Stage<T> stage, ConfigurationSection config) {

        super(stage, config);
    }

    @Override
    protected void load(ConfigurationSection data) {

    }

    @Override
    public void execute(RunningConversation<T> conversation) {

        conversation.end(getStage());
    }
}
