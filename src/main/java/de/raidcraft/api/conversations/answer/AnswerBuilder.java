package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.builder.ActionRequirementBuilder;
import org.bukkit.ChatColor;

public class AnswerBuilder extends ActionRequirementBuilder<Answer> {

    public AnswerBuilder(Answer item) {
        super(item);
    }

    public AnswerBuilder withColor(ChatColor color) {
        getResult().color(color);
        return this;
    }
}
