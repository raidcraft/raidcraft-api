package de.raidcraft.api.conversations.builder;

import de.raidcraft.api.builder.BaseBuilder;
import de.raidcraft.api.conversations.ConversationProvider;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.stage.StageTemplate;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ConversationBuilder extends BaseBuilder<ConversationProvider, ConversationTemplate> {

    public ConversationBuilder(ConversationProvider conversationProvider, String identifier) {

        super(conversationProvider, new CodedConversationTemplate(identifier));
    }

    public ConversationBuilder startStage() {
        return startStage(null);
    }

    public ConversationBuilder startStage(Consumer<StageBuilder> builder) {
        return withStage(StageTemplate.START_STAGE, builder);
    }

    public ConversationBuilder withStage(String identifier, Consumer<StageBuilder> builder) {

        StageBuilder stageBuilder = withBuilder(StageBuilder.class, new CodedStageTemplate(identifier, getResult()), builder);
        stageBuilder.build(result -> getResult().addStage(result));

        return this;
    }

    public ConversationBuilder withStage(String identifier) {
        return withStage(identifier, null);
    }

    public ConversationTemplate registerTemplate() {

        return getParent().registerConversationTemplate(this.build());
    }

    public Conversation startConversation(Player player) {
        return getParent().startConversation(player, this.build());
    }
}