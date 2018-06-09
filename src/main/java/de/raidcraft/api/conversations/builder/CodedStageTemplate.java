package de.raidcraft.api.conversations.builder;

import de.raidcraft.api.conversations.stage.AbstractStageTemplate;
import org.bukkit.configuration.ConfigurationSection;

public class CodedStageTemplate extends AbstractStageTemplate {

    public CodedStageTemplate(String identifier) {
        super(identifier);
    }

    @Override
    public void loadConfig(ConfigurationSection config) {
    }
}
