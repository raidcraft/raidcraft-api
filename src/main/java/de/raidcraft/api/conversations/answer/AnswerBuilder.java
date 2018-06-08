package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.builder.ActionRequirementBuilder;
import de.raidcraft.api.conversations.builder.ConversationBuilder;
import org.bukkit.ChatColor;

public class AnswerBuilder extends ActionRequirementBuilder<ConversationBuilder, Answer> {

    public AnswerBuilder(ConversationBuilder conversationBuilder, Answer item) {
        super(conversationBuilder, item);
    }

    public AnswerBuilder withColor(ChatColor color) {
        getResult().color(color);
        return this;
    }
}
