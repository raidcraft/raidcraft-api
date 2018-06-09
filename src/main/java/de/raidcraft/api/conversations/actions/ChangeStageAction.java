package de.raidcraft.api.conversations.actions;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author mdoering
 */
@RequiredArgsConstructor
public class ChangeStageAction implements Action<Conversation> {

    public String stage;

    @Override
    @Information(
            value = "stage",
            desc = "Changes the stage of the conversation.",
            conf = "stage: <identifier>"
    )
    @SuppressWarnings("unchecked")
    public void accept(Conversation conversation, ConfigurationSection config) {

        Optional<Stage> stage = conversation.getStage(config.getString("stage"));
        if (stage.isPresent()) {
            conversation.changeToStage(stage.get());
        }

    }
}
