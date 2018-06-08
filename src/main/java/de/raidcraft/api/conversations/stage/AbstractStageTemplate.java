package de.raidcraft.api.conversations.stage;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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
    private final ConversationTemplate conversationTemplate;
    private final List<Requirement<?>> requirements = new ArrayList<>();
    private final List<Action<?>> actions = new ArrayList<>();
    private final List<Action<?>> randomActions = new ArrayList<>();
    private final List<Answer> answers = new ArrayList<>();

    @Getter
    private Optional<String[]> text;
    private boolean autoShowingAnswers = true;

    public AbstractStageTemplate(String identifier, ConversationTemplate conversationTemplate) {

        this.identifier = identifier;
        this.conversationTemplate = conversationTemplate;
    }

    @Override
    public void setText(String text) {

        if (text == null) {
            this.text = Optional.empty();
        } else {
            this.text = Optional.of(text.split("\\|"));
        }
    }

    @Override
    public void setText(String... text) {

        this.text = Optional.ofNullable(text);
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
