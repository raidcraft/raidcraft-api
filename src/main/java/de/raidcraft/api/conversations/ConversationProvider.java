package de.raidcraft.api.conversations;

import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.StageTemplate;
import mkremins.fanciful.FancyMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface ConversationProvider {

    /**
     * Registers the given answer at the conversation host. Registered answer types
     * will be available in the configuration.
     *
     * @param type name
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
     *       - ^action-after-answer
     *       - ?requirement1-for-answer
     *       - :"Flow Input Answer"->flow-input-var1
     *       - ^action-with-input action-conf:%%
     *     '1':
     *       text: "Block Answer"
     *       requirements:
     *         ...
     *       actions:
     *         ...
     *     '2':
     *       text: "Block Input Answer"
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
     * Gets a simple answer template with the given text.
     *
     * @param text of the answer
     * @return simple answer with text
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
     * used in the configuration to display custom text and answers that is dynamically generated.
     *
     * @param type name of the stage
     * @param stage type to register
     */
    void registerStage(String type, Class<? extends StageTemplate> stage);

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
     * @param type name of the conversation template
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
     * Registers the given conversation host for the given type. The registered conversation host
     * must have a constructor that takes {@link Class} as the first parameter and a {@link ConfigurationSection}
     * as the second parameter.
     *
     * @param type to register
     * @param host class to register
     */
    void registerConversationHost(Class<?> type, Class<? extends ConversationHost<?>> host);

    /**
     * Gets a registered {@link ConversationHost} for the given type. A new host will be instantiated.
     * If no registered host for the type is found an empty {@link Optional} will be returned.
     *
     * @param identifier of the host
     * @param type to get host for
     * @param config to load host with
     * @param <T> type of the host
     * @return optional conversation host
     */
    <T> Optional<ConversationHost<T>> createConversationHost(String identifier, T type, ConfigurationSection config);

    /**
     * Gets a loaded and cached {@link ConversationHost} that has already been created with
     * {@link #createConversationHost(String, Object, ConfigurationSection)}.
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
     * Registered variables will be replaced when the conversation text is written.
     *
     * @param name of the variable
     * @param variable to register
     */
    void registerConversationVariable(String name, ConversationVariable variable);

    /**
     * Gets all registered conversation variables.
     *
     * @return registered variables
     */
    Map<String, ConversationVariable> getConversationVariables();

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
     * @param player to start conversation for
     * @param conversationHost that started the conversation
     * @return started conversation or an empty optional if the host has no conversations to start
     */
    Optional<Conversation<Player>> startConversation(Player player, ConversationHost<?> conversationHost);

    /**
     * Gets an active conversation for the player if any are found.
     *
     * @param player to get active conversation for
     * @return optional active conversation
     */
    Optional<Conversation<Player>> getActiveConversation(Player player);

    /**
     * Sets the active conversation of the player to the given conversation. If there already is an
     * active conversation it will be aborted.
     * The new conversation will not start automatically and {@link Conversation#start()} must be called.
     *
     * @param conversation to set active for the player
     * @return old active conversation
     */
    Optional<Conversation<Player>> setActiveConversation(Conversation<Player> conversation);

    /**
     * Removes the current active conversation of the player and aborts it.
     *
     * @param player to remove conversation for
     * @return removed conversation
     */
    Optional<Conversation<Player>> removeActiveConversation(Player player);

    /**
     * Checks if the player has an active conversation.
     *
     * @param player to check
     * @return true if player has an active conversation
     */
    boolean hasActiveConversation(Player player);
}
