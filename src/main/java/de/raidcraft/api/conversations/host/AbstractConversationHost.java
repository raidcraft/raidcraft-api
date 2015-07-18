package de.raidcraft.api.conversations.host;

import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mdoering
 */
@Data
@RequiredArgsConstructor
public abstract class AbstractConversationHost<T> implements ConversationHost<T> {

    private final UUID uniqueId;
    private final T type;
    private final List<ConversationTemplate> defaultConversations = new ArrayList<>();
    private final Map<UUID, List<ConversationTemplate>> playerConversations = new HashMap<>();
    private Optional<String> name;

    public void setName(String name) {

        this.name = Optional.ofNullable(name);
    }

    @Override
    public void load(ConfigurationSection config) {

        if (config.isSet("default-conv")) {
            Optional<ConversationTemplate> template = Conversations.getConversationTemplate(config.getString("default-conv"));
            if (template.isPresent()) {
                addDefaultConversation(template.get());
            }
        }
    }

    @Override
    public void addDefaultConversation(ConversationTemplate conversationTemplate) {

        defaultConversations.add(conversationTemplate);
        defaultConversations.sort(ConversationTemplate::compareTo);
    }

    @Override
    public Optional<ConversationTemplate> getDefaultConversation() {

        if (defaultConversations.isEmpty()) return Optional.empty();
        return Optional.of(defaultConversations.get(0));
    }

    @Override
    public List<ConversationTemplate> getDefaultConversations() {

        return new ArrayList<>(defaultConversations);
    }

    @Override
    public Optional<ConversationTemplate> getConversation(@NonNull Player player) {

        if (playerConversations.containsKey(player.getUniqueId()) && !playerConversations.get(player.getUniqueId()).isEmpty()) {
            return Optional.of(playerConversations.get(player.getUniqueId()).get(0));
        }
        return getDefaultConversation();
    }

    @Override
    public List<ConversationTemplate> getPlayerConversations(@NonNull Player player) {

        return new ArrayList<>(playerConversations.getOrDefault(player.getUniqueId(), new ArrayList<>()));
    }

    @Override
    public void setConversation(@NonNull UUID player, @NonNull ConversationTemplate conversation) {

        if (!playerConversations.containsKey(player)) {
            playerConversations.put(player, new ArrayList<>());
        }
        List<ConversationTemplate> templates = playerConversations.get(player);
        templates.add(conversation);
        templates.sort(ConversationTemplate::compareTo);
    }

    @Override
    public boolean unsetConversation(@NonNull Player player, ConversationTemplate conversation) {

        return playerConversations.getOrDefault(player.getUniqueId(), new ArrayList<>()).remove(conversation);
    }

    @Override
    public Optional<Conversation> startConversation(Player player) {

        Optional<ConversationTemplate> conversation = getConversation(player);
        if (conversation.isPresent()) {
            return Optional.of(conversation.get().startConversation(player, this));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Conversation> startConversation(Player player, String conversation) {

        Optional<ConversationTemplate> template = Conversations.getConversationTemplate(conversation);
        if (!template.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(template.get().startConversation(player, this));
    }
}
