package de.raidcraft.api.conversations.stage;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = false, of = {"identifier", "conversationTemplate"})
public abstract class AbstractStageTemplate implements StageTemplate {

    private final String identifier;
    private final List<Requirement<?>> requirements = new ArrayList<>();
    private final List<Action<?>> actions = new ArrayList<>();
    private final List<Action<?>> randomActions = new ArrayList<>();
    private final List<Answer> answers = new ArrayList<>();

    private String[] text = null;
    private boolean autoShowingAnswers = true;
    private ConversationTemplate conversationTemplate = null;

    public AbstractStageTemplate(String identifier) {

        this.identifier = identifier;
    }

    public Optional<String[]> getText() {
        return Optional.ofNullable(text);
    }

    public Optional<ConversationTemplate> getConversationTemplate() {
        return Optional.ofNullable(conversationTemplate);
    }

    @Override
    public void setConversationTemplate(ConversationTemplate template) {
        this.conversationTemplate = template;
    }

    @Override
    public void setText(String text) {

        if (text == null) {
            this.text = null;
        } else {
            this.text = text.split("\\|");
        }
    }

    @Override
    public void setText(String... text) {

        this.text = text;
    }

    @Override
    public Stage create(Conversation conversation) {

        return new SimpleStage(conversation, this);
    }

    @Override
    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }
}
