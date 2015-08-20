package de.raidcraft.api.conversations;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.host.ConversationHostFactory;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public class Conversations {

    private static ConversationProvider provider;
    private static Map<String, Class<? extends Answer>> queuedAnswers = new CaseInsensitiveMap<>();
    private static Map<String, Class<? extends StageTemplate>> queuedStages = new CaseInsensitiveMap<>();
    private static Map<String, ConfigurationSection> queuedConversations = new CaseInsensitiveMap<>();
    private static Map<String, Class<? extends ConversationTemplate>> queuedConversationTemplates = new CaseInsensitiveMap<>();
    private static Map<String, Class<? extends Conversation>> queuedConversationTypes = new CaseInsensitiveMap<>();
    private static Map<Pattern, ConversationVariable> queuedVariables = new HashMap<>();
    private static Map<String, ConversationHostFactory<?>> queuedHostFactories = new HashMap<>();

    private Conversations() {}

    public static void enable(ConversationProvider provider) {

        Conversations.provider = provider;

        queuedAnswers.entrySet().forEach(entry -> provider.registerAnswer(entry.getKey(), entry.getValue()));
        queuedAnswers.clear();
        queuedStages.entrySet().forEach(entry -> provider.registerStage(entry.getKey(), entry.getValue()));
        queuedStages.clear();
        queuedConversationTemplates.entrySet().forEach(entry -> provider.registerConversationTemplate(entry.getKey(), entry.getValue()));
        queuedConversationTemplates.clear();
        queuedConversationTypes.entrySet().forEach(entry -> provider.registerConversationType(entry.getKey(), entry.getValue()));
        queuedConversationTypes.clear();
        queuedVariables.entrySet().forEach(entry -> provider.registerConversationVariable(entry.getKey(), entry.getValue()));
        queuedVariables.clear();
        queuedHostFactories.entrySet().forEach(entry -> provider.registerHostFactory(entry.getKey(), entry.getValue()));
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
     * @see ConversationProvider#createAnswers(StageTemplate, ConfigurationSection)
     */
    public static List<Answer> createAnswers(StageTemplate template, ConfigurationSection config) {

        if (provider == null) {
            return new ArrayList<>();
        }
        return provider.createAnswers(template, config);
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
    public static Optional<Conversation> setActiveConversation(Conversation conversation) {

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
    public static Optional<Conversation> getActiveConversation(Player player) {

        if (provider == null) return Optional.empty();
        return provider.getActiveConversation(player);
    }

    /**
     * @see ConversationProvider#removeActiveConversation(Player)
     */
    public static Optional<Conversation> removeActiveConversation(Player player) {

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

        if (provider == null) {
            queuedConversationTemplates.put(type, conversationTemplate);
        } else {
            provider.registerConversationTemplate(type, conversationTemplate);
        }
    }

    /**
     * Registers the given variable for all conversation replacements.
     * Registered variables will be replaced when the conversation text is written.
     *  @param pattern of the variable
     * @param variable to register
     */
    public static void registerConversationVariable(Pattern pattern, ConversationVariable variable) {

        if (provider == null) {
            queuedVariables.put(pattern, variable);
        } else {
            provider.registerConversationVariable(pattern, variable);
        }
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
    public static Optional<ConversationTemplate> createConversationTemplate(String identifier, ConfigurationSection config) {

        if (provider == null) return Optional.empty();
        return provider.createConversationTemplate(identifier, config);
    }

    /**
     * Gets all registered conversation variables.
     *
     * @return registered variables
     */
    public static Map<Pattern, ConversationVariable> getConversationVariables() {

        if (provider == null) return new HashMap<>();
        return provider.getConversationVariables();
    }

    /**
     * @see ConversationProvider#registerHostFactory(String, ConversationHostFactory)
     */
    public static <T> void registerHostFactory(String identifier, ConversationHostFactory<T> factory) {

        if (provider == null) {
            queuedHostFactories.put(identifier, factory);
        } else {
            provider.registerHostFactory(identifier, factory);
        }
    }

    public static Optional<ConversationTemplate> getConversationTemplate(String identifier) {

        if (provider == null) return Optional.empty();
        return provider.getLoadedConversationTemplate(identifier);
    }

    public static List<ConversationTemplate> findConversationTemplate(String identifier) {

        if (provider == null) return new ArrayList<>();
        return provider.findConversationTemplate(identifier);
    }

    public static Optional<ConversationHost<?>> getConversationHost(String id) {

        if (provider == null) return Optional.empty();
        return provider.getConversationHost(id);
    }

    public static <T> Optional<ConversationHost<T>> getConversationHost(T host) {

        if (provider == null) return Optional.empty();
        return provider.getConversationHost(host);
    }

    public static <T> Optional<ConversationHost<T>> getOrCreateConversationHost(T host, ConfigurationSection config) {

        if (provider == null) return Optional.empty();
        return provider.getOrCreateConversationHost(host, config);
    }

    public static Optional<Conversation> startConversation(Player player, ConversationHost<?> conversationHost) {

        if (provider == null) return Optional.empty();
        return provider.startConversation(player, conversationHost);
    }

    public static Optional<Conversation> startConversation(Player player, String conversation) {

        if (provider == null) return Optional.empty();
        return provider.startConversation(player, conversation);
    }

    public static Optional<ConversationHost<?>> createConversationHost(String holdingPlugin, String identifier, String type, Location location) {

        if (provider == null) return Optional.empty();
        return provider.createConversationHost(holdingPlugin, identifier, type, location);
    }

    public static Optional<ConversationHost<?>> createConversationHost(String holdingPlugin, String identifier, ConfigurationSection config) {

        if (provider == null) return Optional.empty();
        return provider.createConversationHost(holdingPlugin, identifier, config);
    }

    public static <T> Optional<ConversationHost<T>> createConversationHost(T host, ConfigurationSection config) {

        if (provider == null) return Optional.empty();
        return provider.createConversationHost(host, config);
    }

    public static Stage createStage(Conversation conversation, String text, Answer... answers) {

        return provider.createStage(conversation, text, answers);
    }

    public static Answer createAnswer(String text, Action... actions) {

        return provider.createAnswer(text, actions);
    }

    public static <T extends Answer> Optional<Answer> createAnswer(Class<T> answerClass, Action... actions) {

        return provider.createAnswer(answerClass, actions);
    }

    public static void endActiveConversation(Player player, ConversationEndReason reason) {

        if (provider == null) return;
        Optional<Conversation> activeConversation = provider.getActiveConversation(player);
        if (activeConversation.isPresent()) {
            activeConversation.get().end(reason);
        }
    }

    public static void changeStage(Player player, String stage) {

        if (provider == null) return;
        Optional<Conversation> activeConversation = provider.getActiveConversation(player);
        if (activeConversation.isPresent()) {
            Optional<Stage> stageOptional = activeConversation.get().getStage(stage);
            if (stageOptional.isPresent()) {
                stageOptional.get().changeTo();
            }
        }
    }

    public static Optional<ConversationHost<?>> createConversationHost(String plugin, ConfigurationSection config) {

        if (provider == null) return Optional.empty();
        return provider.createConversationHost(plugin, config);
    }

    public static void message(Player player, String message) {

        Optional<Conversation> activeConversation = getActiveConversation(player);
        if (activeConversation.isPresent()) {
            activeConversation.get().sendMessage(message.split("\\|"));
        }
    }

    public static void registerConversationType(String type, Class<? extends Conversation> conversation) {

        if (provider == null) {
            queuedConversationTypes.put(type, conversation);
        } else {
            provider.registerConversationType(type, conversation);
        }
    }

    public static Conversation createConversation(String type, Player player, ConversationTemplate template, ConversationHost host) {

        return provider.createConversation(type, player, template, host);
    }
}
