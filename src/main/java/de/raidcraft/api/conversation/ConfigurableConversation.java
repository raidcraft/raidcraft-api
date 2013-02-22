package de.raidcraft.api.conversation;

import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.util.StringUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class ConfigurableConversation<T> extends ConfigurationBase<RaidCraftPlugin> implements Conversation<T> {

    private final String name;
    private final Map<String, Stage<T>> stages = new HashMap<>();

    public ConfigurableConversation(String name) {

        super(ConversationManager.getInstance().getPlugin(),
                new File(ConversationManager.getInstance().getPlugin().getDataFolder(), StringUtils.formatName(name) + ".yml"));
        this.name = StringUtils.formatName(name);
        for (String key : getKeys(false)) {
            stages.put(key, new ConfigurableStage<>(this, getConfigurationSection(key)));
        }
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Collection<Stage<T>> getStages() {

        return stages.values();
    }

    @Override
    public Stage<T> getStage(String name) {

        return stages.get(StringUtils.formatName(name));
    }

    @Override
    public Stage<T> getStartStage() {

        return stages.get("start");
    }

    @Override
    @SuppressWarnings("unchecked")
    public RunningConversation<T> start(T player) {

        // define more conversations here
        if (player instanceof Player) {
            return (RunningConversation<T>) new PlayerConversation(this, (Player) player);
        }
        return null;
    }
}
