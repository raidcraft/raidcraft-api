package de.raidcraft.api.conversations.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.ActionConfigWrapper;
import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.config.DataMap;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.events.RCConversationAbortedEvent;
import de.raidcraft.api.conversations.events.RCConversationChangedStageEvent;
import de.raidcraft.api.conversations.events.RCConversationEndedEvent;
import de.raidcraft.api.conversations.events.RCConversationStartEvent;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.fanciful.FancyMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = false, of = {"owner", "template", "host"})
public abstract class AbstractConversation extends DataMap implements Conversation {

    private final Player owner;
    private final ConversationTemplate template;
    private final ConversationHost host;
    private final Map<String, StageTemplate> stages;
    private final Stack<Stage> stageHistory = new Stack<>();
    private String lastInput;
    private Stage currentStage;
    private boolean abortActionExecution = false;

    public AbstractConversation(Player owner, ConversationTemplate conversationTemplate, ConversationHost conversationHost) {

        super(new MemoryConfiguration());
        this.owner = owner;
        this.template = conversationTemplate;
        this.host = conversationHost;
        this.stages = conversationTemplate.getStages();
    }

    protected abstract void load();

    /**
     * On Start is called just before the first stage is triggered and after the conversation is created.
     * Override the method to implement custom setup logic before the first stage starts.
     * You can still add stages and answers to the conversation dynamically building it.
     *
     * @return true to proceed with starting the conversation or false to cancel it
     */
    protected boolean onStart() {
        return true;
    }

    /**
     * On End is called just before the conversation is unregistered and destroyed.
     * Override the method to do cleanup and other custom logic when a conversation ends.
     *
     * @param reason the reason the conversation ended
     */
    protected void onEnd(ConversationEndReason reason) {
    }

    @Override
    public void abortActionExection() {

        abortActionExecution = true;
        getCurrentStage().ifPresent(Stage::abortActionExecution);
    }

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
    public Optional<Stage> getPreviousStage() {

        if (getStageHistory().size() > 1) {
            Stage topStage = getStageHistory().pop();
            Stage previousStage = getStageHistory().peek();
            getStageHistory().push(topStage);

            return Optional.of(previousStage);
        }

        return Optional.empty();
    }

    @Override
    public Conversation setCurrentStage(Stage stage) {

        Optional<Stage> currentStage = getCurrentStage();
        currentStage.ifPresent(stageHistory::add);
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
    public Conversation changeToStage(StageTemplate template) {

        return changeToStage(createStage(template));
    }

    @Override
    public Conversation changeToPreviousStage() {

        Optional<Stage> previousStage = getPreviousStage();

        if (previousStage.isPresent()) {
            changeToStage(previousStage.get());
        } else {
            abort(ConversationEndReason.ENDED);
        }
        return this;
    }

    @Override
    public Conversation addStage(StageTemplate stage) {

        this.stages.put(stage.getIdentifier(), stage);
        return this;
    }

    @Override
    public Optional<Answer> answer(String input, boolean executeActions) {

        Optional<Stage> stage = getCurrentStage();
        if (!stage.isPresent()) {
            return Optional.empty();
        }

        Optional<Answer> answer = stage.get().processAnswer(input);
        if (answer.isPresent() && executeActions) {
            answer.get().executeActions(this);
        }

        boolean changedStage = getCurrentStage().map(nextStage -> !nextStage.equals(stage.get())).orElse(false);

        if (getTemplate().isAutoEnding() && answer.isPresent() && !changedStage) {
            long delay = answer.map(ActionHolder::getActions)
                    .map(actions -> actions.stream()
                            .filter(action -> action instanceof ActionConfigWrapper)
                            .reduce((first, second) -> second)
                            .orElse(null))
                    .map(action -> ((ActionConfigWrapper<?>) action).getDelay())
                    .orElse(0L);
            if (delay > 0) {
                Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class), () -> end(ConversationEndReason.SILENT), delay);
            } else {
                // auto end the conversation if no other stages are present
                // and after the player had a chance to answer
                end(ConversationEndReason.SILENT);
            }

        }
        return answer;
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
    public Location getLocation() {
        return getHost().getLocation();
    }

    @Override
    public Optional<Stage> getStage(String identifier) {

        StageTemplate stageTemplate = stages.get(identifier);
        if (stageTemplate != null) {
            return Optional.of(createStage(stageTemplate));
        }
        return Optional.empty();
    }

    protected Stage createStage(StageTemplate template) {
        return template.create(this);
    }

    @Override
    public Optional<String> getLastInput() {

        return Optional.ofNullable(lastInput);
    }

    @Override
    public Conversation sendMessage(String... lines) {

        for (String line : lines) {
            getOwner().sendMessage(RaidCraft.replaceVariables(getOwner(), line));
        }
        return this;
    }

    @Override
    public Conversation sendMessage(FancyMessage... lines) {

        for (FancyMessage line : lines) {
            line.send(getOwner());
        }
        return this;
    }

    @Override
    public boolean start() {

        if (getTemplate().isPersistent()) load();
        if (!onStart()) return false;

        Optional<Stage> stage = getCurrentStage();
        if (!stage.isPresent()) stage = getStage(StageTemplate.START_STAGE);
        if (stage.isPresent()) {
            RCConversationStartEvent event = new RCConversationStartEvent(this);
            RaidCraft.callEvent(event);
            if (event.isCancelled()) return false;
            setCurrentStage(stage.get());
            Conversations.setActiveConversation(this);
            if (triggerCurrentStage()) {
                return true;
            } else {
                Conversations.removeActiveConversation(getOwner());
                return false;
            }
        }
        return false;
    }

    @Override
    public Optional<Stage> end(ConversationEndReason reason) {

        Optional<Stage> currentStage = getCurrentStage();
        if (!currentStage.isPresent()) {
            return Optional.empty();
        }
        switch (reason) {
            case ERROR:
            case DEATH:
            case PLAYER_QUIT:
            case OUT_OF_RANGE:
            case PLAYER_CHANGED_WORLD:
            case START_NEW_CONVERSATION:
            case PLAYER_ABORT:
                abortActionExection();
        }
        onEnd(reason);
        Conversations.removeActiveConversation(getOwner());
        getTemplate().getConversationEndCallback().ifPresent(callback -> callback.accept(this));
        RaidCraft.callEvent(new RCConversationEndedEvent(this, reason));
        return currentStage;
    }

    @Override
    public Optional<Stage> abort(ConversationEndReason reason) {

        Optional<Stage> currentStage = getCurrentStage();
        if (!currentStage.isPresent()) {
            return Optional.empty();
        }
        if (getTemplate().isPersistent()) save();

        abortActionExection();

        Conversations.removeActiveConversation(getOwner());
        getTemplate().getConversationEndCallback().ifPresent(callback -> callback.accept(this));
        RaidCraft.callEvent(new RCConversationAbortedEvent(this, reason));
        return currentStage;
    }
}
