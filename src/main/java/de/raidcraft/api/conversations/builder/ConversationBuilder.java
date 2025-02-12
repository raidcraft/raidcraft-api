package de.raidcraft.api.conversations.builder;

import de.raidcraft.api.builder.BaseBuilder;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.stage.StageTemplate;

import java.util.function.Consumer;

public class ConversationBuilder extends BaseBuilder<ConversationTemplate> {

    public ConversationBuilder(String identifier) {

        super(new CodedConversationTemplate(identifier));
    }

    public ConversationBuilder startStage() {
        return startStage(null);
    }

    public ConversationBuilder startStage(Consumer<StageBuilder> builder) {
        return withStage(StageTemplate.START_STAGE, builder);
    }

    public ConversationBuilder withStage(String identifier, Consumer<StageBuilder> builder) {

        StageBuilder stageBuilder = withBuilder(StageBuilder.class, new CodedStageTemplate(identifier), builder);
        StageTemplate stageTemplate = stageBuilder.build();
        stageTemplate.setConversationTemplate(getResult());
        getResult().addStage(stageTemplate);
        return this;
    }

    public ConversationBuilder withStage(String identifier) {
        return withStage(identifier, null);
    }

    public ConversationBuilder withConversationEndCallback(Consumer<Conversation> callback) {
        getResult().setConversationEndCallback(callback);
        return this;
    }
}