package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.ActionConfigWrapper;
import de.raidcraft.api.action.GlobalAction;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.util.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    static Action<Player> endConversation(ConversationEndReason reason) {

        return ActionConfigWrapper.of((player, config) -> Conversations.endActiveConversation(player, reason), Player.class);
    }

    static Action<Conversation> changeStage(String stage) {

        return ActionConfigWrapper.of((conversation, config) -> conversation.getStage(stage).ifPresent(Stage::changeTo), Conversation.class);
    }

    static Action<Conversation> changeStage(Stage stage) {

        return ActionConfigWrapper.of((conversation, config) -> conversation.changeToStage(stage), Conversation.class);
    }

    static Action<Conversation> setConversationVariable(String key, Object value) {

        return ActionConfigWrapper.of((conversation, config) -> conversation.set(key, value), Conversation.class);
    }

    static Action<Player> text(String text) {

        Action<Player> action = GlobalAction.TEXT.getAction();
        action.withArgs("withText", text);
        return action;
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

    default Action<T> withPlayer(Player player) {

        throw new UnsupportedOperationException();
    }
}
