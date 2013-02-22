package de.raidcraft.api.conversation;

import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class ConfigurableStage<T> implements Stage<T> {

    private final String name;
    private final Conversation conversation;
    private final List<Action<T>> actions = new ArrayList<>();
    private final List<Option<T>> options = new ArrayList<>();

    public ConfigurableStage(Conversation<T> conversation, ConfigurationSection config) {

        this.name = StringUtils.formatName(config.getName());
        this.conversation = conversation;
        load(config);
    }

    private void load(ConfigurationSection config) {

        ConversationManager manager = ConversationManager.getInstance();
        ConfigurationSection section = config.getConfigurationSection("actions");
        if (section != null && section.getKeys(false) != null) {
            for (String actionKey : section.getKeys(false)) {
                actions.add(manager.createAction(StringUtils.formatName(actionKey), this, section.getConfigurationSection(actionKey)));
            }
        }
        section = config.getConfigurationSection("options");
        if (section != null && section.getKeys(false) != null) {
            for (String optionKey : section.getKeys(false)) {
                options.add(new ConfigurableOption(this, section.getConfigurationSection(optionKey)));
            }
        }
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Conversation getConversation() {

        return conversation;
    }

    @Override
    public List<Action<T>> getActions() {

        return actions;
    }

    @Override
    public List<Option<T>> getOptions() {

        return options;
    }

    @Override
    public Option<T> chooseOption(int index) throws InvalidChoiceException {

        if (index < options.size()) {
            return options.get(index);
        }
        throw new InvalidChoiceException("Es gibt keine Option mit der ID " + index);
    }

    @Override
    public Option<T> chooseOption(String alias) throws InvalidChoiceException {

        alias = StringUtils.formatName(alias);
        try {
            int index = Integer.parseInt(alias);
            return chooseOption(index);
        } catch (NumberFormatException e) {
            for (Option<T> option : options) {
                if (option.getAliases().contains(alias)) {
                    return option;
                }
            }
        }
        throw new InvalidChoiceException("Es gibt keine Option die " + alias + " heisst.");
    }
}
