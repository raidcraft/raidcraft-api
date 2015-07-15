package de.raidcraft.api.conversations.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.DataMap;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.events.RCConversationAbortedEvent;
import de.raidcraft.api.conversations.events.RCConversationChangedStageEvent;
import de.raidcraft.api.conversations.events.RCConversationEndedEvent;
import de.raidcraft.api.conversations.events.RCStartConversationEvent;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractConversation extends DataMap implements Conversation {

    private final Player owner;
    private final ConversationTemplate template;
    private final ConversationHost host;
    private final Map<String, StageTemplate> stages = new CaseInsensitiveMap<>();
    private final Stack<Stage> stageHistory = new Stack<>();
    private Stage currentStage;

    public AbstractConversation(Player owner, ConversationTemplate conversationTemplate, ConversationHost conversationHost) {

        super(new MemoryConfiguration());
        this.owner = owner;
        this.template = conversationTemplate;
        this.host = conversationHost;
    }

    protected abstract void load();

    @Override
    public List<StageTemplate> getStages() {

        return new ArrayList<>(stages.values());
    }

    @Override
    public Optional<Stage> getCurrentStage() {

        if (currentStage == null) {
            return getStage(StageTemplate.START_STAGE);
        }
        return Optional.of(currentStage);
    }

    @Override
    public Conversation setCurrentStage(Stage stage) {

        Optional<Stage> currentStage = getCurrentStage();
        if (currentStage.isPresent()) {
            stageHistory.add(currentStage.get());
        }
        this.currentStage = stage;
        return this;
    }

    @Override
    public Conversation changeToStage(Stage stage) {

        Optional<Stage> currentStage = getCurrentStage();
        setCurrentStage(stage);
        triggerCurrentStage();
        RaidCraft.callEvent(new RCConversationChangedStageEvent(this, currentStage, stage));
        return this;
    }

    @Override
    public Conversation addStage(StageTemplate stage) {

        this.stages.put(stage.getIdentifier(), stage);
        return this;
    }

    @Override
    public Optional<Answer> answer(String answer, boolean executeActions) {

        Optional<Stage> currentStage = getCurrentStage();
        if (currentStage.isPresent()) {
            Optional<Answer> optional = currentStage.get().getAnswer(answer);
            if (optional.isPresent() && executeActions) {
                optional.get().executeActions(this);
            }
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public boolean changePage(int page) {

        Optional<Stage> currentStage = getCurrentStage();
        return currentStage.isPresent() && currentStage.get().changePage(page);
    }

    @Override
    public boolean triggerCurrentStage() {

        Optional<Stage> currentStage = getCurrentStage();
        if (currentStage.isPresent()) {
            currentStage.get().trigger();
            return true;
        }
        return false;
    }

    @Override
    public Optional<Stage> getStage(String identifier) {

        StageTemplate stageTemplate = stages.get(identifier);
        if (stageTemplate != null) {
            return Optional.of(stageTemplate.create(this));
        }
        return Optional.empty();
    }

    @Override
    public boolean start() {

        if (getTemplate().isPersistant()) load();
        Optional<Stage> stage = getCurrentStage();
        if (!stage.isPresent()) stage = getStage(StageTemplate.START_STAGE);
        if (stage.isPresent()) {
            RCStartConversationEvent event = new RCStartConversationEvent(this);
            RaidCraft.callEvent(event);
            if (event.isCancelled()) return false;
            setCurrentStage(stage.get());
            return triggerCurrentStage();
        }
        return false;
    }

    @Override
    public Optional<Stage> end(ConversationEndReason reason) {

        Optional<Stage> currentStage = getCurrentStage();
        if (!currentStage.isPresent()) {
            return Optional.empty();
        }
        RaidCraft.callEvent(new RCConversationEndedEvent(this, reason));
        return currentStage;
    }

    @Override
    public Optional<Stage> abort(ConversationEndReason reason) {

        Optional<Stage> currentStage = getCurrentStage();
        if (!currentStage.isPresent()) {
            return Optional.empty();
        }
        if (getTemplate().isPersistant()) save();
        RaidCraft.callEvent(new RCConversationAbortedEvent(this, reason));
        return currentStage;
    }
}
