package de.raidcraft.api.conversations.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.DataMap;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.events.RCConversationAbortedEvent;
import de.raidcraft.api.conversations.events.RCConversationEndedEvent;
import de.raidcraft.api.conversations.events.RCStartConversationEvent;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractConversation<T> extends DataMap implements Conversation<T> {

    private final T entity;
    private final ConversationTemplate template;
    private final ConversationHost host;
    private final Map<String, Stage> stages = new CaseInsensitiveMap<>();
    private Stage currentStage;

    public AbstractConversation(T entity, ConversationTemplate conversationTemplate, ConversationHost conversationHost) {

        super(new MemoryConfiguration());
        this.entity = entity;
        this.template = conversationTemplate;
        this.host = conversationHost;
    }

    protected abstract void load();

    @Override
    public List<Stage> getStages() {

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
    public Conversation<T> setCurrentStage(Stage stage) {

        this.currentStage = stage;
        return this;
    }

    @Override
    public Conversation<T> addStage(Stage stage) {

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

        return Optional.ofNullable(stages.get(identifier));
    }

    @Override
    public final boolean start() {

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
