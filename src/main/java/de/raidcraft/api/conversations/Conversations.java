package de.raidcraft.api.conversations;

import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import mkremins.fanciful.FancyMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mdoering
 */
public class Conversations {

    public static final Map<UUID, Conversation<Player>> ACTIVE_CONVERSATIONS = new HashMap<>();

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

    public static void registerAnswer(String type, Class<? extends Answer> answer) {

        if (provider == null) {
            queuedAnswers.put(type, answer);
        } else {
            provider.registerAnswer(type, answer);
        }
    }

    public static Optional<Answer> getAnswer(StageTemplate stageTemplate, ConfigurationSection config) {

        if (provider == null) {
            return Optional.empty();
        }
        return provider.getAnswer(stageTemplate, config);
    }

    public static Answer getAnswer(String text) {

        return provider.getAnswer(text);
    }

    public static Answer getAnswer(FancyMessage message) {

        return provider.getAnswer(message);
    }

    public static void registerStage(String type, Class<? extends StageTemplate> stage) {

        if (provider == null) {
            queuedStages.put(type, stage);
        } else {
            provider.registerStage(type, stage);
        }
    }

    public static Optional<StageTemplate> getStageTemplate(String identifier, ConversationTemplate conversationTemplate, ConfigurationSection config) {

        if (provider == null) {
            return Optional.empty();
        }
        return provider.getStageTemplate(identifier, conversationTemplate, config);
    }

    public static void loadConversation(String name, ConfigurationSection config) {

        if (provider == null) {
            queuedConversations.put(name, config);
        } else {
            provider.loadConversation(name, config);
        }
    }
}
