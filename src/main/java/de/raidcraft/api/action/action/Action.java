package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author Silthus
 */
@FunctionalInterface
public interface Action<T> extends ActionConfigGenerator {

    static Action<?> ofMethod(Object object, String methodName, Object... args) {

        return (type, config) -> {
            try {
                Method method = ReflectionUtil.getMethod(object, methodName, args);
                method.invoke(args);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        };
    }

    static <T extends Action<?>> T of(Class<T> actionClass) {

        for (Method method : actionClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Information.class)) {
                return ActionAPI.createAction(actionClass);
            }
        }
        throw new UnsupportedOperationException("Action " + actionClass.getCanonicalName() + " has no @Information tag!");
    }

    static Action<?> endConversation(ConversationEndReason reason) {

        return new Action<Player>() {
            @Override
            public void accept(Player player, ConfigurationSection config) {

                Conversations.endActiveConversation(player, reason);
            }
        };
    }

    static Action<?> changeStage(String stage) {

        return new Action<Conversation<Player>>() {
            @Override
            public void accept(Conversation<Player> conversation, ConfigurationSection config) {

                Optional<StageTemplate> stageTemplate = Conversations.getStageTemplate(stage, conversation.getTemplate(), config);
                if (stageTemplate.isPresent()) {
                    conversation.changeToStage(stageTemplate.get().create(conversation));
                }
            }
        };
    }

    static Action<?> changeStage(Stage stage) {

        return new Action<Conversation<Player>>() {
            @Override
            public void accept(Conversation<Player> conversation, ConfigurationSection config) {

                conversation.changeToStage(stage);
            }
        };
    }

    static Action<?> setConversationVariable(String key, Object value) {

        return new Action<Conversation<Player>>() {
            @Override
            public void accept(Conversation<Player> conversation, ConfigurationSection config) {

                conversation.set(key, value);
            }
        };
    }

    default String getIdentifier() {

        return ActionAPI.getIdentifier(this);
    }

    default void addRequirement(Requirement<?> requirement) {

        throw new UnsupportedOperationException();
    }

    void accept(T type, ConfigurationSection config);

    default void accept(T type) {

        accept(type, new MemoryConfiguration());
    }

    default Action<T> with(String key, Object value) {

        throw new UnsupportedOperationException();
    }

    default Action<T> withArgs(String key, Object value) {

        throw new UnsupportedOperationException();
    }
}
