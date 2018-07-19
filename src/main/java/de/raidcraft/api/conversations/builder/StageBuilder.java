package de.raidcraft.api.conversations.builder;

import de.raidcraft.api.builder.ActionRequirementBuilder;
import de.raidcraft.api.conversations.answer.AnswerBuilder;
import de.raidcraft.api.conversations.answer.InputAnswer;
import de.raidcraft.api.conversations.answer.InputBuilder;
import de.raidcraft.api.conversations.answer.SimpleAnswer;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.fanciful.FancyMessage;

import java.util.function.Consumer;

public class StageBuilder extends ActionRequirementBuilder<StageTemplate> {

    public StageBuilder(StageTemplate item) {
        super(item);
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
        getResult().addAnswer(answerBuilder.build());
        return this;
    }

    public StageBuilder withAnswer(FancyMessage message, Consumer<AnswerBuilder> consumer) {
        AnswerBuilder answerBuilder = withBuilder(AnswerBuilder.class, new SimpleAnswer(message), consumer);
        getResult().addAnswer(answerBuilder.build());
        return this;
    }

    public StageBuilder withAnswer(FancyMessage message) {
        return withAnswer(message, null);
    }

    public StageBuilder withInput(String message) {
        return withInput(message, null);
    }

    public StageBuilder withInput(String message, Consumer<InputBuilder> consumer) {
        InputBuilder answerBuilder = withBuilder(InputBuilder.class, new InputAnswer(message), consumer);
        getResult().addAnswer(answerBuilder.build());
        return this;
    }
}
