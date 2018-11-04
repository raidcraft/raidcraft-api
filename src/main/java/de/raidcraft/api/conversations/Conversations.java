package de.raidcraft.api.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.builder.CodedStageTemplate;
import de.raidcraft.api.conversations.builder.ConversationBuilder;
import de.raidcraft.api.conversations.builder.StageBuilder;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.host.ConversationHostFactory;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
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
        queuedStages.entrySet().forEach(entry -> provider.registerStageTemplate(entry.getKey(), entry.getValue()));
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
     * Creates a new {@link ConversationBuilder} with a random {@link UUID}.
     *
     * @return the {@link ConversationBuilder}
     */
    public static ConversationBuilder create() {
        return create(UUID.randomUUID().toString());
    }

    public static ConversationBuilder create(String identifier) {
        return new ConversationBuilder(identifier);
    }

    public static StageBuilder buildStage(String stageName) {
        return new StageBuilder(new CodedStageTemplate(stageName));
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
     * @see ConversationProvider#registerStageTemplate(String, Class)
     */
    public static void registerStage(String type, Class<? extends StageTemplate> stage) {

        if (provider == null) {
            queuedStages.put(type, stage);
        } else {
            provider.registerStageTemplate(type, stage);
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
     *  @param type displayName of the conversation template
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
     * Registered variables will be replaced when the conversation withText is written.
     * @param pattern of the variable
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
     * Registers the given raw string as a pattern for conversation variables.
     *
     * @param pattern  to register
     * @param variable to resolve
     */
    public static void registerConversationVariable(String pattern, ConversationVariable variable) {
        registerConversationVariable(Pattern.compile(pattern), variable);
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

    public static <TConversation extends Conversation> Optional<TConversation> startConversation(Player player, String conversation, Class<TConversation> conversationClass) {
        if (provider == null) return Optional.empty();
        return provider.startConversation(player, conversation, conversationClass);
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

    public static void removeConversationHost(String holder, String id) {
        if (provider == null) return;
        provider.removeConversationHost(holder, id);
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
        activeConversation.ifPresent(conversation -> conversation.end(reason));
    }

    public static void changeStage(Player player, String stage) {

        if (provider == null) return;
        Optional<Conversation> activeConversation = provider.getActiveConversation(player);
        if (activeConversation.isPresent()) {
            Optional<Stage> stageOptional = activeConversation.get().getStage(stage);
            stageOptional.ifPresent(Stage::changeTo);
        }
    }

    public static Optional<ConversationHost<?>> createConversationHost(String plugin, ConfigurationSection config) {

        if (provider == null) return Optional.empty();
        return provider.createConversationHost(plugin, config);
    }

    public static void message(Player player, String message) {

        Optional<Conversation> activeConversation = getActiveConversation(player);
        activeConversation.ifPresent(conversation -> conversation.sendMessage(message.split("\\|")));
    }

    /**
     * Sends an error message to the conversation and console
     * and then ends the conversation.
     *
     * @param conv  to send error to and abort
     * @param error to send
     */
    public static void error(Conversation conv, String error) {

        conv.sendMessage(ChatColor.RED + error);
        conv.abort(ConversationEndReason.ERROR);
        RaidCraft.LOGGER.warning(error);
    }

    public static void registerConversationType(String type, Class<? extends Conversation> conversation) {

        if (provider == null) {
            queuedConversationTypes.put(type, conversation);
        } else {
            provider.registerConversationType(type, conversation);
        }
    }

    public static Conversation createConversation(String type, Player player, ConversationTemplate template, ConversationHost host) {

        return provider.startConversation(type, player, template, host);
    }

    public static Optional<ConversationHost<?>> spawnConversationHost(String pluginName, String name, String conversationName, Location location) {

        if (provider == null) return Optional.empty();
        return provider.spawnConversationHost(pluginName, name, conversationName, location);
    }

    public static Conversation getOrStartConversation(Player player) {
        return getActiveConversation(player).orElseGet(() -> provider.startConversation(player));
    }

    public static void askYesNo(Player player, String yes, String no, Consumer<Boolean> result, String... message) {
        create().startStage(stageBuilder -> stageBuilder.withText(message)
                .withAnswer(yes, answerBuilder -> answerBuilder.withAction((type, config) -> result.accept(true)))
                .withAnswer(no, answerBuilder -> answerBuilder.withAction((type, config) -> result.accept(false)))
        ).build().startConversation(player);
    }

    public static void askYesNo(Player player, Consumer<Boolean> result, String... message) {

        askYesNo(player, "Ja", "Nein", result, message);
    }

    /**
     * Starts a new conversation for the given player and prompts him for input.
     *
     * @param player        to ask for input
     * @param text          to display the player
     * @param inputListener to consume the given input
     */
    public static void readLine(Player player, String text, Consumer<String> inputListener) {
        ConversationTemplate conversationTemplate = create()
                .startStage(stageBuilder -> stageBuilder
                        .withInput(text, inputBuilder -> inputBuilder.withInputListener(inputListener))).build();
        conversationTemplate.startConversation(player);
    }

    public static void readLines(Player player, Consumer<String[]> inputListener, String... inputs) {
        if (inputs.length < 1) {
            inputListener.accept(new String[0]);
            return;
        }

        ArrayList<String> output = new ArrayList<>();

        ConversationBuilder conversationBuilder = null;

        for (int i = 0; i < inputs.length; i++) {
            String text = inputs[i];
            String nextStage = i < inputs.length - 1 ? i + 1 + "" : "end";
            if (i == 0) {
                conversationBuilder = create().startStage(stageBuilder -> buildInputStage(stageBuilder, text, nextStage, output));
            } else {
                conversationBuilder.withStage(i + "", stageBuilder -> buildInputStage(stageBuilder, text, nextStage, output));
            }
        }

        conversationBuilder.withConversationEndCallback(conversation -> inputListener.accept(output.toArray(new String[0])));
        ConversationTemplate conversationTemplate = conversationBuilder.withStage("end", stageBuilder -> stageBuilder
                .withAction(Action.endConversation(ConversationEndReason.ENDED)))
                .build();

        conversationTemplate.startConversation(player);
    }

    private static void buildInputStage(StageBuilder stageBuilder, String text, String nextStage, List<String> output) {
        stageBuilder.withInput(text, inputBuilder -> inputBuilder.withInputListener(output::add)
                .withAction(Action.changeStage(nextStage)));
    }

    public static void unloadConversation(String id) {
        if (provider == null) return;
        provider.unregisterConversationTemplate(id);
    }
}
