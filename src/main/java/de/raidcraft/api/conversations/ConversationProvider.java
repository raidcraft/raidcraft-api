package de.raidcraft.api.conversations;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.host.ConversationHostFactory;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public interface ConversationProvider {

    /**
     * Registers the given answer at the conversation host. Registered answer types
     * will be available in the configuration.
     *
     * @param type displayName
     * @param answer type to register
     */
    void registerAnswer(String type, Class<? extends Answer> answer);

    /**
     * Generates a list of answers from the given {@link ConfigurationSection}.
     * The config section can also contain {@link de.raidcraft.api.action.flow.Flow} statements as they will be parsed also.
     * The ConfigurationSection should be a subsection like the following example:
     * <code>
     *     flow:
     *       - :"Flow Answer"
     *       - ^withAction-after-answer
     *       - ?requirement1-for-answer
     *       - :"Flow Input Answer"->flow-input-var1
     *       - ^withAction-with-input withAction-conf:%%
     *     '1':
     *       withText: "Block Answer"
     *       requirements:
     *         ...
     *       actions:
     *         ...
     *     '2':
     *       withText: "Block Input Answer"
     *       var: block-input-var
     *       requirements:
     *         ...
     *       actions:
     *         ...
     * </code>
     *
     * @param template to create answers for
     * @param config to create answers from
     * @return list of created answers
     */
    List<Answer> createAnswers(StageTemplate template, ConfigurationSection config);

    /**
     * Gets a registered answer for the given config section and stage template.
     * Will return an empty optional if not answer with that type is found.
     * If no type is set the {@link Answer#DEFAULT_ANSWER_TEMPLATE} will be used.
     *
     * @param stageTemplate to get answer for
     * @param config to get answer from
     * @return optional answer
     */
    Optional<Answer> getAnswer(StageTemplate stageTemplate, ConfigurationSection config);

    /**
     * Gets an answer based on its type. If not answer for this type is found an empty optional will be returned.
     *
     * @param type of the answer
     * @param config of the answer
     * @return created optional answer
     */
    Optional<Answer> getAnswer(String type, ConfigurationSection config);

    /**
     * Gets a simple answer template with the given withText.
     *
     * @param text of the answer
     * @return simple answer with withText
     */
    Answer getAnswer(String text);

    /**
     * Gets a simple answer with the given {@link FancyMessage}
     *
     * @param message of the answer
     * @return simple answer with fancy message
     */
    Answer getAnswer(FancyMessage message);

    /**
     * Registers the given stage template class. Registered stage templates can be
     * used in the configuration to display custom withText and answers that is dynamically generated.
     *
     * @param type displayName of the stage
     * @param stage type to register
     */
    void registerStageTemplate(String type, Class<? extends StageTemplate> stage);

    /**
     * Gets an instance of a registered {@link StageTemplate} or an empty {@link Optional} if no
     * registered template with the given identifier is found.
     * If no type is set the {@link StageTemplate#DEFAULT_STAGE_TEMPLATE} will be used.
     *
     * @param identifier of the stage template
     * @param conversationTemplate to create {@link StageTemplate} from
     * @param config to create template from
     * @return optional stage template
     */
    Optional<StageTemplate> getStageTemplate(String identifier, ConversationTemplate conversationTemplate, ConfigurationSection config);

    /**
     * Registers the given conversation template with the {@link ConversationProvider}. Registered
     * conversation templates allow the setting if custom variables at the beginning of a conversation.
     *
     * @param type displayName of the conversation template
     * @param conversationTemplate type to register
     */
    void registerConversationTemplate(String type, Class<? extends ConversationTemplate> conversationTemplate);

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
    Optional<ConversationTemplate> createConversationTemplate(String identifier, ConfigurationSection config);

    /**
     * Registers the given Conversation Type with the {@link ConversationProvider}. The conversation will be
     * instantiated from the {@link ConversationTemplate#getConversationType()} id.
     *
     * @param type displayName to register
     * @param conversation to register
     */
    void registerConversationType(String type, Class<? extends Conversation> conversation);

    /**
     * Creates a new {@link Conversation} for the given ConversationType.
     * If the given type is not found an error message will be printed to the log and the default conversation type will be used.
     *
     * @param player that owns the conversation
     * @param template to create conversation from
     * @param host to create conversation with
     * @return created conversation
     */
    Conversation startConversation(String type, Player player, ConversationTemplate template, ConversationHost host);

    /**
     * Registers the host factory with the {@link ConversationProvider} allowing {@link ConversationHost}s to be created.
     * {@link ConversationHostFactory}s can be factories for signs, npcs, etc.
     *
     * @param identifier of the factory
     * @param factory to register
     */
    void registerHostFactory(String identifier, ConversationHostFactory<?> factory);

    /**
     * Creates a conversation host purly based on the config and the plugin.
     *
     * @param plugin that holds the conversation host
     * @param config to create host from
     * @return created host
     */
    default Optional<ConversationHost<?>> createConversationHost(String plugin, ConfigurationSection config) {

        return createConversationHost(plugin, UUID.randomUUID().toString(), config);
    }

    /**
     * Creates a {@link ConversationHost} from the given type at the given location.
     *
     * @param plugin that holds the conversation host
     * @param identifier of the host
     * @param type of the host
     * @param location to create host at
     * @return created {@link ConversationHost}
     */
    Optional<ConversationHost<?>> createConversationHost(String plugin, String identifier, String type, Location location);

    /**
     * Creates a new conversation host from the given config. The config needs to define the type of the host.
     * If no registered host for the type is found an empty {@link Optional} will be returned.
     *
     * @param plugin that holds the conversation host
     * @param identifier of the created host
     * @param config to create host from
     * @return optional host
     */
    Optional<ConversationHost<?>> createConversationHost(String plugin, String identifier, ConfigurationSection config);

    /**
     * Creates a new {@link ConversationHost} from the given type.
     *
     * @param host to create ConversationHost from
     * @param config to create host from
     * @param <T> type of the host
     * @return optional host
     */
    <T> Optional<ConversationHost<T>> createConversationHost(T host, ConfigurationSection config);

    void removeConversationHost(String holder, String id);

    /**
     * Gets a loaded and cached {@link ConversationHost} that has already been created with
     * {@link #createConversationHost(String, ConfigurationSection)}.
     *
     * @param id if the host
     * @return cached host
     */
    Optional<ConversationHost<?>> getConversationHost(String id);

    /**
     * Tries to get a cached conversation host for the given host type.
     *
     * @param host to get conversation host for
     * @param <T> type of the host
     * @return optional cached host
     */
    <T> Optional<ConversationHost<T>> getConversationHost(T host);

    /**
     * Tries to get a cached conversation host and if none exists, tries to create a new conversation host.
     *
     * @param host to get or create
     * @param config to create host from
     * @param <T> type of the host
     * @return optional created or cached host
     */
    <T> Optional<ConversationHost<T>> getOrCreateConversationHost(T host, ConfigurationSection config);

    /**
     * Registers the given variable for all conversation replacements.
     * Registered variables will be replaced when the conversation withText is written.
     *
     * @param pattern of the variable
     * @param variable to register
     */
    void registerConversationVariable(Pattern pattern, ConversationVariable variable);

    /**
     * Gets all registered conversation variables.
     *
     * @return registered variables
     */
    Map<Pattern, ConversationVariable> getConversationVariables();

    /**
     * Loads the {@link ConversationTemplate} from the given config with the given identifier.
     * Conversations need to be loaded from disk in order to be available in {@link #createConversationTemplate(String, ConfigurationSection)}.
     * If the template is already registered the registered template will be returned and no new template will be loaded.
     *
     * @param identifier of the template
     * @param config to load template from
     * @return loaded conversation template or empty {@link Optional} if template was not found
     */
    Optional<ConversationTemplate> loadConversation(String identifier, ConfigurationSection config);

    void unregisterConversationTemplate(String id);

    ConversationTemplate registerConversationTemplate(ConversationTemplate template);

    /**
     * Gets a registered and loaded {@link ConversationTemplate} with the given identifier.
     * If no ConversationTemplate with the identifier is found an empty {@link Optional} will be returned.
     *
     * @param identifier to get conversation template for
     * @return optional {@link ConversationTemplate}
     */
    Optional<ConversationTemplate> getLoadedConversationTemplate(String identifier);

    /**
     * Tries to find the given {@link ConversationTemplate} based on its identifier.
     * It will look thru the loaded conversation templates and will try to find a matching template that
     * ends with the identifier.
     * Will find all matching templates
     *
     * @param identifier to find template for
     * @return list of matching templates or empty list if none found
     */
    List<ConversationTemplate> findConversationTemplate(String identifier);

    /**
     * Starts the {@link ConversationHost#getConversation(Player)} for the player. This will usually be the
     * default conversation of the given host.
     * The started conversation will depend on the priority of the conversations. If the player is already
     * engaged in a conversation the active conversation will be aborted and the new one will be started.
     * If the conversation is the same as the active conversation nothing will change.
     *
     * @param player to startStage conversation for
     * @param conversationHost that started the conversation
     * @return started conversation or an empty optional if the host has no conversations to startStage
     */
    Optional<Conversation> startConversation(Player player, ConversationHost<?> conversationHost);

    /**
     * Starts the given conversation directly for the player using the player as the host.
     * So the conversation will never end because of range problems.
     *
     * @param player to startStage conversation for
     * @param conversation to startStage
     * @return started conversation
     */
    Optional<Conversation> startConversation(Player player, String conversation);

    /**
     * Creates a ready to start conversation from the given {@link Conversation} class.
     * Great for plugins that want to start a specific Conversation directly from a class.
     *
     * @param player            to start conversation for
     * @param conversation      the name of the conversation template to start
     * @param conversationClass to start conversation from
     * @param <TConversation>   type of the conversation
     * @return ready to start conversation
     */
    <TConversation extends Conversation> Optional<TConversation> startConversation(Player player, String conversation, Class<TConversation> conversationClass);

    Conversation startConversation(Player player, ConversationTemplate template, ConversationHost<?> host);

    Conversation startConversation(Player player, ConversationTemplate template);

    Conversation startConversation(Player player);

    /**
     * Gets an active conversation for the player if any are found.
     *
     * @param player to get active conversation for
     * @return optional active conversation
     */
    Optional<Conversation> getActiveConversation(Player player);

    /**
     * Sets the active conversation of the player to the given conversation. If there already is an
     * active conversation it will be aborted.
     * The new conversation will not startStage automatically and {@link Conversation#start()} must be called.
     *
     * @param conversation to set active for the player
     * @return old active conversation
     */
    Optional<Conversation> setActiveConversation(Conversation conversation);

    /**
     * Removes the current active conversation of the player and aborts it.
     *
     * @param player to remove conversation for
     * @return removed conversation
     */
    Optional<Conversation> removeActiveConversation(Player player);

    /**
     * Checks if the player has an active conversation.
     *
     * @param player to check
     * @return true if player has an active conversation
     */
    boolean hasActiveConversation(Player player);

    Stage createStage(Conversation conversation, String text, Answer... answers);

    Answer createAnswer(String text, Action... actions);

    <T extends Answer> Optional<Answer> createAnswer(Class<T> answerClass, Action... actions);

    Optional<ConversationHost<?>> spawnConversationHost(String pluginName, String name, String conversationName, Location location);
}
