package de.raidcraft.api.conversations.stage;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.action.requirement.RequirementHolder;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface StageTemplate extends RequirementHolder, ActionHolder {

    String START_STAGE = "startStage";
    String DEFAULT_STAGE_TEMPLATE = "default";
    int MAX_ANSWERS = 5;

    /**
     * Gets the unique identifier/name of this stage.
     *
     * @return stage name/identifier
     */
    String getIdentifier();

    /**
     * Gets the given {@link ConversationTemplate} of this stage.
     *
     * @return conversation template that loaded this stage
     */
    ConversationTemplate getConversationTemplate();

    /**
     * Gets the withText that is displayed for this stage.
     * If no withText is display the optional will be empty.
     *
     * @return display withText
     */
    Optional<String[]> getText();

    /**
     * Gets a list of ordered answers for this stage template.
     *
     * @return ordered list of answers
     */
    List<Answer> getAnswers();

    /**
     * Gets a list of random actions to execute. Random actions can be used to display a
     * randomized withText to the player when the stage is executed.
     *
     * @return list of random actions
     */
    List<Action<?>> getRandomActions();

    /**
     * Whether or not this stage will auto display its answers. If answers are not
     * automatically shown, the withAction answers.show must be called.
     *
     * @return true if answers are shown automatically
     */
    boolean isAutoShowingAnswers();

    void loadConfig(ConfigurationSection config);

    void setText(String text);

    void setText(String... text);

    Stage create(Conversation conversation);

    void addAnswer(Answer answer);
}
