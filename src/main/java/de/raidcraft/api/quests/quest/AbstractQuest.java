package de.raidcraft.api.quests.quest;

import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.player.PlayerObjective;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.requirement.Requirement;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Silthus
 */
@Getter
@Setter
public abstract class AbstractQuest implements Quest {

    private final int id;
    private final QuestTemplate template;
    private final QuestHolder holder;
    private final List<PlayerObjective> playerObjectives;
    private final Collection<TriggerFactory> startTrigger;
    private final Collection<TriggerFactory> completionTrigger;

    private Phase phase;
    private Timestamp startTime;
    private Timestamp completionTime;

    public AbstractQuest(int id, QuestTemplate template, QuestHolder holder) {

        this.id = id;
        this.template = template;
        this.holder = holder;
        this.playerObjectives = loadObjectives();
        this.startTrigger = template.getStartTrigger();
        this.completionTrigger = template.getCompletionTrigger();
        registerListeners();
    }

    protected abstract List<PlayerObjective> loadObjectives();

    @Override
    public void processTrigger() {

        boolean meetsRequirements = false;
        if (getTemplate().getRequirements().length > 0) {
            try {
                for (Requirement requirement : getTemplate().getRequirements()) {
                    if (!requirement.isMet(questHolder.getPlayer())) {
                        meetsRequirements = false;
                        break;
                    }
                }
            } catch (QuestException e) {
                meetsRequirements = false;
                getPlayer().sendMessage(ChatColor.RED + e.getMessage());
            }
        } else {
            meetsRequirements = true;
        }
        // dont trigger objectives when no requirements are met
        if (meetsRequirements) {
            for (PlayerObjective playerObjective : getUncompletedObjectives()) {
                playerObjective.trigger(questHolder);

                // abort if we are dealing with ordered required objectives
                if (!playerObjective.getObjective().isOptional() && getTemplate().isOrdered()) {
                    return;
                }
            }
        }
    }

    public void registerListeners() {

        if (!isCompleted() && !isActive()) {
            // register our start trigger
            startTrigger.forEach(factory -> factory.registerListener(this));
        } else if (hasCompletedAllObjectives() && isActive()) {
            // register the completion trigger
            completionTrigger.forEach(factory -> factory.registerListener(this));
        }
    }

    public void unregisterListeners() {

        startTrigger.forEach(factory -> factory.unregisterListener(this));
        completionTrigger.forEach(factory -> factory.unregisterListener(this));
    }

    @Override
    public Player getTriggerEntityType() {

        return getPlayer();
    }

    @Override
    public boolean isCompleted() {

        return completionTime != null;
    }

    @Override
    public boolean isActive() {

        return getStartTime() != null && !isCompleted();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractQuest)) return false;

        AbstractQuest that = (AbstractQuest) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {

        return id;
    }

    @Override
    public String toString() {

        return getTemplate().toString() + "." + getHolder().toString();
    }

    @Override
    public boolean hasCompletedAllObjectives() {

        List<PlayerObjective> uncompletedObjectives = getUncompletedObjectives();
        boolean completed = uncompletedObjectives.isEmpty()
                || (getTemplate().getRequiredObjectiveAmount() > 0
                && getTemplate().getRequiredObjectiveAmount() <= uncompletedObjectives.size());
        if (!uncompletedObjectives.isEmpty() && !completed) {
            int optionalObjectives = 0;
            for (PlayerObjective objective : uncompletedObjectives) {
                if (objective.getObjective().isOptional()) optionalObjectives++;
            }
            if (optionalObjectives == uncompletedObjectives.size()) {
                completed = true;
            }
        }
        return completed;
    }

    @Override
    public List<PlayerObjective> getPlayerObjectives() {

        Collections.sort(playerObjectives);
        return playerObjectives;
    }

    @Override
    public void onObjectCompletion(PlayerObjective objective) {

        getHolder().getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + getTemplate().getFriendlyName() +
                ChatColor.RESET + ": " + ChatColor.DARK_GREEN + "Aufgabe erledigt!");
        getHolder().getPlayer().sendMessage(ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + ChatColor.ITALIC + objective.getObjective().getFriendlyName());
    }

    @Override
    public void start() {

        if (!isActive()) {
            setStartTime(new Timestamp(System.currentTimeMillis()));
            save();
        }
        getHolder().getPlayer().sendMessage(ChatColor.YELLOW + "Quest angenommen: " + ChatColor.GREEN + getFriendlyName());
    }

    @Override
    public void complete() {

        if (!isActive() || !hasCompletedAllObjectives()) {
            return;
        }
        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + getHolder().getPlayer().getName() + " hat die Quest '" +
                ChatColor.GOLD + getFriendlyName() + ChatColor.DARK_GREEN + "' abgeschlossen!");
//        getHolder().getPlayer().sendMessage(ChatColor.YELLOW + "Quest abgeschlossen: " + ChatColor.GREEN + getFriendlyName());
        // complete the quest and trigger the complete actions
        setCompletionTime(new Timestamp(System.currentTimeMillis()));
        // give rewards and execute completion actions
        for (Action<QuestTemplate> action : getTemplate().getCompleteActions()) {
            try {
                action.execute(getHolder(), getTemplate());
            } catch (QuestException e) {
                getPlayer().sendMessage(ChatColor.RED + e.getMessage());
            }
        }
        // unregister ourselves as trigger listener
        for (Trigger trigger : getTemplate().getCompletionTrigger()) {
            trigger.unregisterListener(this);
        }
    }

    @Override
    public void abort() {

        getHolder().abortQuest(this);
        setStartTime(null);
        unregisterListeners();
    }
}
