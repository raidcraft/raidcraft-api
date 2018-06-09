package de.raidcraft.api.conversations.actions;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.conversation.Conversation;
import org.bukkit.configuration.ConfigurationSection;

public class ChangeToPreviousStageAction implements Action<Conversation> {

    @Override
    @Information(
            value = "stage.previous",
            desc = "Changes the conversation to the previous stage or exits if the stage does not exist."
    )
    public void accept(Conversation conversation, ConfigurationSection config) {
        conversation.changeToPreviousStage();
    }
}
