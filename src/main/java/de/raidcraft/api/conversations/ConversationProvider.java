package de.raidcraft.api.conversations;

import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.StageTemplate;
import mkremins.fanciful.FancyMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
    Optional<ConversationTemplate> getConversationTemplate(String identifier, ConfigurationSection config);

    /**
     * Loads the {@link ConversationTemplate} from the given config with the given identifier.
     * Conversations need to be loaded from disk in order to be available in {@link #getConversationTemplate(String, ConfigurationSection)}.
     * If the template is already registered the registered template will be returned and no new template will be loaded.
     *
     * @param identifier of the template
     * @param config to load template from
     * @return loaded conversation template or empty {@link Optional} if template was not found
     */
    Optional<ConversationTemplate> loadConversation(String identifier, ConfigurationSection config);

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
    Optional<Conversation<Player>> startConversation(Player player, ConversationHost conversationHost);

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
