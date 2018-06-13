package de.raidcraft.api.conversations.host;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.util.ConfigUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author mdoering
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"uniqueId"})
public abstract class AbstractConversationHost<T> implements ConversationHost<T> {

    private final UUID uniqueId;
    private final Optional<String> identifier;
    private final T type;
    private final List<ConversationTemplate> defaultConversations = new ArrayList<>();
    private final Map<UUID, List<ConversationTemplate>> playerConversations = new HashMap<>();
    private Optional<String> name = Optional.empty();

    public void setName(String name) {

        this.name = Optional.ofNullable(name);
    }

    @Override
    public void load(ConfigurationSection config) {

        if (config.isSet("default-conv")) {
            Optional<ConversationTemplate> template = Conversations.getConversationTemplate(config.getString("default-conv"));
            if (template.isPresent()) {
                addDefaultConversation(template.get());
            } else {
                RaidCraft.LOGGER.warning("Could not find default conversation " + config.getString("default-conv") + " for " + ConfigUtil.getFileName(config));
            }
        }
    }

    @Override
    public void addDefaultConversation(ConversationTemplate conversationTemplate) {

        defaultConversations.add(conversationTemplate);
        defaultConversations.sort(ConversationTemplate::compareTo);
    }

    @Override
    public boolean addTrait(Class<? extends Trait> traitClass) {
        return false;
    }

    @Override
    public boolean addTrait(Trait trait) {
        return false;
    }

    @Override
    public <TTrait extends Trait> Optional<TTrait> getTrait(Class<TTrait> traitClass) {
        return Optional.empty();
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
