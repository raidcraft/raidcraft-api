package de.raidcraft.api.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class ConversationManager {

    private static ConversationManager manager;

    public static ConversationManager getInstance() {

        if (manager == null) {
            manager = new ConversationManager(RaidCraft.getComponent(RaidCraftPlugin.class));
        }
        return manager;
    }

    private final RaidCraftPlugin plugin;
    private final Map<String, Class<? extends Action>> actions = new HashMap<>();
    private final Map<Class<? extends Action>, Constructor<? extends Action>> constructors = new HashMap<>();
    private final Map<String, Conversation<?>> conversations = new HashMap<>();

    public ConversationManager(RaidCraftPlugin plugin) {

        manager = this;
        this.plugin = plugin;
        load();
    }

    private void load() {

        File dir = new File(getPlugin().getDataFolder(), "conversations");
        dir.mkdirs();

        for (String file : dir.list()) {
            if (file.endsWith(".yml")) {
                Conversation<?> conversation = new ConfigurableConversation<>(file.replace(".yml", ""));
                conversations.put(conversation.getName(), conversation);
            }
        }
    }

    public RaidCraftPlugin getPlugin() {

        return plugin;
    }

    public void registerAction(Class<? extends Action> actionClass) {

        if (actionClass.isAnnotationPresent(ActionInformation.class)) {
            try {
                actions.put(StringUtils.formatName(actionClass.getAnnotation(ActionInformation.class).value()), actionClass);
                Constructor<? extends Action> constructor = actionClass.getConstructor(Stage.class, ConfigurationSection.class);
                constructor.setAccessible(true);
                constructors.put(actionClass, constructor);
            } catch (NoSuchMethodException e) {
                plugin.getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().warning("Error registering action cass! Did not find action information on " + actionClass.getCanonicalName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Player> RunningConversation<T> createConversation(String name, T partner) throws ConversationException {

        if (conversations.containsKey(name)) {
            return (RunningConversation<T>) new PlayerConversation(conversations.get(name), partner);
        }
        throw new ConversationException("Es gibt keine Unterhaltung mit dem Namen " + name);
    }

    protected <T> Action<T> createAction(String name, Stage<T> stage, ConfigurationSection config) {

        return createAction(actions.get(StringUtils.formatName(name)), stage, config);
    }

    @SuppressWarnings("unchecked")
    protected <T> Action<T> createAction(Class<? extends Action> aClass, Stage<T> stage, ConfigurationSection config) {

        try {
            Constructor<? extends Action> constructor = constructors.get(aClass);
            constructor.setAccessible(true);
            Action<T> action = constructor.newInstance(stage, config);
            if (action instanceof AbstractAction) {
                ((AbstractAction) action).load(config);
            }
            return action;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
