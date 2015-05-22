package de.raidcraft.api.conversations;

import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import mkremins.fanciful.FancyMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author mdoering
 */
public class Conversations {

    private static ConversationProvider provider;
    private static Map<String, Class<? extends Answer>> queuedAnswers = new CaseInsensitiveMap<>();
    private static Map<String, Class<? extends StageTemplate>> queuedStages = new CaseInsensitiveMap<>();
    private static Map<String, ConfigurationSection> queuedConversations = new CaseInsensitiveMap<>();

    private Conversations() {}

    public static void enable(ConversationProvider provider) {

        Conversations.provider = provider;

        queuedAnswers.entrySet().forEach(entry -> provider.registerAnswer(entry.getKey(), entry.getValue()));
        queuedAnswers.clear();
        queuedStages.entrySet().forEach(entry -> provider.registerStage(entry.getKey(), entry.getValue()));
        queuedStages.clear();
        queuedConversations.entrySet().forEach(entry -> provider.loadConversation(entry.getKey(), entry.getValue()));
        queuedConversations.clear();
    }

    public static void disable(ConversationProvider provider) {

        de.raidcraft.api.conversations.Conversations.provider = null;
    }

    /**
     * @see ConversationProvider#registerAnswer(String, Class)
     */
    public static void registerAnswer(String type, Class<? extends Answer> answer) {

        if (provider == null) {
            queuedAnswers.put(type, answer);
        } else {
            provider.registerAnswer(type, answer);
        }
    }

    /**
     * @see ConversationProvider#getAnswer(StageTemplate, ConfigurationSection)
     */
    public static Optional<Answer> getAnswer(StageTemplate stageTemplate, ConfigurationSection config) {

        if (provider == null) {
            return Optional.empty();
        }
        return provider.getAnswer(stageTemplate, config);
    }

    /**
     * @see ConversationProvider#getAnswer(String)
     */
    public static Answer getAnswer(String text) {

        return provider.getAnswer(text);
    }

    /**
     * @see ConversationProvider#getAnswer(FancyMessage)
     */
    public static Answer getAnswer(FancyMessage message) {

        return provider.getAnswer(message);
    }

    /**
     * @see ConversationProvider#registerStage(String, Class)
     */
    public static void registerStage(String type, Class<? extends StageTemplate> stage) {

        if (provider == null) {
            queuedStages.put(type, stage);
        } else {
            provider.registerStage(type, stage);
        }
    }

    /**
     * @see ConversationProvider#getStageTemplate(String, ConversationTemplate, ConfigurationSection)
     */
    public static Optional<StageTemplate> getStageTemplate(String identifier, ConversationTemplate conversationTemplate, ConfigurationSection config) {

        if (provider == null) {
            return Optional.empty();
        }
        return provider.getStageTemplate(identifier, conversationTemplate, config);
    }

    /**
     * @see ConversationProvider#loadConversation(String, ConfigurationSection)
     */
    public static void loadConversation(String name, ConfigurationSection config) {

        if (provider == null) {
            queuedConversations.put(name, config);
        } else {
            provider.loadConversation(name, config);
        }
    }

    /**
     * @see ConversationProvider#setActiveConversation(Conversation)
     */
    public static Optional<Conversation<Player>> setActiveConversation(Conversation<Player> conversation) {

        if (provider == null) return Optional.empty();
        return provider.setActiveConversation(conversation);
    }

    /**
     * @see ConversationProvider#hasActiveConversation(Player)
     */
    public static boolean hasActiveConversation(Player player) {

        if (provider == null) return false;
        return provider.hasActiveConversation(player);
    }

    /**
     * @see ConversationProvider#getActiveConversation(Player)
     */
    public static Optional<Conversation<Player>> getActiveConversation(Player player) {

        if (provider == null) return Optional.empty();
        return provider.getActiveConversation(player);
    }

    /**
     * @see ConversationProvider#removeActiveConversation(Player)
     */
    public static Optional<Conversation<Player>> removeActiveConversation(Player player) {

        if (provider == null) return Optional.empty();
        return provider.removeActiveConversation(player);
    }

    /**
     * Registers the given conversation template with the {@link ConversationProvider}. Registered
     * conversation templates allow the setting if custom variables at the beginning of a conversation.
     *  @param type name of the conversation template
     * @param conversationTemplate type to register
     */
    public static void registerConversationTemplate(String type, Class<? extends ConversationTemplate> conversationTemplate) {

        provider.registerConversationTemplate(type, conversationTemplate);
    }

    /**
     * Registers the given variable for all conversation replacements.
     * Registered variables will be replaced when the conversation text is written.
     *  @param name of the variable
     * @param variable to register
     */
    public static void registerConversationVariable(String name, ConversationVariable variable) {

        provider.registerConversationVariable(name, variable);
    }

    /**
     * Gets the given {@link ConversationTemplate} if one with the identifier is registered, otherwise an
     * empty {@link Optional} will be returned.
     * ConversationTemplates can be used to set dynamic variables at the beginning of a {@link Conversation}.
     * If no type is set the {@link ConversationTemplate#DEFAULT_CONVERSATION_TEMPLATE} will be used.
     *
     * @param identifier of the conversation template
     * @param config to create template from
     * @return optional conversation template
     */
    public static Optional<ConversationTemplate> getConversationTemplate(String identifier, ConfigurationSection config) {

        return provider.createConversationTemplate(identifier, config);
    }

    /**
     * Gets all registered conversation variables.
     *
     * @return registered variables
     */
    public static Map<String, ConversationVariable> getConversationVariables() {

        return provider.getConversationVariables();
    }

    /**
     * @see ConversationProvider#registerConversationHost(Class, Class)
     */
    public static <T> void registerConversationHost(Class<T> type, Class<? extends ConversationHost<T>> host) {

        provider.registerConversationHost(type, host);
    }

    public static <T> Optional<ConversationHost<T>> createConversationHost(T type, ConfigurationSection config) {

        return provider.createConversationHost(type, config);
    }

    public static Optional<ConversationTemplate> getLoadedConversationTemplate(String identifier) {

        return provider.getLoadedConversationTemplate(identifier);
    }

    public static List<ConversationTemplate> findConversationTemplate(String identifier) {

        return provider.findConversationTemplate(identifier);
    }

    public static <T> Optional<ConversationHost<T>> getConversationHost(T type) {

        return provider.getConversationHost(type);
    }

    public static Optional<Conversation<Player>> startConversation(Player player, ConversationHost<?> conversationHost) {

        return provider.startConversation(player, conversationHost);
    }

    public static Optional<ConversationTemplate> createConversationTemplate(String identifier, ConfigurationSection config) {

        return provider.createConversationTemplate(identifier, config);
    }
}
