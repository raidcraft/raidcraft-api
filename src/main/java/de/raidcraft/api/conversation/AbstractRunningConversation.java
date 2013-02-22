package de.raidcraft.api.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.conversation.actions.ExitAction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Silthus
 */
public abstract class AbstractRunningConversation<T> implements RunningConversation<T>, Listener {

    private final Conversation<T> conversation;
    private final T conversationPartner;
    private Stage<T> currentStage;

    public AbstractRunningConversation(Conversation<T> conversation, T conversationPartner) {

        this.conversation = conversation;
        this.conversationPartner = conversationPartner;
        setCurrentStage(conversation.getStartStage());
        RaidCraft.getComponent(RaidCraftPlugin.class).registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (event.getPlayer().equals(getConversationPartner())) {
            end(getCurrentStage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        try {
            getCurrentStage().chooseOption(event.getMessage());
        } catch (InvalidChoiceException e) {
            event.getPlayer().sendMessage(e.getMessage());
        }
        event.setCancelled(true);
    }

    private void executeStage(Stage<T> stage) {

        Action<T> exit = null;
        for (Action<T> action : stage.getActions()) {
            if (action.getClass() == ExitAction.class) {
                // execute an exit action last
                exit = action;
            } else {
                action.execute(this);
            }
        }
        if (exit != null) exit.execute(this);
    }

    private void disableEventHooks() {

        HandlerList.unregisterAll(this);
    }

    public Conversation getConversation() {

        return conversation;
    }

    public T getConversationPartner() {

        return conversationPartner;
    }

    @Override
    public Stage<T> getCurrentStage() {

        return currentStage;
    }

    @Override
    public void setCurrentStage(Stage<T> stage) {

        this.currentStage = stage;
    }

    @Override
    public void end(Stage<T> stage) {

        setCurrentStage(stage);
        disableEventHooks();
    }
}
