package de.raidcraft.api.conversations.builder;

import de.raidcraft.api.builder.ActionRequirementBuilder;
import de.raidcraft.api.conversations.answer.AnswerBuilder;
import de.raidcraft.api.conversations.answer.SimpleAnswer;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.fanciful.FancyMessage;

import java.util.function.Consumer;

public class StageBuilder extends ActionRequirementBuilder<ConversationBuilder, StageTemplate> {

    public StageBuilder(ConversationBuilder conversationBuilder, StageTemplate item) {
        super(conversationBuilder, item);
    }

    public StageBuilder withText(String... text) {

        getResult().setText(text);

        return this;
    }

    public StageBuilder withAnswer(String message) {
        return withAnswer(message, null);
    }

    public StageBuilder withAnswer(String answer, Consumer<AnswerBuilder> consumer) {

        AnswerBuilder answerBuilder = withBuilder(AnswerBuilder.class, new SimpleAnswer(answer), consumer);
        answerBuilder.build(result -> getResult().addAnswer(result));

        return this;
    }

    public StageBuilder withAnswer(FancyMessage message, Consumer<AnswerBuilder> consumer) {
        AnswerBuilder answerBuilder = withBuilder(AnswerBuilder.class, new SimpleAnswer(message));
        answerBuilder.build(result -> getResult().addAnswer(result));

        return this;
    }

    public StageBuilder withAnswer(FancyMessage message) {
        return withAnswer(message, null);
    }
}
