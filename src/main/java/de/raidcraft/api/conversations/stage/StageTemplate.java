package de.raidcraft.api.conversations.stage;

import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.action.requirement.RequirementHolder;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;

import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface StageTemplate extends RequirementHolder, ActionHolder {

    String START_STAGE = "start";
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
     * Gets the text that is displayed for this stage.
     * If no text is display the optional will be empty.
     *
     * @return display text
     */
    Optional<String[]> getText();

    /**
     * Gets a list of ordered answers for this stage template.
     *
     * @return ordered list of answers
     */
    List<Answer> getAnswers();

    Stage create(Conversation conversation);
}
