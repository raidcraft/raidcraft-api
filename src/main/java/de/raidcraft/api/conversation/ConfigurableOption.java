package de.raidcraft.api.conversation;

import de.raidcraft.RaidCraft;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class ConfigurableOption<T> implements Option<T> {

    private final Stage stage;
    private int index;
    private String text;
    private List<String> aliases = new ArrayList<>();
    private List<Action<T>> actions = new ArrayList<>();

    public ConfigurableOption(Stage<T> stage, ConfigurationSection config) {

        this.stage = stage;
        load(config);
    }

    private void load(ConfigurationSection config) {

        try {
            this.index = Integer.parseInt(config.getName());
            this.text = config.getString("text", "Option " + index);
            this.aliases.addAll(config.getStringList("aliases"));
            ConversationManager manager = ConversationManager.getInstance();
            ConfigurationSection section = config.getConfigurationSection("action");
            if (section == null || section.getKeys(false) == null) return;
            for (String key : section.getKeys(false)) {
                Action<T> action = manager.createAction(key, getStage(), section.getConfigurationSection(key));
                actions.add(action);
            }
        } catch (NumberFormatException e) {
            RaidCraft.LOGGER.warning("Wrong option format in stage: " + stage.getName() + " for " + config.getName());
        }
    }

    @Override
    public Stage<T> getStage() {

        return stage;
    }

    @Override
    public int getIndex() {

        return index;
    }

    @Override
    public String getText() {

        return text;
    }

    @Override
    public List<String> getAliases() {

        return aliases;
    }

    @Override
    public List<Action<T>> getActions() {

        return actions;
    }

    @Override
    public void choose(RunningConversation<T> conversation) {

        for (Action<T> action : getActions()) {
            action.execute(conversation);
        }
    }
}
