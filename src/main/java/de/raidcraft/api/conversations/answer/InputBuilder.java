package de.raidcraft.api.conversations.answer;

import java.util.function.Consumer;

public class InputBuilder extends AnswerBuilder {

    public InputBuilder(Answer item) {
        super(item);
    }

    public InputBuilder withInputListener(Consumer<String> listener) {
        if (getResult() instanceof InputAnswer) {
            ((InputAnswer) getResult()).setInputListener(listener);
        }
        return this;
    }
}
