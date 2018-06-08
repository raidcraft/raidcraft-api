package de.raidcraft.api.conversations.builder;

import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.stage.AbstractStageTemplate;
import org.bukkit.configuration.ConfigurationSection;

public class CodedStageTemplate extends AbstractStageTemplate {

    public CodedStageTemplate(String identifier, ConversationTemplate conversationTemplate) {
        super(identifier, conversationTemplate);
    }

    @Override
    public void loadConfig(ConfigurationSection config) {
    }
}
