package de.raidcraft.api.conversations;

import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import mkremins.fanciful.FancyMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
     * @see ConversationProvider#startConversation(Player, ConversationHost)
     */
    public static Optional<Conversation<Player>> startConversation(Player player, ConversationHost conversationHost) {

        if (provider == null) return Optional.empty();
        return provider.startConversation(player, conversationHost);
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
}
