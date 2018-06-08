package de.raidcraft.api.conversations.builder;

import de.raidcraft.api.conversations.conversation.AbstractConversationTemplate;
import org.bukkit.configuration.ConfigurationSection;

public class CodedConversationTemplate extends AbstractConversationTemplate {

    public CodedConversationTemplate(String identifier) {
        super(identifier);
    }

    @Override
    public void loadConfig(ConfigurationSection config) {
    }

    public CodedStageTemplate stage(String identifier) {
        return addStage(new CodedStageTemplate(identifier, this));
    }
}
