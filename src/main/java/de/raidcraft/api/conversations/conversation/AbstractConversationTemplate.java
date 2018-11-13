package de.raidcraft.api.conversations.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.host.PlayerHost;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = {"identifier"})
public abstract class AbstractConversationTemplate implements ConversationTemplate {

    private final String identifier;

    private String conversationType = Conversation.DEFAULT_TYPE;
    private boolean persistent = false;
    private boolean autoEnding = true;
    private boolean blockingConversationStart = false;
    private boolean endingOutOfRange = true;
    private boolean exitable = true;
    private int priority = 1;
    private ConfigurationSection hostSettings = new MemoryConfiguration();
    private Consumer<Conversation> conversationEndCallback;

    private final List<Requirement<?>> requirements = new ArrayList<>();
    private final List<Action<?>> actions = new ArrayList<>();
    private final Map<String, StageTemplate> stages = new CaseInsensitiveMap<>();

    public AbstractConversationTemplate(String identifier) {
        this.identifier = identifier;
    }

    public Optional<Consumer<Conversation>> getConversationEndCallback() {
        return Optional.ofNullable(conversationEndCallback);
    }

    @Override
    public Optional<StageTemplate> getStage(String name) {

        return Optional.ofNullable(stages.get(name));
    }

    @Override
    public <TStage extends StageTemplate> TStage addStage(TStage stageTemplate) {

        if (stages.containsKey(stageTemplate.getIdentifier())) {
            StageTemplate existingTemplate = stages.get(stageTemplate.getIdentifier());
            if (stageTemplate.getClass().isInstance(existingTemplate)) {
                return (TStage) existingTemplate;
            }
        }

        stages.put(stageTemplate.getIdentifier(), stageTemplate);
        return stageTemplate;
    }

    @Override
    public Conversation createConversation(Player player, ConversationHost host) {

        return Conversations.createConversation(getConversationType(), player, this, host);
    }

    @Override
    public Conversation startConversation(Player player) {
        return startConversation(player, new PlayerHost(player));
    }

    @Override
    public Conversation startConversation(Player player, ConversationHost host) {

        return startConversation(player, host, null);
    }

    public void setHostSettings(ConfigurationSection hostSettings) {
        if (hostSettings != null) {
            this.hostSettings = hostSettings;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Conversation startConversation(Player player, ConversationHost host, StageTemplate stage) {

        Optional<Conversation> activeConversation = Conversations.removeActiveConversation(player);
        if (activeConversation.isPresent()) {
            if (activeConversation.get().getTemplate().isBlockingConversationStart()
                    || activeConversation.get().getHost().equals(host)
                    || activeConversation.get().getTemplate().equals(this)) {
                Conversations.setActiveConversation(activeConversation.get());
                return activeConversation.get();
            }
            activeConversation.get().abort(ConversationEndReason.START_NEW_CONVERSATION);
        }

        // lets execute all actions of this conversation
        Conversation conversation = createConversation(player, host);
        for (Action<?> action : getActions()) {
            if (conversation.isAbortActionExecution()) break;
            if (ActionAPI.matchesType(action, Player.class)) {
                ((Action<Player>) action).accept(player);
            } else if (ActionAPI.matchesType(action, Conversation.class)) {
                ((Action<Conversation>) action).withPlayer(conversation.getOwner()).accept(conversation);
            }
        }

        if (stage != null) {
            conversation.setCurrentStage(stage.create(conversation));
        }

        // delay the start of the conversation by one tick
        // to allow the creator of the conversation to fill in member variables
        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class), conversation::start, 1L);

        return conversation;
    }

    @Override
    public int compareTo(ConversationTemplate o) {

        return Integer.compare(getPriority(), o.getPriority());
    }
}
