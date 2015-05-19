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

    void registerAnswer(String type, Class<? extends Answer> answer);

    Optional<Answer> getAnswer(StageTemplate stageTemplate, ConfigurationSection config);

    Answer getAnswer(String text);

    Answer getAnswer(FancyMessage message);

    void registerStage(String type, Class<? extends StageTemplate> stage);

    Optional<StageTemplate> getStageTemplate(String identifier, ConversationTemplate conversationTemplate, ConfigurationSection config);

    void loadConversation(String identifier, ConfigurationSection config);

    Optional<Conversation<Player>> startConversation(Player player, ConversationHost conversationHost);

    Optional<Conversation<Player>> getActiveConversation(Player player);
}
